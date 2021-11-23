/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvShaderInstance3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Logger;

/**
 * Created by lukas on 26.02.14.
 */
public class ShaderMainDepth extends IvShaderInstance3D implements ShaderWorld
{
    public ShaderMainDepth(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean activate(float partialTicks, float ticks)
    {
        if (useShader())
        {
            Minecraft mc = Minecraft.getMinecraft();

            Entity renderEntity = mc.getRenderViewEntity();
            com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties drugProperties = com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.DrugProperties.getDrugProperties((EntityPlayer) renderEntity);

            setUniformFloats("ticks", ticks);
            setUniformInts("worldTime", (int) mc.world.getWorldTime());

            setUniformFloats("playerPos", (float) renderEntity.posX, (float) renderEntity.posY, (float) renderEntity.posZ);
            setDepthMultiplier(1.0f);
            setTexture2DEnabled(GLStateProxy.isTextureEnabled(OpenGlHelper.defaultTexUnit));
            setOverrideColor(null);
            setUseScreenTexCoords(false);
            setPixelSize(1.0f / mc.displayWidth, 1.0f / mc.displayHeight);

            float bigWaveStrength = 0.0f;
            float smallWaveStrength = 0.0f;
            float wiggleWaveStrength = 0.0f;
            float distantWorldDeformationStrength = 0.0f;
            if (drugProperties != null)
            {
                bigWaveStrength = drugProperties.hallucinationManager.getBigWaveStrength(drugProperties, partialTicks);
                smallWaveStrength = drugProperties.hallucinationManager.getSmallWaveStrength(drugProperties, partialTicks);
                wiggleWaveStrength = drugProperties.hallucinationManager.getWiggleWaveStrength(drugProperties, partialTicks);
                distantWorldDeformationStrength = drugProperties.hallucinationManager.getDistantWorldDeformationStrength(drugProperties, partialTicks);
            }
            setUniformFloats("bigWaves", bigWaveStrength);
            setUniformFloats("smallWaves", smallWaveStrength);
            setUniformFloats("wiggleWaves", wiggleWaveStrength);
            setUniformFloats("distantWorldDeformation", distantWorldDeformationStrength);

            return true;
        }

        return false;
    }

    @Override
    public void deactivate()
    {
        stopUsingShader();
    }

    @Override
    public void setTexture2DEnabled(boolean enabled)
    {
        setUniformInts("texture2DEnabled", enabled ? 1 : 0);
    }

    @Override
    public void setLightmapEnabled(boolean enabled)
    {

    }

    @Override
    public void setOverrideColor(float[] color)
    {
        if (color != null)
        {
            setUniformFloats("overrideColor", color);
        }
        else
        {
            setUniformFloats("overrideColor", 1F, 1F, 1F, 1F);
        }
    }

    @Override
    public void setGLLightEnabled(boolean enabled)
    {

    }

    @Override

    public void setGLLight(int number, float x, float y, float z, float strength, float specular)
    {

    }

    @Override
    public void setGLLightAmbient(float strength)
    {

    }

    @Override
    public void setFogMode(int mode)
    {

    }

    @Override
    public void setFogEnabled(boolean enabled)
    {

    }

    @Override
    public void setDepthMultiplier(float depthMultiplier)
    {
        setUniformFloats("depthMultiplier", depthMultiplier);
    }

    @Override
    public void setUseScreenTexCoords(boolean enabled)
    {
        setUniformInts("useScreenTexCoords", enabled ? 1 : 0);
    }

    @Override
    public void setPixelSize(float pixelWidth, float pixelHeight)
    {
        setUniformFloats("pixelSize", pixelWidth, pixelHeight);
    }

    @Override
    public void setBlendModeEnabled(boolean enabled)
    {

    }

    @Override
    public void setBlendFunc(int sFactor, int dFactor, int sFactorA, int dFactorA)
    {

    }

    @Override
    public void setProjectShadows(boolean projectShadows)
    {

    }

    @Override
    public void setForceColorSafeMode(boolean enable)
    {

    }
}
