package net.minecraftforge.fml.relauncher;

/**
 * Patched version of Side for the dev environment.
 * The ForgeGradle 3.0 mapped jar adds a spurious BUKKIT constant to this enum
 * which causes NetworkRegistry.newChannel() to NPE (it initializes channels only
 * for CLIENT and SERVER, but iterates Side.values() which includes BUKKIT).
 * The production Forge jar only has CLIENT and SERVER — this matches that.
 * This file is excluded from the production jar via build.gradle.
 */
public enum Side {

    CLIENT,
    SERVER;

    public boolean isServer() {
        return !isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
