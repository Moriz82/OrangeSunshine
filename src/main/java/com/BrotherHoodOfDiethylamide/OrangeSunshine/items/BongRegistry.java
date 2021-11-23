package com.BrotherHoodOfDiethylamide.OrangeSunshine.items;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;

public class BongRegistry {
    public static void register() {
        // bong items
        BongItem.BONGABLES.put(ModItems.COCAINE_ROCK.get(), new ModItems.DrugChain().add(DrugRegistry.COCAINE, 0, 0.45F, 2800));
        BongItem.BONGABLES.put(ModItems.DRIED_WEED_BUD.get(), new ModItems.DrugChain().add(DrugRegistry.WEED, 0, 0.2F, 3200));
        // rig items
        RigItem.RIGABLES.put(ModItems.WEED_EXTRACT.get(), new ModItems.DrugChain().add(DrugRegistry.WEED, 0, 0.90F, 5200));
    }
}
