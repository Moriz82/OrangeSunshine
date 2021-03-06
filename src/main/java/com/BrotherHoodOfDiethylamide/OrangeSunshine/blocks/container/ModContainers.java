package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static DeferredRegister<ContainerType<?>> CONTAINERS
            = DeferredRegister.create(ForgeRegistries.CONTAINERS, OrangeSunshine.MOD_ID);

    public static final RegistryObject<ContainerType<DryingTableContainer>> DRYING_TABLE_CONTAINER
            = CONTAINERS.register("drying_table_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new DryingTableContainer(windowId, world, pos, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<FridgeContainer>> FRIDGE_CONTAINER
            = CONTAINERS.register("fridge_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new FridgeContainer(windowId, world, pos, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<CompoundCompressorContainer>> COMPOUND_COMPRESSOR_CONTAINER
            = CONTAINERS.register("compound_compressor_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new CompoundCompressorContainer(windowId, world, pos, inv, inv.player);
            })));

    public static final RegistryObject<ContainerType<CompoundExtractorContainer>> COMPOUND_EXTRACTOR_CONTAINER
            = CONTAINERS.register("compound_extractor_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new CompoundExtractorContainer(windowId, world, pos, inv, inv.player);
            })));


    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}