/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity;

import moriz.orangesunshine.OrangeSunshine;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Created by lukas on 25.04.14.
 *
 * Updated by Sollace on 1 Jan 2023
 * Updated by Moriz starting 5/19/2024
 */
public interface PSEntities {
    EntityType<MolotovCocktailEntity> MOLOTOV_COCKTAIL = register("molotov_cocktail",
            EntityType.Builder.<MolotovCocktailEntity>of(MolotovCocktailEntity::new, MobCategory.MISC)
                    .updateInterval(10).clientTrackingRange(64).sized(0.1F, 0.1F));
    EntityType<RealityRiftEntity> REALITY_RIFT = register("reality_rift",
            EntityType.Builder.<RealityRiftEntity>of(RealityRiftEntity::new, MobCategory.MISC)
                    .updateInterval(3).clientTrackingRange(80).sized(2F, 2F));

    static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, OrangeSunshine.id(name));
        EntityType<T> type = builder.build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, OrangeSunshine.id(name), type);
    }

    static void bootstrap() {
        // Force eager entity type initialization during common bootstrap so
        // loader-specific client events don't trigger late registry writes.
        EntityType<?> ignoredMolotov = MOLOTOV_COCKTAIL;
        EntityType<?> ignoredRift = REALITY_RIFT;
        PSTradeOffers.bootstrap();
    }
}
