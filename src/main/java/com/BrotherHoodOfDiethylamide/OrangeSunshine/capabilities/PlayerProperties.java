package com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PlayerProperties {
    @CapabilityInject(IPlayerDrugs.class)
    public static Capability<IPlayerDrugs> PLAYER_DRUGS;

    public static IPlayerDrugs getPlayerDrugs(EntityPlayer player) {
        IPlayerDrugs drugs = player.getCapability(PLAYER_DRUGS, null);
        if (drugs != null) return drugs;
        return PLAYER_DRUGS.getDefaultInstance();
    }

    public static void register() {
        PlayerDrugs.register();
    }
}
