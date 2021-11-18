package com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Objects;

public class PlayerProperties {
    @CapabilityInject(IPlayerDrugs.class)
    static Capability<IPlayerDrugs> PLAYER_DRUGS;

    public static IPlayerDrugs getPlayerDrugs(PlayerEntity player) {
        return player.getCapability(PLAYER_DRUGS, null).orElse(Objects.requireNonNull(PLAYER_DRUGS.getDefaultInstance()));
    }

    public static void register() {
        PlayerDrugs.register();
    }
}
