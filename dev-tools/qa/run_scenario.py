#!/usr/bin/env python3
"""
Run lightweight scenario checks against a running Minecraft server via RCON.
"""

from __future__ import annotations

import argparse
import json
import re
import socket
import struct
import sys
import time
from pathlib import Path
from typing import List, Optional

SERVERDATA_RESPONSE_VALUE = 0
SERVERDATA_AUTH_RESPONSE = 2
SERVERDATA_AUTH = 3
SERVERDATA_EXECCOMMAND = 2
DEFAULT_TIMEOUT = 12.0


def _pack_packet(req_id: int, ptype: int, body: str) -> bytes:
    payload = body.encode("utf-8") + b"\x00\x00"
    header = struct.pack("<ii", req_id, ptype)
    return struct.pack("<i", len(header) + len(payload)) + header + payload


def _unpack_packet(raw: bytes):
    size = struct.unpack("<i", raw[:4])[0]
    req_id, ptype = struct.unpack("<ii", raw[4:12])
    body = raw[12 : 4 + size - 2].decode("utf-8", errors="replace")
    return req_id, ptype, body


class RconClient:
    def __init__(self, host: str, port: int, password: str, timeout: float = DEFAULT_TIMEOUT):
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.settimeout(timeout)
        self._socket.connect((host, port))
        self._next_id = 1
        self._auth(password)

    def _auth(self, password: str):
        self._next_id += 1
        req_id = self._next_id
        self._send(req_id, SERVERDATA_AUTH, password)
        response_id, response_type, _ = self._recv_packet()
        if response_id != req_id or response_type != SERVERDATA_AUTH_RESPONSE:
            raise ConnectionError("RCON authentication failed")

    def _send(self, req_id: int, packet_type: int, body: str):
        self._socket.sendall(_pack_packet(req_id, packet_type, body))

    def _recv_exact(self, count: int) -> bytes:
        data = b""
        while len(data) < count:
            chunk = self._socket.recv(count - len(data))
            if not chunk:
                raise ConnectionError("RCON socket closed unexpectedly")
            data += chunk
        return data

    def _recv_packet(self):
        raw_size = self._recv_exact(4)
        size = struct.unpack("<i", raw_size)[0]
        raw_packet = raw_size + self._recv_exact(size)
        return _unpack_packet(raw_packet)

    def command(self, command: str) -> str:
        self._next_id += 1
        req_id = self._next_id
        self._send(req_id, SERVERDATA_EXECCOMMAND, command)

        parts: List[str] = []
        timeout_at = time.monotonic() + (self._socket.gettimeout() or DEFAULT_TIMEOUT)

        while True:
            if time.monotonic() > timeout_at and not parts:
                raise TimeoutError(f"Timed out waiting for command response: {command}")

            remaining = timeout_at - time.monotonic()
            if remaining < 0.1:
                remaining = 0.1
            self._socket.settimeout(remaining)

            rid, ptype, body = self._recv_packet()
            if rid != req_id:
                continue
            if ptype != SERVERDATA_RESPONSE_VALUE:
                continue
            parts.append(body)
            if body == "":
                break
            # Most RCON responses end with an empty frame.
            if body.endswith("\n") and remaining <= 0.2:
                break
        return "\n".join(parts).strip()

    def close(self):
        self._socket.close()


class StepResult:
    def __init__(self, step: dict, output: str, passed: bool, details: List[str]):
        self.step = step
        self.output = output
        self.passed = passed
        self.details = details

    def to_dict(self):
        return {
            "id": self.step.get("id"),
            "command": self.step.get("command"),
            "expects": self.step.get("expects", {}),
            "output": self.output,
            "passed": self.passed,
            "details": self.details,
        }


def _matches_any(patterns: List[str], text: str) -> bool:
    return any(re.search(p, text, re.IGNORECASE | re.DOTALL) for p in patterns)


def run_step(client: RconClient, step: dict) -> StepResult:
    sid = step.get("id", "unnamed")
    command = str(step.get("command", ""))
    delay = float(step.get("delay", 0))
    details: List[str] = []

    if delay > 0:
        time.sleep(delay)

    output = client.command(command)

    must_match = step.get("must_match")
    must_not_match = step.get("must_not_match")
    acceptable = step.get("acceptable") or []
    if isinstance(acceptable, str):
        acceptable = [acceptable]

    passed = True

    if must_match and not re.search(must_match, output, re.IGNORECASE | re.DOTALL):
        passed = False
        details.append(f"missing expected match: {must_match}")

    if must_not_match and re.search(must_not_match, output, re.IGNORECASE | re.DOTALL):
        passed = False
        details.append(f"forbidden output matched: {must_not_match}")

    if acceptable:
        if not _matches_any(acceptable, output):
            passed = False
            details.append("output did not match any acceptable patterns")

    if passed and not (must_match or must_not_match or acceptable):
        default_bad = [
            r"unknown or invalid",
            r"unknown command",
            r"not.*found",
            r"no such",
            r"cannot be used",
            r"Could not parse",
            r"Invalid",
            r"not.*allowed",
        ]
        if _matches_any(default_bad, output):
            passed = False
            details.append("default failure pattern detected")

    status = "PASS" if passed else "FAIL"
    print(f"[{status}] {sid}: {command}")
    print(f"  output: {output}")
    for item in details:
        print(f"  detail: {item}")

    return StepResult(step=step, output=output, passed=passed, details=details)


def run_scenario(path: Path, host: str, port: int, password: str, report: Optional[Path], loader: str = "fabric") -> int:
    data = json.loads(path.read_text(encoding="utf-8"))
    steps = data.get("steps", [])
    if not isinstance(steps, list):
        raise ValueError("Scenario file missing 'steps' list")

    client = RconClient(host=host, port=port, password=password)
    scenario_result = {
        "loader": loader,
        "scenario": data.get("name", path.stem),
        "scenario_file": str(path),
        "steps": [],
        "passed": True,
    }

    print(f"[scenario] {scenario_result['scenario']} ({loader})")

    for step in steps:
        result = run_step(client, step)
        scenario_result["steps"].append(result.to_dict())
        if not result.passed:
            scenario_result["passed"] = False

    client.close()

    print(f"[scenario] result: {'PASS' if scenario_result['passed'] else 'FAIL'}")

    if report is not None:
        report.parent.mkdir(parents=True, exist_ok=True)
        report.write_text(json.dumps(scenario_result, indent=2), encoding="utf-8")

    return 0 if scenario_result["passed"] else 1


def main(argv: Optional[List[str]] = None) -> int:
    parser = argparse.ArgumentParser(description="Run an RCON scenario file")
    parser.add_argument("--loader", required=False, default="fabric")
    parser.add_argument("--scenario", required=True)
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=25575)
    parser.add_argument("--password", default="devtest")
    parser.add_argument("--report", default="")
    args = parser.parse_args(argv)

    report = Path(args.report) if args.report else None

    try:
        return run_scenario(
            path=Path(args.scenario),
            host=args.host,
            port=args.port,
            password=args.password,
            report=report,
            loader=args.loader,
        )
    except Exception as exc:  # pragma: no cover
        print(f"[scenario] failed: {exc}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
