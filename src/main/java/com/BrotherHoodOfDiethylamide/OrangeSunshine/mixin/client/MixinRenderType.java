package com.BrotherHoodOfDiethylamide.OrangeSunshine.mixin.client;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders.RenderTypeTypeExt;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks.BufferDrawEvent;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class) @SuppressWarnings("ConstantConditions")
public class MixinRenderType extends RenderState {
    public MixinRenderType(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldVertexBufferUploader;end(Lnet/minecraft/client/renderer/BufferBuilder;)V"), method = "end")
    private void preDraw(BufferBuilder bufferBuilder, int cameraX, int cameraY, int cameraZ, CallbackInfo ci) {
        RenderType renderType = (RenderType) (Object) this;
        AccessorRenderTypeState accessorRenderTypeState = (AccessorRenderTypeState) (Object) ((RenderTypeTypeExt) renderType).getState();

        InvokerRenderStateTexture invokerRenderStateTexture = (InvokerRenderStateTexture) accessorRenderTypeState.getTextureState();

        ResourceLocation resourceLocation = null;
        if (invokerRenderStateTexture.callTexture().isPresent()) resourceLocation = invokerRenderStateTexture.callTexture().get();

        MinecraftForge.EVENT_BUS.post(new BufferDrawEvent.Pre(name, renderType, resourceLocation));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldVertexBufferUploader;end(Lnet/minecraft/client/renderer/BufferBuilder;)V", shift = At.Shift.AFTER), method = "end")
    private void postDraw(BufferBuilder bufferBuilder, int cameraX, int cameraY, int cameraZ, CallbackInfo ci) {
        RenderType renderType = (RenderType) (Object) this;
        AccessorRenderTypeState accessorRenderTypeState = (AccessorRenderTypeState) (Object) ((RenderTypeTypeExt) renderType).getState();

        InvokerRenderStateTexture invokerRenderStateTexture = (InvokerRenderStateTexture) accessorRenderTypeState.getTextureState();

        ResourceLocation resourceLocation = null;
        if (invokerRenderStateTexture.callTexture().isPresent()) resourceLocation = invokerRenderStateTexture.callTexture().get();

        MinecraftForge.EVENT_BUS.post(new BufferDrawEvent.Post(name, renderType, resourceLocation));
    }
}
