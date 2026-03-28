#!/usr/bin/env python3
"""
Create a superflat creative test world for the OrangeSunshine client test pipeline.

Copies an existing world's level.dat and patches it to be creative mode with cheats
enabled, or creates the world save directory from an existing world template.

Usage:
    python3 dev-tools/create_test_world.py [--loader fabric] [--force]
"""

import argparse
import gzip
import os
import shutil
import struct
import sys


def patch_nbt_int(data: bytes, tag_name: str, new_value: int) -> bytes:
    """Patch a named Int tag (type 3) in raw NBT data."""
    name_bytes = tag_name.encode("utf-8")
    idx = data.find(name_bytes)
    if idx < 0:
        return data
    # Int tag: after the name comes a 4-byte big-endian int
    val_start = idx + len(name_bytes)
    return data[:val_start] + struct.pack(">i", new_value) + data[val_start + 4:]


def patch_nbt_byte(data: bytes, tag_name: str, new_value: int) -> bytes:
    """Patch a named Byte tag (type 1) in raw NBT data."""
    name_bytes = tag_name.encode("utf-8")
    idx = data.find(name_bytes)
    if idx < 0:
        return data
    val_start = idx + len(name_bytes)
    return data[:val_start] + struct.pack(">b", new_value) + data[val_start + 1:]


def patch_nbt_string(data: bytes, tag_name: str, old_value: str, new_value: str) -> bytes:
    """Replace a named String tag value if it matches old_value."""
    name_bytes = tag_name.encode("utf-8")
    idx = data.find(name_bytes)
    if idx < 0:
        return data
    # String tag: after name comes 2-byte length + string bytes
    str_len_start = idx + len(name_bytes)
    old_len = struct.unpack(">H", data[str_len_start:str_len_start + 2])[0]
    actual = data[str_len_start + 2:str_len_start + 2 + old_len].decode("utf-8", errors="replace")
    if actual != old_value:
        return data
    new_bytes = new_value.encode("utf-8")
    return (data[:str_len_start]
            + struct.pack(">H", len(new_bytes))
            + new_bytes
            + data[str_len_start + 2 + old_len:])


def create_test_world(loader: str, force: bool = False):
    root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    saves_dir = os.path.join(root, loader, "run", "saves")
    test_world = os.path.join(saves_dir, "OrangeSunshine_Test")
    source_world = None

    # Find a source world to copy from (check current loader, then fall back to fabric)
    search_dirs = [saves_dir]
    if loader != "fabric":
        search_dirs.append(os.path.join(root, "fabric", "run", "saves"))
    for search_dir in search_dirs:
        if os.path.isdir(search_dir):
            for entry in os.listdir(search_dir):
                candidate = os.path.join(search_dir, entry)
                if os.path.isfile(os.path.join(candidate, "level.dat")) and entry != "OrangeSunshine_Test":
                    source_world = candidate
                    break
        if source_world:
            break

    if os.path.isdir(test_world) and not force:
        print(f"Test world already exists: {test_world}")
        return test_world

    if os.path.isdir(test_world):
        shutil.rmtree(test_world)

    if source_world:
        print(f"Copying world from: {source_world}")
        shutil.copytree(source_world, test_world)

        # Patch level.dat for creative + cheats
        level_dat = os.path.join(test_world, "level.dat")
        with gzip.open(level_dat, "rb") as f:
            data = f.read()

        data = patch_nbt_int(data, "GameType", 1)            # Creative
        data = patch_nbt_byte(data, "allowCommands", 1)       # Cheats on
        data = patch_nbt_byte(data, "hardcore", 0)            # Not hardcore
        data = patch_nbt_int(data, "Difficulty", 0)           # Peaceful
        data = patch_nbt_int(data, "rainTime", 999999)        # No rain
        data = patch_nbt_byte(data, "raining", 0)             # Clear weather
        data = patch_nbt_byte(data, "thundering", 0)          # No thunder

        # Rename
        for old_name in ["New World", "New World (1)"]:
            data = patch_nbt_string(data, "LevelName", old_name, "OrangeSunshine_Test")

        with gzip.open(level_dat, "wb") as f:
            f.write(data)

        print(f"Test world created: {test_world}")
        print("  Mode: Creative, Cheats: ON, Difficulty: Peaceful")
    else:
        # No source world - create minimal directory structure
        os.makedirs(test_world, exist_ok=True)
        print(f"No source world found in {saves_dir}")
        print("Launch the game once to create a world, then re-run this script.")
        return None

    return test_world


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--loader", default="fabric")
    parser.add_argument("--force", action="store_true", help="Recreate even if exists")
    args = parser.parse_args()
    result = create_test_world(args.loader, args.force)
    if result:
        print(f"\nReady. Launch with: --quickPlaySingleplayer \"OrangeSunshine_Test\"")
    else:
        sys.exit(1)
