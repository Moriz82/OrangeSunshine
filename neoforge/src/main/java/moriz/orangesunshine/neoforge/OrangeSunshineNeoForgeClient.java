package moriz.orangesunshine.neoforge;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.OrangeSunshinePlatform;
import moriz.orangesunshine.block.PSBlocks;
import moriz.orangesunshine.block.entity.FlaskBlockEntity;
import moriz.orangesunshine.block.entity.PSBlockEntities;
import moriz.orangesunshine.client.PSClientConfig;
import moriz.orangesunshine.client.item.PSModelPredicates;
import moriz.orangesunshine.client.render.DebugOverlay;
import moriz.orangesunshine.client.render.blocks.*;
import moriz.orangesunshine.client.render.shader.PSShaders;
import moriz.orangesunshine.client.screen.*;
import moriz.orangesunshine.entity.PSEntities;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.particle.PSParticles;
import moriz.orangesunshine.screen.PSScreenHandlers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@Mod(value = OrangeSunshinePlatform.MOD_ID, dist = Dist.CLIENT)
public final class OrangeSunshineNeoForgeClient {
    private static KeyMapping debugMenuKey;

    public OrangeSunshineNeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(this::onRegisterKeyMappings);
        modEventBus.addListener(this::onRegisterParticles);
        modEventBus.addListener(this::onRegisterMenuScreens);

        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Wire platform-agnostic config accessor using NeoForge config directory
            PSClientConfig.setConfigSupplier(NeoForgeConfigHolder::getConfig);

            // Wire drug properties and crosshair target suppliers
            OrangeSunshine.globalDrugProperties = () -> DrugProperties.of((net.minecraft.world.entity.Entity) Minecraft.getInstance().player);
            OrangeSunshine.crossHairTarget = () -> Optional.ofNullable(Minecraft.getInstance().hitResult);

            // Block render layers
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.DISTILLERY, ChunkSectionLayer.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.FLASK, ChunkSectionLayer.TRANSLUCENT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.FRUITING_JUNIPER_LEAVES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.WINE_GRAPE_LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MORNING_GLORY_LATTICE, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.CANNABIS, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.HOP, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.TOBACCO, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.COCA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.COFFEA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MORNING_GLORY, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.AGAVE_PLANT, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JIMSONWEEED, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.BELLADONNA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.TOMATOES, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.MASH_TUB, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_DOOR, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.JUNIPER_TRAPDOOR, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_CANNABIS, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_JUNIPER_SAPLING, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_MORNING_GLORY, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_HOP, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_TOBACCO, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_COCA, ChunkSectionLayer.CUTOUT);
            ItemBlockRenderTypes.setRenderLayer(PSBlocks.POTTED_COFFEA, ChunkSectionLayer.CUTOUT);
            OrangeSunshine.LOGGER.info("[OrangeSunshine] RENDERERS_OK");

            // Model predicates and shaders
            PSModelPredicates.bootstrap();
            PSShaders.bootstrap();
            OrangeSunshine.LOGGER.info("[OrangeSunshine] SHADERS_OK");

            // TODO: ShaderLoader.POST_EFFECTS reload listener needs a NeoForge-native
            // implementation (the Fabric version implements IdentifiableResourceReloadListener).
            // For now the post-effect shader pipeline will initialize with defaults.

            OrangeSunshine.LOGGER.info("[OrangeSunshine] CLIENT_SMOKE_OK -- NeoForge client bootstrap finished (rendering, screens, shaders registered)");
        });
    }

    /**
     * NeoForge-specific config loading. Uses FMLPaths.CONFIGDIR instead of
     * FabricLoader.getConfigDir() to locate the config file.
     */
    private static final class NeoForgeConfigHolder {
        private static final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder()
                .setLenient().setPrettyPrinting().create();
        private static PSClientConfig config;

        static PSClientConfig getConfig() {
            if (config == null) {
                java.nio.file.Path path = FMLPaths.CONFIGDIR.get().resolve("orangesunshine_client.json");
                if (java.nio.file.Files.exists(path)) {
                    try (var reader = java.nio.file.Files.newBufferedReader(path)) {
                        config = GSON.fromJson(reader, PSClientConfig.class);
                    } catch (Exception e) {
                        OrangeSunshine.LOGGER.warn("[OrangeSunshine] Failed to read client config, using defaults", e);
                    }
                }
                if (config == null) {
                    config = new PSClientConfig();
                }
                // Write back (creates file if missing, normalizes format)
                try (var writer = java.nio.file.Files.newBufferedWriter(path)) {
                    GSON.toJson(config, writer);
                } catch (Exception e) {
                    OrangeSunshine.LOGGER.warn("[OrangeSunshine] Failed to save client config", e);
                }
            }
            return config;
        }
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(PSScreenHandlers.DRYING_TABLE, DryingTableScreen::new);
        event.register(PSScreenHandlers.BARREL, BarrelScreen::new);
        event.register(PSScreenHandlers.DISTILLERY, DistilleryScreen::new);
        event.register(PSScreenHandlers.FLASK, FlaskScreen<FlaskBlockEntity>::new);
        event.register(PSScreenHandlers.MASH_TUB, MushTubScreen::new);
        event.register(PSScreenHandlers.MORTAR_PESTLE, MortarPestleScreen::new);
        event.register(PSScreenHandlers.MIXING_TABLE, MixingTableScreen::new);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entity renderers
        event.registerEntityRenderer(PSEntities.MOLOTOV_COCKTAIL, ThrownItemRenderer::new);

        // Block entity renderers
        event.registerBlockEntityRenderer(PSBlockEntities.BARREL, BarrelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.MASH_TUB, MashTubBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.MIXING_TABLE_BLOCK_ENTITY, MixingTableEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.DRYING_TABLE, DryingTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.FLASK, FlaskBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.PEYOTE, PeyoteBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.RIFT_JAR, RiftJarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.BOTTLE_RACK, BottleRackBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(PSBlockEntities.PLACED_DRINK, DrinksBlockEntityRenderer::new);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        debugMenuKey = new KeyMapping(
                "key.orangesunshine.debug",
                GLFW.GLFW_KEY_F7,
                KeyMapping.Category.register(OrangeSunshine.id("orangesunshine"))
        );
        event.register(debugMenuKey);
    }

    private void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(PSParticles.EXHALED_SMOKE,
                spriteSet -> (effect, world, x, y, z, dx, dy, dz, random) ->
                        new moriz.orangesunshine.client.particle.ExhaledSmokeParticle(
                                (moriz.orangesunshine.particle.ExhaledSmokeParticleEffect) effect,
                                spriteSet, world, x, y, z, dx, dy, dz, random));
        event.registerSpriteSet(PSParticles.BUBBLE,
                spriteSet -> (effect, world, x, y, z, dx, dy, dz, random) ->
                        new moriz.orangesunshine.client.particle.BubbleParticle(
                                (moriz.orangesunshine.particle.BubbleParticleEffect) effect,
                                spriteSet, world, x, y, z, dx, dy, dz, random));
        OrangeSunshine.LOGGER.info("[OrangeSunshine] PARTICLES_OK");
    }

    private void onClientTick(ClientTickEvent.Post event) {
        if (debugMenuKey != null) {
            while (debugMenuKey.consumeClick()) {
                DebugOverlay.isEnabled = !DebugOverlay.isEnabled;
            }
        }
    }
}
