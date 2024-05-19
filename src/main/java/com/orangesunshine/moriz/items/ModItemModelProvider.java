package com.orangesunshine.moriz.items;

import com.orangesunshine.moriz.OrangeSunshine;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, OrangeSunshine.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.simpleItems.forEach(this::simpleItem);
        ModItems.blockItems.forEach(this::simpleItem);
    }

    private void simpleItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(OrangeSunshine.MODID, "item/" + item.getId().getPath()));
    }
}
