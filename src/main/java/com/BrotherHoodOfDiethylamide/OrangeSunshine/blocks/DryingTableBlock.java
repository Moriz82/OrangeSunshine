package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.DryingTableContainer;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.DryingTableTile;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Objects;

public class DryingTableBlock extends Block {
    public DryingTableBlock(Properties properties) {
        super(properties);
    }


    @Override
       public ActionResultType use(BlockState state, World worldIn, BlockPos pos,
                                PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isClientSide()) {

            try {
                addChunk(pos, worldIn.getChunk(pos.getX(), pos.getY()).getPos(), (ServerWorld) worldIn);
            }catch (Exception ignore){}

            TileEntity tileEntity = worldIn.getBlockEntity(pos);

            if(tileEntity instanceof DryingTableTile) {
                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);

                NetworkHooks.openGui(((ServerPlayerEntity)player), containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void addChunk(BlockPos bPos, ChunkPos pos, ServerWorld world) {
        try {
            ForgeChunkManager.forceChunk(world, OrangeSunshine.MOD_ID, bPos, pos.x, pos.z, true, true);
        }catch (Exception ignore) {}
    }

    public void removeChunkLoader(BlockPos bPos, ChunkPos pos, ServerWorld world) {
        try {
            ForgeChunkManager.forceChunk(world, OrangeSunshine.MOD_ID, bPos, pos.x, pos.z, false, true);
        }catch (Exception ignore) {}
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.orangesunshine.drying_table");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new DryingTableContainer(i, worldIn, pos, playerInventory, playerEntity);
            }
        };
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity entity) {
        if (!world.isClientSide()){
            try {
                removeChunkLoader(pos, world.getChunk(pos.getX(), pos.getY()).getPos(), (ServerWorld) world);
            }catch (Exception ignore){}
        }
        super.playerWillDestroy(world, pos, state, entity);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.DRYING_TABLE_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}