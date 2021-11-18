package com.BrotherHoodOfDiethylamide.OrangeSunshine.network;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OrangeSunshine.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        register(DrugCapSync.class, DrugCapSync::new);
        register(ActiveDrugCapSync.class, ActiveDrugCapSync::new);
    }

    private static int id = 0;
    private static <T extends IMessage> void register(Class<T> msg, Function<PacketBuffer, T> decoder) {
        INSTANCE.registerMessage(id++, msg, IMessage::encode, decoder, IMessage::handle);
    }
}
