package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BufferDrawEvent extends Event {
    public final String name;
    public final RenderType renderType;
    @Nullable
    public final ResourceLocation resourceLocation;

    public BufferDrawEvent(String name, RenderType renderType, @Nullable ResourceLocation resourceLocation) {
        this.name = name;
        this.renderType = renderType;
        this.resourceLocation = resourceLocation;
    }

    public static class Pre extends BufferDrawEvent {
        public Pre(String name, RenderType renderType, @Nullable ResourceLocation resourceLocation) {
            super(name, renderType, resourceLocation);
        }
    }

    public static class Post extends BufferDrawEvent {
        public Post(String name, RenderType renderType, @Nullable ResourceLocation resourceLocation) {
            super(name, renderType, resourceLocation);
        }
    }
}
