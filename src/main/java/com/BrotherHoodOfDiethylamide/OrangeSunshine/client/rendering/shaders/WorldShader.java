package com.BrotherHoodOfDiethylamide.OrangeSunshine.client.rendering.shaders;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.shader.*;
import net.minecraft.client.util.JSONBlendingMode;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class WorldShader implements IShaderManager, AutoCloseable {
    private static final Logger LOGGER = OrangeSunshine.LOGGER;
    private static final WorldShaderDefault DUMMY_UNIFORM = new WorldShaderDefault();
    protected static WorldShader lastAppliedEffect;
    protected static int lastProgramId = -1;
    protected final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
    protected final List<String> samplerNames = Lists.newArrayList();
    protected final List<Integer> samplerLocations = Lists.newArrayList();
    protected final List<WorldShaderUniform> uniforms = Lists.newArrayList();
    protected final List<Integer> uniformLocations = Lists.newArrayList();
    protected final Map<String, WorldShaderUniform> uniformMap = Maps.newHashMap();
    protected final int programId;
    protected final String name;
    protected boolean dirty;
    protected final JSONBlendingMode blend;
    protected final List<Integer> attributes;
    protected final List<String> attributeNames;
    protected final ShaderLoader vertexProgram;
    protected final ShaderLoader fragmentProgram;
    protected boolean samplerDirty;

    public WorldShader(IResourceManager resourceManager, String location) throws IOException {
        ResourceLocation rl = ResourceLocation.tryParse(location);
        assert rl != null;
        ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + ".json");
        this.name = location;
        IResource iresource = null;

        try {
            iresource = resourceManager.getResource(resourcelocation);
            JsonObject jsonobject = JSONUtils.parse(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            String s = JSONUtils.getAsString(jsonobject, "vertex");
            String s2 = JSONUtils.getAsString(jsonobject, "fragment");
            JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "samplers", null);
            if (jsonarray != null) {
                int i = 0;

                for(JsonElement jsonelement : jsonarray) {
                    try {
                        this.parseSamplerNode(jsonelement);
                    } catch (Exception exception2) {
                        JSONException jsonexception1 = JSONException.forException(exception2);
                        jsonexception1.prependJsonKey("samplers[" + i + "]");
                        throw jsonexception1;
                    }

                    ++i;
                }
            }

            JsonArray jsonarray1 = JSONUtils.getAsJsonArray(jsonobject, "attributes", null);
            if (jsonarray1 != null) {
                int j = 0;
                this.attributes = Lists.newArrayListWithCapacity(jsonarray1.size());
                this.attributeNames = Lists.newArrayListWithCapacity(jsonarray1.size());

                for(JsonElement jsonelement1 : jsonarray1) {
                    try {
                        this.attributeNames.add(JSONUtils.convertToString(jsonelement1, "attribute"));
                    } catch (Exception exception1) {
                        JSONException jsonexception2 = JSONException.forException(exception1);
                        jsonexception2.prependJsonKey("attributes[" + j + "]");
                        throw jsonexception2;
                    }

                    ++j;
                }
            } else {
                this.attributes = null;
                this.attributeNames = null;
            }

            JsonArray jsonarray2 = JSONUtils.getAsJsonArray(jsonobject, "uniforms", null);
            if (jsonarray2 != null) {
                int k = 0;

                for(JsonElement jsonelement2 : jsonarray2) {
                    try {
                        this.parseUniformNode(jsonelement2);
                    } catch (Exception exception) {
                        JSONException jsonexception3 = JSONException.forException(exception);
                        jsonexception3.prependJsonKey("uniforms[" + k + "]");
                        throw jsonexception3;
                    }

                    ++k;
                }
            }

            this.blend = ShaderInstance.parseBlendNode(JSONUtils.getAsJsonObject(jsonobject, "blend", null));
            this.vertexProgram = ShaderInstance.getOrCreate(resourceManager, ShaderLoader.ShaderType.VERTEX, s);
            this.fragmentProgram = ShaderInstance.getOrCreate(resourceManager, ShaderLoader.ShaderType.FRAGMENT, s2);
            this.programId = ShaderLinkHelper.createProgram();
            ShaderLinkHelper.linkProgram(this);
            this.updateLocations();
            if (this.attributeNames != null) {
                for(String s3 : this.attributeNames) {
                    int l = ShaderUniform.glGetAttribLocation(this.programId, s3);
                    this.attributes.add(l);
                }
            }
        } catch (Exception exception3) {
            String s1;
            if (iresource != null) {
                s1 = " (" + iresource.getSourceName() + ")";
            } else {
                s1 = "";
            }

            JSONException jsonexception = JSONException.forException(exception3);
            jsonexception.setFilenameAndFlush(resourcelocation.getPath() + s1);
            throw jsonexception;
        } finally {
            IOUtils.closeQuietly(iresource);
        }

        this.markDirty();
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public WorldShaderUniform getUniform(String name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.uniformMap.get(name);
    }

    public WorldShaderDefault safeGetUniform(String name) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        WorldShaderUniform shaderuniform = this.getUniform(name);
        return shaderuniform == null ? DUMMY_UNIFORM : shaderuniform;
    }

    private void updateLocations() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IntList intlist = new IntArrayList();

        for(int i = 0; i < this.samplerNames.size(); ++i) {
            String s = this.samplerNames.get(i);
            int j = ShaderUniform.glGetUniformLocation(this.programId, s);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, s);
                this.samplerMap.remove(s);
                intlist.add(i);
            } else {
                this.samplerLocations.add(j);
            }
        }

        for(int l = intlist.size() - 1; l >= 0; --l) {
            this.samplerNames.remove(intlist.getInt(l));
        }

        for(WorldShaderUniform shaderuniform : this.uniforms) {
            String s1 = shaderuniform.getName();
            int k = ShaderUniform.glGetUniformLocation(this.programId, s1);
            if (k == -1) {
                LOGGER.warn("Could not find uniform named {} in the specified shader program.", s1);
            } else {
                this.uniformLocations.add(k);
                shaderuniform.setLocation(k);
                this.uniformMap.put(s1, shaderuniform);
            }
        }

    }

    private void parseSamplerNode(JsonElement p_216541_1_) {
        JsonObject jsonobject = JSONUtils.convertToJsonObject(p_216541_1_, "sampler");
        String s = JSONUtils.getAsString(jsonobject, "name");
        if (!JSONUtils.isStringValue(jsonobject, "file")) {
            this.samplerMap.put(s, null);
        }
        this.samplerNames.add(s);
    }

    public void setSampler(@Nonnull String samplerName, @Nonnull IntSupplier intSupplier) {
        samplerMap.remove(samplerName);

        this.samplerMap.put(samplerName, intSupplier);
        this.markDirty();

        samplerDirty = true;
    }

    private void parseUniformNode(JsonElement p_216540_1_) throws JSONException {
        JsonObject jsonobject = JSONUtils.convertToJsonObject(p_216540_1_, "uniform");
        String uniformName = JSONUtils.getAsString(jsonobject, "name");
        int uniformType = WorldShaderUniform.getTypeFromString(JSONUtils.getAsString(jsonobject, "type"));
        int uniformCount = JSONUtils.getAsInt(jsonobject, "count");
        float[] afloat = new float[Math.max(uniformCount, 16)];
        JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "values");
        if (jsonarray.size() != uniformCount && jsonarray.size() > 1) {
            throw new JSONException("Invalid amount of values specified (expected " + uniformCount + ", found " + jsonarray.size() + ")");
        } else {
            int k = 0;

            for(JsonElement jsonelement : jsonarray) {
                try {
                    afloat[k] = JSONUtils.convertToFloat(jsonelement, "value");
                } catch (Exception exception) {
                    JSONException jsonexception = JSONException.forException(exception);
                    jsonexception.prependJsonKey("values[" + k + "]");
                    throw jsonexception;
                }

                ++k;
            }

            if (uniformCount > 1 && jsonarray.size() == 1) {
                while(k < uniformCount) {
                    afloat[k] = afloat[0];
                    ++k;
                }
            }

            int l = (uniformCount > 1 && uniformCount <= 4 && uniformType < 8) ? uniformCount - 1 : 0;
            WorldShaderUniform shaderuniform = new WorldShaderUniform(uniformName, uniformType + l, uniformCount, this);
            if (uniformType <= 3) {
                shaderuniform.setSafeInt((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
            } else if (uniformType <= 7) {
                shaderuniform.setSafeFloat(afloat[0], afloat[1], afloat[2], afloat[3]);
            } else {
                shaderuniform.setFloat(afloat);
            }

            this.uniforms.add(shaderuniform);
        }
    }

    public void apply() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);

        if (samplerDirty) {
            samplerDirty = false;
            samplerLocations.clear();
            uniformLocations.clear();
            updateLocations();
        }

        dirty = false;
        lastAppliedEffect = this;
        blend.apply();
        if (programId != lastProgramId) {
            ShaderLinkHelper.glUseProgram(getId());
            lastProgramId = programId;
        }

        for(int i = 0; i < samplerLocations.size(); ++i) {
            String s = samplerNames.get(i);
            IntSupplier intsupplier = samplerMap.get(s);
            if (intsupplier != null) {
                int j = intsupplier.getAsInt();
                if (j != -1) {
                    ShaderUniform.uploadInteger(samplerLocations.get(i), j);
                }
            }
        }

        for (WorldShaderUniform shaderUniform : uniforms) {
            shaderUniform.upload();
        }
    }

    @Override
    public void close() {
        for(WorldShaderUniform shaderuniform : this.uniforms) {
            shaderuniform.close();
        }

        ShaderLinkHelper.releaseProgram(this);
    }

    public void clear() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ShaderLinkHelper.glUseProgram(0);
        lastProgramId = -1;
        lastAppliedEffect = null;
    }

    @Override
    @Nonnull
    public ShaderLoader getVertexProgram() {
        return this.vertexProgram;
    }

    @Override
    @Nonnull
    public ShaderLoader getFragmentProgram() {
        return this.fragmentProgram;
    }

    @Override
    public int getId() {
        return this.programId;
    }
}
