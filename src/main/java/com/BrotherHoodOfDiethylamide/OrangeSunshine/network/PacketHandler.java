package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int id = 0;

    public static void init() {
        OrangeSunshine.network.registerMessage(DrugCapSyncMessage.Handler.class, DrugCapSyncMessage.class, id++, Side.CLIENT);
        OrangeSunshine.network.registerMessage(ActiveDrugCapSyncMessage.Handler.class, ActiveDrugCapSyncMessage.class, id++, Side.CLIENT);
    }
}
