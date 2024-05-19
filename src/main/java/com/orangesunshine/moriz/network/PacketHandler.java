package com.orangesunshine.moriz.network;

import com.mojang.brigadier.Message;
import com.orangesunshine.moriz.OrangeSunshine;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(OrangeSunshine.MODID, "main")
    )
            .serverAcceptedVersions(((status, version) -> true))
            .clientAcceptedVersions(((status, version) -> true))
            .networkProtocolVersion(1)
            .simpleChannel();

    private static <T extends Message> void register() {
        INSTANCE.messageBuilder(DrugCapSync.class, 0)
                .encoder(DrugCapSync::encode)
                .decoder(DrugCapSync::new)
                .consumerMainThread(DrugCapSync::handle)
                .add();
        INSTANCE.messageBuilder(ActiveDrugCapSync.class, 1)
                .encoder(ActiveDrugCapSync::encode)
                .decoder(ActiveDrugCapSync::new)
                .consumerMainThread(ActiveDrugCapSync::handle)
                .add();
    }
}
