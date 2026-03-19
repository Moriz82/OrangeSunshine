package moriz.orangesunshine.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

interface BlockConstructionUtils {
    static BlockBehaviour.Properties leaves(SoundType soundType) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(soundType)
                .noOcclusion()
                .isValidSpawn(BlockConstructionUtils::canSpawnOnLeaves)
                .isSuffocating(BlockConstructionUtils::never)
                .isViewBlocking(BlockConstructionUtils::never)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY)
                .forceSolidOff();
    }

    static BarrelBlock barrel(MapColor mapColor, ResourceKey<Block> id) {
        return new BarrelBlock(BlockBehaviour.Properties.of()
                .setId(id)
                .mapColor(mapColor)
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WOOD)
                .strength(2)
                .ignitedByLava()
                .pushReaction(PushReaction.BLOCK));
    }

    static RotatedPillarBlock log(MapColor topColor, MapColor sideColor, ResourceKey<Block> id) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of()
                .setId(id)
                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava());
    }

    static BlockBehaviour.Properties plant(SoundType soundType) {
        return BlockBehaviour.Properties.of()
                .randomTicks()
                .mapColor(MapColor.PLANT)
                .noCollision()
                .instabreak()
                .sound(soundType)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);
    }

    static FlowerPotBlock pottedPlant(Block flower, ResourceKey<Block> id, FeatureFlag... requiredFeatures) {
        BlockBehaviour.Properties settings = BlockBehaviour.Properties.of()
                .setId(id)
                .instabreak()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
        if (requiredFeatures.length > 0) {
            settings = settings.requiredFeatures(requiredFeatures);
        }
        return new FlowerPotBlock(flower, settings);
    }

    static ButtonBlock woodenButton(BlockSetType blockSetType, ResourceKey<Block> id, FeatureFlag... requiredFeatures) {
        BlockBehaviour.Properties settings = BlockBehaviour.Properties.of()
                .setId(id)
                .noCollision()
                .strength(0.5F)
                .pushReaction(PushReaction.DESTROY);
        if (requiredFeatures.length > 0) {
            settings = settings.requiredFeatures(requiredFeatures);
        }
        return new ButtonBlock(blockSetType, 30, settings) {
        };
    }

    static DoorBlock woodenDoor(BlockSetType blockSetType, BlockBehaviour.Properties settings) {
        return new DoorBlock(blockSetType, settings) {
        };
    }

    static PressurePlateBlock woodenPressurePlate(BlockSetType blockSetType, BlockBehaviour.Properties settings) {
        return new PressurePlateBlock(blockSetType, settings) {
        };
    }

    static TrapDoorBlock woodenTrapdoor(BlockSetType blockSetType, BlockBehaviour.Properties settings) {
        return new TrapDoorBlock(blockSetType, settings) {
        };
    }

    static boolean never(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    static boolean always(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return true;
    }

    static boolean always(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    static boolean canSpawnOnLeaves(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }
}
