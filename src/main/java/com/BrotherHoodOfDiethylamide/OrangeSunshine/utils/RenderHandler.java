package com.BrotherHoodOfDiethylamide.OrangeSunshine.utils;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.EntityShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.rendering.RenderShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderHandler
{
    @SideOnly(Side.CLIENT)
    public static void registerEntityRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityShmokeStackz.class, new IRenderFactory<EntityShmokeStackz>()
        {
            @Override
            public Render<? super EntityShmokeStackz> createRenderFor(RenderManager manager)
            {
                return new RenderShmokeStackz(manager);
            }
        });
    }

    public static void registerCustomMeshesAndStates()
    {
    }
}