package com.BrotherHoodOfDiethylamide.OrangeSunshine.proxy;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.DrugEffectsBridge;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DrugEffectsBridge());
        // DrugDebugOverlay is auto-registered via @Mod.EventBusSubscriber
    }
}
