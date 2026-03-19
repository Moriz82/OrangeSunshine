package moriz.orangesunshine.util;

import org.joml.Vector3d;
import org.joml.Vector3f;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public interface MathUtils {
    static float nearValue(float from, float to, float delta, float adjustmentRate) {
        return approach(Mth.lerp(delta, from, to), to, adjustmentRate);
    }

    static float approach(float value, float target, float adjustmentRate) {
        if (value > target) {
            return Math.max(value - adjustmentRate, target);
        }

        if (value < target) {
            return Math.min(value + adjustmentRate, target);
        }

        return value;
    }

    static double nearValue(double from, double to, double delta, double adjustmentRate) {
        return approach(Mth.lerp(delta, from, to), to, adjustmentRate);
    }

    static double approach(double value, double target, double adjustmentRate) {
        if (value > target) {
            return Math.max(value - adjustmentRate, target);
        }

        if (value < target) {
            return Math.min(value + adjustmentRate, target);
        }

        return value;
    }

    static Vector3f unpackRgbVector(int left) {
        return new Vector3f(r(left), g(left), b(left));
    }

    static float[] unpackRgb(int left) {
        return new float[] {r(left), g(left), b(left)};
    }

    static float[] unpackArgb(int left) {
        return new float[] {a(left), r(left), g(left), b(left)};
    }

    static float a(int left) {
        return (left >> 24 & 0xFF) / 255F;
    }

    static float r(int left) {
        return (left >> 16 & 0xFF) / 255F;
    }

    static float g(int left) {
        return (left >> 8 & 0xFF) / 255F;
    }

    static float b(int left) {
        return (left & 0xFF) / 255F;
    }

    static int mixColors(int left, int right, float progress) {
        return packArgb(
                Mth.lerp(progress, a(left), a(right)),
                Mth.lerp(progress, r(left), r(right)),
                Mth.lerp(progress, g(left), g(right)),
                Mth.lerp(progress, b(left), b(right))
        );
    }

    static int packArgb(float a, float r, float g, float b) {
        return packArgb(
                Mth.floor(a * 255 + 0.5F),
                Mth.floor(r * 255 + 0.5F),
                Mth.floor(g * 255 + 0.5F),
                Mth.floor(b * 255 + 0.5F)
        );
    }

    static float[] mixColorsDynamic(float[] color, float[] colorBase, float alpha, boolean fixAlpha) {
        if (alpha > 0.0f) {
            float max = alpha + colorBase[3];
            colorBase[0] = Mth.lerp(alpha / max, colorBase[0], color[0]);
            colorBase[1] = Mth.lerp(alpha / max, colorBase[1], color[1]);
            colorBase[2] = Mth.lerp(alpha / max, colorBase[2], color[2]);
            colorBase[3] = max;
        }
        if (fixAlpha) {
            colorBase[3] = Mth.clamp(colorBase[3], 0, 1);
        }
        return colorBase;
    }

    static float randomColor(RandomSource random, int ticksExisted, float base, float sway, float... speed) {
        for (float s : speed) {
            base *= 1.0f + Mth.sin(ticksExisted * s) * sway;
        }
        return base;
    }

    static float progress(float metric, float delta) {
        return 1 - (delta / (1 + metric));
    }

    static float progress(float metric) {
        return progress(metric, 1F);
    }

    static int packArgb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    static float mixEaseInOut(float v1, float v2, float delta) {
        return cubicMix(v1, v1, v2, v2, delta);
    }

    static double mixEaseInOut(double v1, double v2, double delta) {
        return cubicMix(v1, v1, v2, v2, delta);
    }

    static double easeZeroToOne(double delta) {
        return cubicMix(0, 0, 1, 1, Mth.clamp(delta, 0, 1));
    }

    static float easeZeroToOne(float delta) {
        return cubicMix(0, 0, 1, 1, Mth.clamp(delta, 0, 1));
    }

    static float inverseLerp(float value, float start, float end) {
        return Mth.clamp(Mth.inverseLerp(value, start, end), 0, 1);
    }

    static Vector3d cubicMix(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, double delta, Vector3d dest) {
        return dest.set(
            cubicMix(v1.x, v2.x, v3.x, v4.x, delta),
            cubicMix(v1.y, v2.y, v3.y, v4.y, delta),
            cubicMix(v1.z, v2.z, v3.z, v4.z, delta)
        );
    }

    static float cubicMix(float v1, float v2, float v3, float v4, float delta) {
        return (float)Mth.lerp3(delta, delta, delta, v1, v2, v2, v3, v2, v3, v3, v4);
    }

    static double cubicMix(double v1, double v2, double v3, double v4, double delta) {
        return Mth.lerp3(delta, delta, delta, v1, v2, v2, v3, v2, v3, v3, v4);
    }

    static Vector3d apply(Vector3d vector, Double2DoubleFunction function) {
        return new Vector3d(
                function.applyAsDouble(vector.x),
                function.applyAsDouble(vector.y),
                function.applyAsDouble(vector.z)
        );
    }
}
