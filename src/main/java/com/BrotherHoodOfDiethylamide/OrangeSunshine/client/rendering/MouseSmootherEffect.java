package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSmootherEffect {
    public static final MouseSmootherEffect INSTANCE = new MouseSmootherEffect();
    private final Smoother smoothTurnX = new Smoother();
    private final Smoother smoothTurnY = new Smoother();
    private double lastMouseEventTime = Double.MIN_VALUE;
    private double amplifier = 0;
    private double smoothedX = 0;
    private double smoothedY = 0;

    private MouseSmootherEffect() {
    }

    public void setAmplifier(double amplifier) {
        this.amplifier = amplifier;
    }

    public double getAmplifier() {
        return amplifier;
    }

    public void tick(double deltaX, double deltaY) {
        Minecraft minecraft = Minecraft.getInstance();
        double time = NativeUtil.getTime();
        double deltaTime = time - lastMouseEventTime;
        lastMouseEventTime = time;

        if (minecraft.mouseHandler.isMouseGrabbed() && minecraft.isWindowActive()) {
            double d0 = minecraft.options.sensitivity * 0.6D + 0.2D;
            double d1 = d0 * d0 * d0 * 8.0D;

            double amplifier = getAmplifier();
            smoothedX = smoothTurnX.getNewDeltaValue(deltaX*d1, deltaTime*d1, amplifier);
            smoothedY = smoothTurnY.getNewDeltaValue(deltaY*d1, deltaTime*d1, amplifier);
        }
    }

    public double getX() {
        return smoothedX;
    }

    public double getY() {
        return smoothedY;
    }

    public void reset() {
        smoothTurnX.reset();
        smoothTurnY.reset();
    }

    private static class Smoother {
        private double targetValue;
        private double remainingValue;
        private double lastAmount;

        public double getNewDeltaValue(double target, double dt, double inertia) {
            targetValue += target;
            double t = 1D - inertia;
            dt = MathHelper.lerp(t*t*t, dt, 1D);
            double d0 = targetValue - remainingValue;
            double d1 = MathHelper.lerp(1D - inertia/2D, lastAmount, d0);
            double d2 = Math.signum(d0);
            if (d2 * d0 > d2 * lastAmount) {
                d0 = d1;
            }

            lastAmount = d1;
            remainingValue += d0 * dt;
            return d0 * dt;
        }

        public void reset() {
            this.targetValue = 0.0D;
            this.remainingValue = 0.0D;
            this.lastAmount = 0.0D;
        }
    }
}
