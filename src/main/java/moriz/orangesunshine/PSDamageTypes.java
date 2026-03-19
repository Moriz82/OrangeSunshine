package moriz.orangesunshine;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface PSDamageTypes {
    List<ResourceKey<DamageType>> REGISTRY = new ArrayList<>();

    ResourceKey<DamageType> ALCOHOL_POSIONING = register("alcohol_poisoning");
    ResourceKey<DamageType> RESPIRATORY_FAILURE = register("respiratory_failure");
    ResourceKey<DamageType> STROKE = register("stroke");
    ResourceKey<DamageType> HEART_FAILURE = register("heart_failure");
    ResourceKey<DamageType> HEART_ATTACK = register("heart_attack");
    ResourceKey<DamageType> KIDNEY_FAILURE = register("kidney_failure");
    ResourceKey<DamageType> IN_SLEEP = register("in_sleep");
    ResourceKey<DamageType> OVER_EATING = register("over_eating");
    ResourceKey<DamageType> MOLOTOV = register("molotov");
    ResourceKey<DamageType> SELF_MOLOTOV = register("self_molotov");
    ResourceKey<DamageType> OVERDOSE = register("overdose");

    static ResourceKey<DamageType> molotov(Entity target, @Nullable Entity attacker) {
        return target == attacker ? SELF_MOLOTOV : MOLOTOV;
    }

    static DamageSource create(Level world, ResourceKey<DamageType> type) {
        return new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type));
    }

    static DamageSource create(Level world, Entity source, @Nullable Entity attacker, ResourceKey<DamageType> type) {
        return new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type), source, attacker);
    }

    private static ResourceKey<DamageType> register(String name) {
        var key = ResourceKey.create(Registries.DAMAGE_TYPE, OrangeSunshine.id(name));
        REGISTRY.add(key);
        return key;
    }

    static void bootstrap() {
        DynamicRegistrySetupCallback.EVENT.register(registries -> {
            registries.getOptional(Registries.DAMAGE_TYPE).ifPresent(registry -> {
                REGISTRY.forEach(key -> {
                    Registry.register(registry, key.identifier(), new DamageType(key.identifier().getNamespace() + "." + key.identifier().getPath(), 0));
                });
            });
        });
    }
}
