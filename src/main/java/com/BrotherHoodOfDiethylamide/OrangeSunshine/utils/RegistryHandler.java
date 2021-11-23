package com.BrotherHoodOfDiethylamide.OrangeSunshine.utils;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.OrangeSunshine;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.blocks.ComplexBlocks;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.effects.particles.Smoke;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.registry.StackzProfession;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.tradelists.TradelistShmokeStackz;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Block_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.init.Item_init;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.portedpsych.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(OrangeSunshine.MODID)
public class RegistryHandler {

    public static ArrayList<EntityPlayer> playerProps = new ArrayList<>();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PORTAL)
        {
            Minecraft mc = Minecraft.getMinecraft();
            Entity renderEntity = mc.getRenderViewEntity();
            DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) renderEntity);

            if (drugProperties != null && drugProperties.renderer != null)
            {
                drugProperties.renderer.renderOverlaysAfterShaders(event.getPartialTicks(), (EntityLivingBase) renderEntity, renderEntity.ticksExisted, event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), drugProperties);
            }
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        float partialTicks = event.partialTicks;
        int rendererUpdateCount = mc.ingameGUI.getUpdateCounter();
        float ticks = partialTicks + rendererUpdateCount;

        if (event instanceof RenderWorldEvent.Pre)
        {
//            setPlayerAngles(partialTicks);

            PSRenderStates.preRender(ticks);

            for (String pass : PSRenderStates.getRenderPasses(partialTicks))
            {
                if (!pass.equals("Default"))
                {
                    if (PSRenderStates.startRenderPass(pass, partialTicks, ticks))
                    {
                        mc.entityRenderer.renderWorld(partialTicks, 0L);
                        PSRenderStates.endRenderPass();
                    }
                }
            }

            PSRenderStates.startRenderPass("Default", partialTicks, ticks);
            PSRenderStates.preRender3D(ticks);
        }
        else if (event instanceof RenderWorldEvent.Post)
        {
            PSRenderStates.endRenderPass();

            DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) mc.getRenderViewEntity());

            if (drugProperties != null && drugProperties.renderer != null)
                drugProperties.renderer.renderOverlaysBeforeShaders(event.partialTicks, (EntityLivingBase) mc.getRenderViewEntity(), rendererUpdateCount, mc.displayWidth, mc.displayHeight, drugProperties);

            PSRenderStates.postRender(ticks, partialTicks);
        }
    }

    @SubscribeEvent
    public void orientCamera(OrientCameraEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase renderEntity = (EntityLivingBase) mc.getRenderViewEntity();
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) renderEntity);
        int rendererUpdateCount = renderEntity.ticksExisted;

        if (drugProperties != null && drugProperties.renderer != null)
            drugProperties.renderer.distortScreen(event.partialTicks, renderEntity, rendererUpdateCount, drugProperties);
    }

    @SubscribeEvent
    public void psycheGLEnable(GLSwitchEvent event)
    {
        PSRenderStates.setEnabled(event.cap, event.enable);
    }

    @SubscribeEvent
    public void psycheGLBlendFunc(GLBlendFuncEvent event)
    {
        PSRenderStates.setBlendFunc(event.sFactor, event.dFactor, event.dfactorAlpha, event.dfactorAlpha);
    }

    @SubscribeEvent
    public void psycheGLActiveTexture(GLActiveTextureEvent event)
    {
        GLStateProxy.setActiveTextureUnit(event.texture);
    }

    @SubscribeEvent
    public void standardItemLighting(ItemLightingEvent event)
    {
        if (event.enable)
        {
            float var0 = 0.4F;
            float var1 = 0.6F;
            float var2 = 0.0F;

            PSRenderStates.setGLLightEnabled(true);
            Vector3d field_82884_b = new Vector3d();
            field_82884_b.x = 0.20000000298023224D;
            field_82884_b.y = 1.0D;
            field_82884_b.z = -0.699999988079071D;
            Vector3d field_82885_c = new Vector3d();
            field_82885_c.x = -0.20000000298023224D;
            field_82885_c.y = 1.0D;
            field_82885_c.z = 0.699999988079071D;
            PSRenderStates.setGLLight(0, (float) field_82884_b.x, (float) field_82884_b.y, (float) field_82884_b.z, var1, var2);
            PSRenderStates.setGLLight(1, (float) field_82885_c.x, (float) field_82885_c.y, (float) field_82885_c.z, var1, var2);
            PSRenderStates.setGLLightAmbient(var0);
        }
        else
        {
            PSRenderStates.setGLLightEnabled(false);
        }
    }

    @SubscribeEvent
    public void renderHeldItem(RenderHeldItemEvent event)
    {
        float partialTicks = event.partialTicks;

        Minecraft mc = Minecraft.getMinecraft();
        int rendererUpdateCount = mc.ingameGUI.getUpdateCounter();

        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) mc.getRenderViewEntity());

        if (drugProperties != null)
        {
            float shiftX = DrugEffectInterpreter.getHandShiftX(drugProperties, (float) rendererUpdateCount + partialTicks);
            float shiftY = DrugEffectInterpreter.getHandShiftY(drugProperties, (float) rendererUpdateCount + partialTicks);
            GL11.glTranslatef(shiftX, shiftY, 0.0f);
        }
    }

    @SubscribeEvent
    public void renderEntities(RenderEntitiesEvent event)
    {
        int pass = MinecraftForgeClient.getRenderPass();

        if (pass == 1)
        {
            Minecraft mc = Minecraft.getMinecraft();
            EntityLivingBase renderEntity = (EntityLivingBase) mc.getRenderViewEntity();
            DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) renderEntity);

            if (drugProperties != null && drugProperties.renderer != null)
            {
                drugProperties.renderer.renderAllHallucinations(event.partialTicks, drugProperties);
            }
        }
    }

    @SubscribeEvent
    public void renderHand(RenderHandEvent event)
    {
        if (event instanceof RenderHandEvent.Pre)
        {
            if (!"Default".equals(PSRenderStates.currentRenderPass))
            {
                PSRenderStates.setDepthMultiplier(0.0f);
                event.setCanceled(true);
            }
        }
        else if (event instanceof RenderHandEvent.Post)
        {
            if (!"Default".equals(PSRenderStates.currentRenderPass))
            {
                PSRenderStates.setDepthMultiplier(1.0f);
            }
        }
    }

    @SubscribeEvent
    public void setPlayerAngles(SetPlayerAnglesEvent event)
    {
        float partialTicks = event.partialTicks;

        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase renderEntity = (EntityLivingBase) mc.getRenderViewEntity();
        DrugProperties drugProperties = DrugProperties.getDrugProperties((EntityPlayer) renderEntity);

        if (drugProperties != null)
        {
            float smoothness = DrugEffectInterpreter.getSmoothVision(drugProperties);
            if (smoothness < 1.0f && mc.inGameHasFocus)
            {
                float deltaX = mc.mouseHelper.deltaX;
                float deltaY = mc.mouseHelper.deltaY;

                float[] angles = SmoothCameraHelper.instance.getAngles(mc.gameSettings.mouseSensitivity, partialTicks, deltaX, deltaY, mc.gameSettings.invertMouse);

                if (!mc.gameSettings.smoothCamera)
                {
                    float[] originalAngles = SmoothCameraHelper.instance.getOriginalAngles(mc.gameSettings.mouseSensitivity, partialTicks, deltaX, deltaY, mc.gameSettings.invertMouse);
                    renderEntity.moveToBlockPosAndAngles(renderEntity.getPosition(),angles[0] - originalAngles[0], angles[1] - originalAngles[1]);
                }
                else
                {
                    renderEntity.moveToBlockPosAndAngles(renderEntity.getPosition(),angles[0], angles[1]);
                }
            }
        }
    }

    @SubscribeEvent
    public void psycheGLFogi(GLFogiEvent event)
    {
        if (event.pname == GL11.GL_FOG_MODE)
        {
            PSRenderStates.setFogMode(event.param);
        }
    }

    @SubscribeEvent
    public void setupCameraTransform(SetupCameraTransformEvent event)
    {
        if (PSRenderStates.setupCameraTransform())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderBlockOverlay(RenderBlockOverlayEvent event)
    {
        if (!"Default".equals(PSRenderStates.currentRenderPass))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void fixGLState(GLStateFixEvent event)
    {
        PSRenderStates.setUseScreenTexCoords(false);
        PSRenderStates.setTexture2DEnabled(OpenGlHelper.defaultTexUnit, true);
    }

    @SubscribeEvent
    public void glClear(GLClearEvent event)
    {
        event.currentMask = event.currentMask & PSRenderStates.getCurrentAllowedGLDataMask();
    }

    @SubscribeEvent
    public void renderSkyPre(RenderSkyEvent.Pre event)
    {
        PSRenderStates.preRenderSky(event.partialTicks);
    }






    public static StackzProfession shmokestackz = new StackzProfession(OrangeSunshine.MODID+":shmoke_stackz", OrangeSunshine.MODID+":textures/entity/shmoke_stackz.png", OrangeSunshine.MODID+":textures/entity/shmoke_stackz.png");
//make multiple profession to override old ones
    @SubscribeEvent
    public static void addProfessions(final RegistryEvent.Register<VillagerRegistry.VillagerProfession> villagerProfessionRegister){
        villagerProfessionRegister.getRegistry().register(shmokestackz);
        VillagerRegistry.VillagerCareer career = new VillagerRegistry.VillagerCareer(shmokestackz, "Shmoke_Stackz");
        career.addTrade(1, new TradelistShmokeStackz());
        shmokestackz.getId();
    }

    public static boolean isSmoke = false;
    public static EntityLivingBase smokePlayer = null;
    static int currTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (isSmoke && smokePlayer != null){
            Smoke.doEffect(smokePlayer, smokePlayer.world);
            currTick++;
            if (currTick >= 100){
                isSmoke = false;
                currTick = 0;
            }
        }
        DrugProperties drugProperties = DrugProperties.getDrugProperties(event.player);

        if (drugProperties == null)
            DrugProperties.initInEntity(event.player);

        if (drugProperties != null)
            drugProperties.updateDrugEffects(event.player);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(Item_init.ITEMS.toArray(new Item[0]));
        ComplexBlocks.registerItemBlocks(event.getRegistry());
        ComplexBlocks.registerModels();
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(Block_init.BLOCKS.toArray(new Block[0]));
        ComplexBlocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for(Item item : Item_init.ITEMS) {
            if(item instanceof IHasModel) {
                ((IHasModel)item).registerModels();
            }
        }

        for(Block block : Block_init.BLOCKS) {
            if(block instanceof IHasModel) {
                ((IHasModel)block).registerModels();
            }
        }
    }
}