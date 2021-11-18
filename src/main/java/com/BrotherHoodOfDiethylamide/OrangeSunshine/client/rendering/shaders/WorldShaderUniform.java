package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.shader.IShaderManager;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public class WorldShaderUniform extends WorldShaderDefault implements AutoCloseable {
    private static final Logger LOGGER = OrangeSunshine.LOGGER;
    private int location;
    private final int count;
    private final int type;
    private final IntBuffer intValues;
    private final FloatBuffer floatValues;
    private final String name;
    private boolean dirty;
    private final IShaderManager parent;

    public WorldShaderUniform(String name, int type, int count, IShaderManager parentShader) {
        this.name = name;
        this.count = count;
        this.type = type;
        this.parent = parentShader;
        if (type <= 3) {
            this.intValues = MemoryUtil.memAllocInt(count);
            this.floatValues = null;
        } else {
            this.intValues = null;
            this.floatValues = MemoryUtil.memAllocFloat(count);
        }

        this.location = -1;
        this.markDirty();
    }

    public static int glGetUniformLocation(int programId, CharSequence name) {
        return GlStateManager._glGetUniformLocation(programId, name);
    }

    public static void uploadInteger(int uniformLocation, int value) {
        RenderSystem.glUniform1i(uniformLocation, value);
    }

    public static int glGetAttribLocation(int programId, CharSequence name) {
        return GlStateManager._glGetAttribLocation(programId, name);
    }

    @Override
    public void close() {
        if (this.intValues != null) {
            MemoryUtil.memFree(this.intValues);
        }

        if (this.floatValues != null) {
            MemoryUtil.memFree(this.floatValues);
        }

    }

    private void markDirty() {
        this.dirty = true;
        if (this.parent != null) {
            this.parent.markDirty();
        }

    }

    public static int getTypeFromString(String typeName) {
        return ShaderUniform.getTypeFromString(typeName);
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void setFloat(float v0) {
        this.floatValues.position(0);
        this.floatValues.put(0, v0);
        this.markDirty();
    }

    @Override
    public void setFloat(float v0, float v1) {
        this.floatValues.position(0);
        this.floatValues.put(0, v0);
        this.floatValues.put(1, v1);
        this.markDirty();
    }

    @Override
    public void setFloat(float v0, float v1, float v2) {
        this.floatValues.position(0);
        this.floatValues.put(0, v0);
        this.floatValues.put(1, v1);
        this.floatValues.put(2, v2);
        this.markDirty();
    }

    @Override
    public void setFloat(float v0, float v1, float v2, float v3) {
        this.floatValues.position(0);
        this.floatValues.put(v0);
        this.floatValues.put(v1);
        this.floatValues.put(v2);
        this.floatValues.put(v3);
        this.floatValues.flip();
        this.markDirty();
    }

    @Override
    public void setInt(int v0) {
        this.intValues.position(0);
        this.intValues.put(0, v0);
        this.markDirty();
    }

    @Override
    public void setInt(int v0, int v1) {
        this.intValues.position(0);
        this.intValues.put(0, v0);
        this.intValues.put(1, v1);
        this.markDirty();
    }

    @Override
    public void setInt(int v0, int v1, int v2) {
        this.intValues.position(0);
        this.intValues.put(0, v0);
        this.intValues.put(1, v1);
        this.intValues.put(2, v2);
        this.markDirty();
    }

    @Override
    public void setInt(int v0, int v1, int v2, int v3) {
        this.intValues.position(0);
        this.intValues.put(0, v0);
        this.intValues.put(1, v1);
        this.intValues.put(2, v2);
        this.intValues.put(3, v3);
        this.markDirty();
    }

    @Override
    public void setSafeFloat(float v0, float v1, float v2, float v3) {
        this.floatValues.position(0);
        if (this.type >= 4) {
            this.floatValues.put(0, v0);
        }

        if (this.type >= 5) {
            this.floatValues.put(1, v1);
        }

        if (this.type >= 6) {
            this.floatValues.put(2, v2);
        }

        if (this.type >= 7) {
            this.floatValues.put(3, v3);
        }

        this.markDirty();
    }

    @Override
    public void setSafeInt(int v0, int v1, int v2, int v3) {
        this.intValues.position(0);
        if (this.type >= 0) {
            this.intValues.put(0, v0);
        }

        if (this.type >= 1) {
            this.intValues.put(1, v1);
        }

        if (this.type >= 2) {
            this.intValues.put(2, v2);
        }

        if (this.type >= 3) {
            this.intValues.put(3, v3);
        }

        this.markDirty();
    }

    @Override
    public void setFloat(float[] floatArray) {
        if (floatArray.length < this.count) {
            LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, floatArray.length);
        } else {
            this.floatValues.position(0);
            this.floatValues.put(floatArray);
            this.floatValues.position(0);
            this.markDirty();
        }
    }

    @Override
    public void setInt(int[] intArray) {
        if (intArray.length < this.count) {
            LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, intArray.length);
        } else {
            this.intValues.position(0);
            this.intValues.put(intArray);
            this.intValues.position(0);
            this.markDirty();
        }
    }

    @Override
    public void setMatrix(Matrix4f matrix) {
        this.floatValues.position(0);
        matrix.store(this.floatValues);
        this.markDirty();
    }

    @Override
    public void upload() {
        /*if (!this.dirty) {
        }*/

        this.dirty = false;
        if (this.type <= 3) {
            this.uploadAsInteger();
        } else if (this.type <= 7) {
            this.uploadAsFloat();
        } else {
            if (this.type > 10) {
                LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", this.type);
                return;
            }

            this.uploadAsMatrix();
        }

    }

    private void uploadAsInteger() {
        this.intValues.clear();
        switch(this.type) {
            case 0:
                RenderSystem.glUniform1(this.location, this.intValues);
                break;
            case 1:
                RenderSystem.glUniform2(this.location, this.intValues);
                break;
            case 2:
                RenderSystem.glUniform3(this.location, this.intValues);
                break;
            case 3:
                RenderSystem.glUniform4(this.location, this.intValues);
                break;
            default:
                LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
        }

    }

    private void uploadAsFloat() {
        this.floatValues.clear();
        switch(this.type) {
            case 4:
                RenderSystem.glUniform1(this.location, this.floatValues);
                break;
            case 5:
                RenderSystem.glUniform2(this.location, this.floatValues);
                break;
            case 6:
                RenderSystem.glUniform3(this.location, this.floatValues);
                break;
            case 7:
                RenderSystem.glUniform4(this.location, this.floatValues);
                break;
            default:
                LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
        }

    }

    private void uploadAsMatrix() {
        this.floatValues.clear();
        switch(this.type) {
            case 8:
                RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
                break;
            case 9:
                RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
                break;
            case 10:
                RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
        }

    }
}
