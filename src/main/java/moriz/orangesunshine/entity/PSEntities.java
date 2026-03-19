/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity;

import moriz.orangesunshine.OrangeSunshine;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Created by lukas on 25.04.14.
 *
 * Updated by Sollace on 1 Jan 2023
 * Updated by Moriz starting 5/19/2024
 */
public interface PSEntities {
    EntityType<MolotovCocktailEntity> MOLOTOV_COCKTAIL = register("molotov_cocktail", FabricEntityTypeBuilder.<MolotovCocktailEntity>create(MobCategory.MISC, MolotovCocktailEntity::new)
            .trackedUpdateRate(10).trackRangeBlocks(64)
            .dimensions(EntityDimensions.fixed(0.1F, 0.1F)));
    EntityType<RealityRiftEntity> REALITY_RIFT = register("reality_rift", FabricEntityTypeBuilder.create(MobCategory.MISC, RealityRiftEntity::new)
            .trackedUpdateRate(3).trackRangeBlocks(80)
            .dimensions(EntityDimensions.fixed(2F, 2F)));

    static <T extends Entity> EntityType<T> register(String name, FabricEntityTypeBuilder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, OrangeSunshine.id(name));
        EntityType<T> type = builder.build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, OrangeSunshine.id(name), type);
    }

    static void bootstrap() {
        PSTradeOffers.bootstrap();
    }
}
