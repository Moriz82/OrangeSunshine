package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.CompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.PsychOre;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.crops.MushroomCrop;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.crops.RyeCrop;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.crops.WeedCrop;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.useables.edibles.MushroomCubensis;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;

public class Block_init {
    public static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final Block WeedCrop = new WeedCrop("weed_crop");
    public static final Block RyeCrop = new RyeCrop("rye_crop");
    public static final Block MushroomCrop = new MushroomCrop("mushroom_crop");
}
