/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.block.entity.MashTubBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.fluid.FluidVolumes;
import moriz.orangesunshine.fluid.SimpleFluid;
import moriz.orangesunshine.fluid.container.FluidContainer;
import moriz.orangesunshine.fluid.container.Resovoir;
import moriz.orangesunshine.item.MashTubItem;
import moriz.orangesunshine.screen.FluidContraptionScreenHandler;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Created by lukas on 27.10.14.
 * Updated by Sollace on 12 Jan 2023
 */
public class MashTubBlock extends BlockWithFluid<MashTubBlockEntity> implements LiquidBlockContainer {
    public static final MapCodec<MashTubBlock> CODEC = simpleCodec(MashTubBlock::new);
    public static final int SIZE = 15;
    public static final int BORDER_SIZE = 1;
    public static final int HEIGHT = 16;

    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;

    static final VoxelShape COLLISSION_SHAPE = Shapes.or(
            createShape(-8, -0.5F, -8, 32, 16, 1),
            createShape(-8, -0.5F, 23, 32, 16, 1),
            createShape(23, -0.5F, -8, 1, 16, 32),
            createShape(-8, -0.5F, -8, 1, 16, 32),
            createShape(-8, -0.5F, -8, 32, 1, 32)
    );
    static final VoxelShape RAYCAST_SHAPE = createShape(-8, -0.5F, -8, 32, 16, 32);

    private static VoxelShape createShape(double x, double y, double z, double width, double height, double depth) {
        return Block.box(x, y, z, x + width, y + height, z + depth);
    }

    public MashTubBlock(BlockBehaviour.Properties settings) {
        super(settings.lightLevel(state -> state.getValue(LIGHT)));
        registerDefaultState(defaultBlockState().setValue(LIGHT, 0));
    }

    @Override
    public MapCodec<? extends MashTubBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 0.2F;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return COLLISSION_SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected BlockEntityType<MashTubBlockEntity> getBlockEntityType() {
        return PSBlockEntities.MASH_TUB;
    }

    @Override
    protected MenuType<FluidContraptionScreenHandler<MashTubBlockEntity>> getScreenHandlerType() {
        return PSScreenHandlers.MASH_TUB;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        world.getBlockEntity(getBlockEntityPosition(world, pos), getBlockEntityType()).ifPresent(be -> {
            SimpleFluid fluid = be.getTank(Direction.UP).getFluidType();
            fluid.randomDisplayTick(world, pos, fluid.getPhysical().getDefaultState(), random);
        });
    }

    @Override
    protected InteractionResult onInteract(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, MashTubBlockEntity blockEntity) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!heldStack.isEmpty()) {
            MashTubBlockEntity.DepositResult<ItemStack> result = blockEntity.depositIngredient(heldStack.copy());
            if (!player.isCreative()) {
                player.setItemInHand(hand, result.getValue());
            }
            if (result.getResult() != InteractionResult.PASS) {
                return result.getResult();
            }
        }

        if (!blockEntity.solidContents.isEmpty()) {
            PSCriteria.SIMPLY_MASHING.trigger(player, blockEntity.solidContents);
            Block.popResource(world, pos, blockEntity.solidContents);
            blockEntity.solidContents = ItemStack.EMPTY;
            blockEntity.markForUpdate();
            return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return MashTubItem.findPlacementPosition(world, pos).isPresent();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT);
    }

    public int getFluidHeight(Level world, BlockState state, BlockPos pos, TagKey<Fluid> tag) {
        return world.getBlockEntity(pos, getBlockEntityType())
                .map(be -> be.getTank(Direction.UP))
                .filter(tank -> tank.getFluidType().getPhysical().isIn(tag) || (tag == FluidTags.WATER && tank.getFluidType().isCustomFluid()))
                .map(tank -> (int)(((float)tank.getLevel() / tank.getCapacity()) * 8))
                .orElse(-1);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity player, BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return world.getBlockEntity(pos, getBlockEntityType()).filter(be -> {
            Resovoir tank = be.getTank(Direction.UP);
            return (tank.isEmpty() || tank.getFluidType().getPhysical().isOf(fluid))
                    && tank.getCapacity() - tank.getLevel() >= FluidVolumes.BUCKET;
        }).isPresent();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        for (BlockPos neighbourPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            if (!neighbourPos.equals(pos) && world.getBlockState(neighbourPos).is(PSBlocks.MASH_TUB_EDGE)) {
                world.removeBlockEntity(neighbourPos);
                world.setBlock(neighbourPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return world.getBlockEntity(getBlockEntityPosition(world, pos), getBlockEntityType()).filter(be -> {
            SimpleFluid fluid = SimpleFluid.forVanilla(fluidState.getType());
            Resovoir tank = be.getTank(Direction.UP);

            if (tank.getCapacity() - tank.getLevel() < FluidVolumes.BUCKET) {
                return false;
            }

            ItemStack overflow = tank.deposit(fluid.getDefaultStack(FluidVolumes.BUCKET));
            if (!FluidContainer.UNLIMITED.getFluid(overflow).isEmpty() && world instanceof Level level) {
                Block.popResource(level, pos, overflow);
            }

            be.markForUpdate();
            return true;
        }).isPresent();
    }

    protected BlockPos getBlockEntityPosition(BlockGetter world, BlockPos pos) {
        return world.getBlockEntity(pos, PSBlockEntities.MASH_TUB_EDGE).map(MashTubWallBlock.MasterPosition::getMasterPos).orElse(pos);
    }

    @Override
    @Nullable
    public <Q extends BlockEntity> BlockEntityTicker<Q> getTicker(Level world, BlockState state, BlockEntityType<Q> type) {
        return world.isClientSide() ? createTickerHelper(type, getBlockEntityType(), (level, blockPos, blockState, entity) -> entity.tickAnimations()) : super.getTicker(world, state, type);
    }
}
