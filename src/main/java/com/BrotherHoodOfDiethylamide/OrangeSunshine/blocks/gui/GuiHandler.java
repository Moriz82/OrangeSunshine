package com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.gui;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.container.*;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int GUI_DRYING_TABLE = 0;
    public static final int GUI_FRIDGE = 1;
    public static final int GUI_COMPOUND_COMPRESSOR = 2;
    public static final int GUI_COMPOUND_EXTRACTOR = 3;

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        switch (id) {
            case GUI_DRYING_TABLE:
                if (te instanceof TileDryingTable)
                    return new ContainerDryingTable(player.inventory, (TileDryingTable) te);
                break;
            case GUI_FRIDGE:
                if (te instanceof TileFridge)
                    return new ContainerFridge(player.inventory, (TileFridge) te);
                break;
            case GUI_COMPOUND_COMPRESSOR:
                if (te instanceof TileCompoundCompressor)
                    return new ContainerCompoundCompressor(player.inventory, (TileCompoundCompressor) te);
                break;
            case GUI_COMPOUND_EXTRACTOR:
                if (te instanceof TileCompoundExtractor)
                    return new ContainerCompoundExtractor(player.inventory, (TileCompoundExtractor) te);
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        switch (id) {
            case GUI_DRYING_TABLE:
                if (te instanceof TileDryingTable)
                    return new GuiDryingTable(player.inventory, (TileDryingTable) te);
                break;
            case GUI_FRIDGE:
                if (te instanceof TileFridge)
                    return new GuiFridge(player.inventory, (TileFridge) te);
                break;
            case GUI_COMPOUND_COMPRESSOR:
                if (te instanceof TileCompoundCompressor)
                    return new GuiCompoundCompressor(player.inventory, (TileCompoundCompressor) te);
                break;
            case GUI_COMPOUND_EXTRACTOR:
                if (te instanceof TileCompoundExtractor)
                    return new GuiCompoundExtractor(player.inventory, (TileCompoundExtractor) te);
                break;
        }
        return null;
    }
}
