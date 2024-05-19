package com.orangesunshine.moriz.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Objects;

public class PlayerProperties {
    static Capability<IPlayerDrugs> PLAYER_DRUGS;

    public static IPlayerDrugs getPlayerDrugs(Player player) {
        return player.getCapability(PLAYER_DRUGS, null).orElse(new PlayerDrugs.Implementation());
    }

    public static void register() {
        PlayerDrugs.register();
    }
}