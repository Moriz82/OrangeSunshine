/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */
package com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.Iv2DScreenEffect;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvOpenGLTexturePingPong;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.legacy.rendering.IvRenderHelper;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.ivtoolkit.math.IvMathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by lukas on 26.02.14.
 */
public class EffectLensFlare implements Iv2DScreenEffect
{
    public float[] sunFlareSizes;
    public float[] sunFlareInfluences;
    public ResourceLocation[] sunFlareTextures;

    public ResourceLocation sunBlindnessTexture;
    public float sunFlareIntensity;

    public float actualSunAlpha = 0.0f;

    public void updateLensFlares()
    {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        Entity renderEntity = mc.getRenderViewEntity(); // .renderViewEntity;

        if (renderEntity != null && world != null)
        {;
            float sunSizeRadians = -5.0f / 180.0f * 3.1315926f;
            float sunWidth = 20.0f;
            float sunRadians = world.getCelestialAngleRadians(1.0f);

            Vec3d sunVecTopLeft = new Vec3d(-MathHelper.sin(sunRadians - sunSizeRadians) * 120.0f,MathHelper.cos(sunRadians - sunSizeRadians) * 120.0f,-sunWidth);
            Vec3d sunVecTopRight = new Vec3d(-MathHelper.sin(sunRadians - sunSizeRadians) * 120.0f,MathHelper.cos(sunRadians - sunSizeRadians) * 120.0f,sunWidth);
            Vec3d sunVecBottomLeft = new Vec3d(-MathHelper.sin(sunRadians + sunSizeRadians) * 120.0f,MathHelper.cos(sunRadians + sunSizeRadians) * 120.0f,-sunWidth);
            Vec3d sunVecBottomRight = new Vec3d(-MathHelper.sin(sunRadians + sunSizeRadians) * 120.0f,MathHelper.cos(sunRadians + sunSizeRadians) * 120.0f,sunWidth);

            BlockPos playerPos = renderEntity.getPosition();

            //Need copies, because the raytracer edits these
            Vec3d playerPositionLT = new Vec3d(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            Vec3d playerPositionLB = new Vec3d(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            Vec3d playerPositionRT = new Vec3d(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            Vec3d playerPositionRB = new Vec3d(playerPos.getX(), playerPos.getY(), playerPos.getZ());

            Vec3d sunPosTopLeft = playerPositionLT.addVector(sunVecTopLeft.x, sunVecTopLeft.y, sunVecTopLeft.z);
            Vec3d sunPosTopRight = playerPositionRT.addVector(sunVecTopRight.x, sunVecTopRight.y, sunVecTopRight.z);
            Vec3d sunPosBottomLeft = playerPositionLB.addVector(sunVecBottomLeft.x, sunVecBottomLeft.y, sunVecBottomLeft.z);
            Vec3d sunPosBottomRight = playerPositionRB.addVector(sunVecBottomRight.x, sunVecBottomRight.y, sunVecBottomRight.z);

            // Raytraceblocks
            RayTraceResult sunTopLeft = world.rayTraceBlocks(playerPositionLT, sunPosTopLeft, true, true, true);
            RayTraceResult sunTopRight = world.rayTraceBlocks(playerPositionRT, sunPosTopRight, true, true, true);
            RayTraceResult sunBottomLeft = world.rayTraceBlocks(playerPositionLB, sunPosBottomLeft, true, true, true);
            RayTraceResult sunBottomRight = world.rayTraceBlocks(playerPositionRB, sunPosBottomRight, true, true, true);

            float newSunAlpha = (1.0F - world.getRainStrength(1.0f)) * ((sunTopLeft == null ? 0.25f : 0.0f) + (sunTopRight == null ? 0.25f : 0.0f) + (sunBottomLeft == null ? 0.25f : 0.0f) + (sunBottomRight == null ? 0.25f : 0.0f));
            actualSunAlpha = IvMathHelper.nearValue(actualSunAlpha, newSunAlpha, 0.1f, 0.01f);
            if (actualSunAlpha > 1.0f)
            {
                actualSunAlpha = 1.0f;
            }
        }
    }

    public void renderLensFlares(int screenWidth, int screenHeight, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        Entity renderEntity = mc.getRenderViewEntity();

        float sunRadians = world.getCelestialAngleRadians(partialTicks);

        Vector3f sunVecCenter = new Vector3f(-MathHelper.sin(sunRadians) * 120.0f, MathHelper.cos(sunRadians) * 120.0f, 0.0f);

        if (actualSunAlpha > 0.0f)
        {
            float genSize = screenWidth > screenHeight ? screenWidth : screenHeight;

            Vector3f sunPositionOnScreen = PsycheMatrixHelper.projectPointCurrentView(sunVecCenter, partialTicks);

            Vector3f normSunPos = new Vector3f();
            sunPositionOnScreen.normalise(normSunPos);
            float xDist = normSunPos.x * screenWidth;
            float yDist = normSunPos.y * screenHeight;

            Vec3d color = world.getFogColor(1.0f);

            if (sunPositionOnScreen.z > 0.0f)
            {
                float alpha = sunPositionOnScreen.z;
                if (alpha > 1.0f)
                {
                    alpha = 1.0f;
                }

                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                Tessellator var3 = Tessellator.getInstance();

                float screenCenterX = screenWidth * 0.5f;
                float screenCenterY = screenHeight * 0.5f;

                for (int i = 0; i < sunFlareSizes.length; i++)
                {
                    float flareSizeHalf = sunFlareSizes[i] * genSize * 0.5f;
                    float flareCenterX = screenCenterX + xDist * sunFlareInfluences[i];
                    float flareCenterY = screenCenterY + yDist * sunFlareInfluences[i];

                    GL11.glColor4f((float) color.x - 0.1f, (float) color.y - 0.1f, (float) color.z - 0.1f, (alpha * i == 8 ? 1.0f : 0.5f) * actualSunAlpha * sunFlareIntensity);

                    mc.renderEngine.bindTexture(sunFlareTextures[i]);
                    //var3.startDrawingQuads();
                    var3.getBuffer().begin(2, new VertexFormat());
                    var3.getBuffer().addVertexData(new int[]{(int) ((int)flareCenterX - flareSizeHalf), (int) ((int)flareCenterY + flareSizeHalf), (int)-90, 0, 1});
                    var3.getBuffer().addVertexData(new int[]{(int) (flareCenterX + flareSizeHalf), (int) (flareCenterY + flareSizeHalf), -90, 1, 1});
                    var3.getBuffer().addVertexData(new int[]{(int) (flareCenterX + flareSizeHalf), (int) (flareCenterY - flareSizeHalf), -90, 1, 0});
                    var3.getBuffer().addVertexData(new int[]{(int) (flareCenterX - flareSizeHalf), (int) (flareCenterY - flareSizeHalf), -90, 0, 0});
                    var3.draw();
                }

                // Looks weird because of a hard edge... :|
                float genDist = 1.0f - (normSunPos.x * normSunPos.x + normSunPos.y * normSunPos.y);
                float blendingSize = (genDist - 0.1f) * sunFlareIntensity * 250.0f * genSize;

                if (blendingSize > 0.0f)
                {
                    float blendingSizeHalf = blendingSize * 0.5f;
                    float blendCenterX = screenCenterX + xDist;
                    float blendCenterY = screenCenterY + yDist;

                    float blendAlpha = blendingSize / genSize / 150f;
                    if (blendAlpha > 1.0f)
                    {
                        blendAlpha = 1.0f;
                    }

                    GL11.glColor4f((float) color.x - 0.1f, (float) color.y - 0.1f, (float) color.z - 0.1f, blendAlpha * actualSunAlpha);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);

                    mc.renderEngine.bindTexture(sunBlindnessTexture);
                    //var3.startDrawingQuads();
                    var3.getBuffer().begin(2, new VertexFormat());
                    var3.getBuffer().addVertexData(new int[]{(int) (blendCenterX - blendingSizeHalf), (int) (blendCenterY + blendingSizeHalf), -90, 0, 1});
                    var3.getBuffer().addVertexData(new int[]{(int) (blendCenterX + blendingSizeHalf), (int) (blendCenterY + blendingSizeHalf), -90, 1, 1});
                    var3.getBuffer().addVertexData(new int[]{(int) (blendCenterX + blendingSizeHalf), (int) (blendCenterY - blendingSizeHalf), -90, 1, 0});
                    var3.getBuffer().addVertexData(new int[]{(int) (blendCenterX - blendingSizeHalf), (int) (blendCenterY - blendingSizeHalf), -90, 0, 0});
                    var3.draw();
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        // Reset
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean shouldApply(float ticks)
    {
        return sunFlareIntensity > 0.0f;
    }

    @Override
    public void apply(int screenWidth, int screenHeight, float ticks, IvOpenGLTexturePingPong pingPong)
    {
        pingPong.pingPong();
        IvRenderHelper.drawRectFullScreen(screenWidth, screenHeight);

        renderLensFlares(screenWidth, screenHeight, 1.0f);
    }

    @Override
    public void destruct()
    {

    }
}
