package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.eventbus.api.Event;

public class BobHurtEvent extends Event {
    public final MatrixStack matrixStack;
    public final float partialTicks;

    public BobHurtEvent(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }
}
