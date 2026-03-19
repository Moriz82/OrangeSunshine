/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.fluid.Combustable;
import moriz.orangesunshine.item.PSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MolotovCocktailEntity extends ThrowableItemProjectile {

    public MolotovCocktailEntity(EntityType<MolotovCocktailEntity> type, Level level) {
        super(type, level);
    }

    public MolotovCocktailEntity(Level level, LivingEntity owner) {
        super(PSEntities.MOLOTOV_COCKTAIL, owner, level, PSItems.MOLOTOV_COCKTAIL.getDefaultInstance());
    }

    public MolotovCocktailEntity(Level level, double x, double y, double z) {
        super(PSEntities.MOLOTOV_COCKTAIL, x, y, z, level, PSItems.MOLOTOV_COCKTAIL.getDefaultInstance());
    }

    @Override
    protected Item getDefaultItem() {
        return PSItems.MOLOTOV_COCKTAIL;
    }

    @Override
    public ItemStack getItem() {
        ItemStack stack = super.getItem();
        CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        customData.putBoolean("flying", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        return stack;
    }

    @Override
    public void tick() {
        super.tick();
        if (Combustable.fromStack(getItem()).getFireStrength(getItem()) > 0) {
            spawnParticles(1);
        }
    }

    private void spawnParticles(float spread) {
        level().addParticle(ParticleTypes.FLAME,
                level().getRandom().triangle(getX(), 0.5 * spread),
                level().getRandom().triangle(getY(), 0.5 * spread),
                level().getRandom().triangle(getZ(), 0.5 * spread),
                0, 0, 0
        );

        level().addParticle(ParticleTypes.LAVA,
                level().getRandom().triangle(getX(), 0.5 * spread),
                level().getRandom().triangle(getY() + getBbHeight(), 0.5 * spread),
                level().getRandom().triangle(getZ(), 0.5 * spread),
                0, 0, 0
        );
    }


    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        damageEntity(hitResult.getEntity(), 1);
        float explosionStrength = Combustable.fromStack(getItem()).getExplosionStrength(getItem());
        if (explosionStrength > 0) {
            level().getEntities(this, getBoundingBox().inflate(explosionStrength), entity -> entity.distanceTo(this) <= explosionStrength).forEach(entity -> {
                damageEntity(entity, 1 - (entity.distanceTo(this) / explosionStrength));
            });
        }
    }

    private void damageEntity(Entity entity, float percentageScale) {
        Combustable combustable = Combustable.fromStack(getItem());
        float explosionStrength = combustable.getExplosionStrength(getItem());
        float fireStrength = combustable.getFireStrength(getItem());
        entity.hurt(PSDamageTypes.create(level(), getOwner(), this, PSDamageTypes.molotov(entity, getOwner())), percentageScale * Math.max(4, explosionStrength * 0.6F + fireStrength * 0.3F));
        if (fireStrength > 0) {
            entity.igniteForSeconds(Math.max(1F, Math.max(10F, 3F * fireStrength) / 20F));
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (level().isClientSide() || isRemoved()) {
            return;
        }

        playSound(SoundEvents.GLASS_BREAK, 1, 1);

        Combustable combustable = Combustable.fromStack(getItem());
        float explosionStrength = combustable.getExplosionStrength(getItem());
        float fireStrength = combustable.getFireStrength(getItem());

        if (fireStrength > 0) {
            for (int i = 0; i < fireStrength * 2; i++) {
                level().addParticle(ParticleTypes.FLAME,
                        level().getRandom().triangle(getX(), 0.5 * fireStrength),
                        level().getRandom().triangle(getY(), 0.5 * fireStrength),
                        level().getRandom().triangle(getZ(), 0.5 * fireStrength),
                        0, 0, 0
                );

                level().addParticle(ParticleTypes.LAVA,
                        level().getRandom().triangle(getX(), 0.5 * fireStrength),
                        level().getRandom().triangle(getY() + getBbHeight(), 0.5 * fireStrength),
                        level().getRandom().triangle(getZ(), 0.5 * fireStrength),
                        0, 0, 0
                );
            }
        }

        if (explosionStrength > 0) {
            level().explode(
                    this,
                    damageSources().thrown(this, getOwner()),
                    new ExplosionDamageCalculator() {
                        @Override
                        public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power) {
                            return state.canBeReplaced();
                        }
                    },
                    getX(),
                    getY(),
                    getZ(),
                    explosionStrength,
                    fireStrength > 0,
                    Level.ExplosionInteraction.MOB
            );
        } else {
            playSound(SoundEvents.FIRE_EXTINGUISH, 1, 1);
        }

        discard();
    }
}
