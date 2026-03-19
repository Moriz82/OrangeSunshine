package moriz.orangesunshine.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NightshadeBlock extends BushBlock {
    public static final MapCodec<NightshadeBlock> CODEC = RecordCodecBuilder.<NightshadeBlock>mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("fruit").forGetter(b -> b.fruit.asItem()),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("leaf").forGetter(b -> b.leaf.asItem()),
            propertiesCodec()
    ).apply(instance, NightshadeBlock::new));
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final int MAX_AGE = 7;
    private static final TagKey<Item> SHEARS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "shears"));

    private static final VoxelShape[] SHAPES = {
            ShapeUtil.createCenteredShape(1, 2, 1),
            ShapeUtil.createCenteredShape(1.5, 4, 1.5),
            ShapeUtil.createCenteredShape(2, 6, 2),
            ShapeUtil.createCenteredShape(3, 9, 3),
            ShapeUtil.createCenteredShape(6, 10, 6),
            ShapeUtil.createCenteredShape(6, 14, 6),
            ShapeUtil.createCenteredShape(7, 15, 7),
            ShapeUtil.createCenteredShape(7, 15, 7)
    };

    private final ItemLike fruit;
    private final ItemLike leaf;

    public NightshadeBlock(ItemLike fruit, ItemLike leaf, BlockBehaviour.Properties settings) {
        super(settings);
        this.fruit = fruit;
        this.leaf = leaf;
        registerDefaultState(defaultBlockState().setValue(AGE, 0));
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<BushBlock> codec() {
        return (MapCodec<BushBlock>)(MapCodec<?>)CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) < MAX_AGE && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9) {
            state = state.cycle(AGE);
            world.setBlock(pos, state, Block.UPDATE_ALL);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean inside) {
        if (!(entity instanceof LivingEntity) || state.getValue(AGE) < 3 || entity.getType() == EntityType.FOX || entity.getType() == EntityType.BEE) {
            return;
        }
        entity.makeStuckInBlock(state, new Vec3(0.8f, 0.75, 0.8f));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        if ((stack.is(SHEARS) && age >= 1) || (stack.is(Items.BONE_MEAL) && age == MAX_AGE)) {
            if (!world.isClientSide()) {
                if (stack.is(SHEARS)) {
                    stack.hurtAndBreak(1, player, hand);
                }
                Block.popResource(world, pos, new ItemStack((age == MAX_AGE ? fruit : leaf).asItem(), 1 + world.random.nextInt(2)));

                BlockState newState = state.setValue(AGE, age - 1);
                world.setBlock(pos, newState, Block.UPDATE_ALL);
                world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

                if (stack.is(Items.BONE_MEAL)) {
                    return InteractionResult.PASS;
                }

                world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1, 0.8F + world.random.nextFloat() * 0.4F);
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int i = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        world.setBlock(pos, state.setValue(AGE, i), Block.UPDATE_ALL);
    }
}
