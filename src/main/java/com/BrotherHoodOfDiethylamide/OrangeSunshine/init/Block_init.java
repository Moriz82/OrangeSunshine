package com.BrotherHoodOfDiethylamide.OrangeSunshine.init;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.CompoundExtractor;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.crops.RyeCrop;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.crops.WeedCrop;
import net.minecraft.block.Block;
import java.util.ArrayList;

public class Block_init {
    public static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final Block WeedCrop = new WeedCrop("weedcrop");
    public static final Block RyeCrop = new RyeCrop("ryecrop");
}
