/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.hallucination;

import moriz.orangesunshine.PSTags;
import moriz.orangesunshine.entity.TouchingWaterAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntityHallucination extends AbstractEntityHallucination {
    private float rotationYawPlus;

    public EntityHallucination(Player player) {
        this(player, PSTags.Entities.SINGLE_ENTITY_HALLUCINATIONS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EntityHallucination(Player player, TagKey<EntityType<?>> entityTypes) {
        super(player, player.level().registryAccess().lookupOrThrow(Registries.ENTITY_TYPE)
                .getRandomElementOf(entityTypes, player.getRandom())
                .map(Holder::value)
                .orElse((EntityType)EntityType.PIG)
                .create(player.level(), EntitySpawnReason.LOAD));

        entity.setPos(
                player.getX() + random.nextDouble() * 50D - 25D,
                player.getY() + random.nextDouble() * 10D - 5D,
                player.getZ() + random.nextDouble() * 50D - 25D
        );
        entity.setDeltaMovement(
                (random.nextDouble() - 0.5D) / 10D,
                (random.nextDouble() - 0.5D) / 10D,
                (random.nextDouble() - 0.5D) / 10D
        );
        entity.setYRot(random.nextInt(360));
        maxAge = (random.nextInt(59) + 3) * 20;
        rotationYawPlus = random.nextFloat() * 10 * (random.nextBoolean() ? 0 : 1);

        color = new float[] {
            random.nextFloat(),
            random.nextFloat(),
            random.nextFloat()
        };

        scale = 1;
        while (random.nextFloat() < 0.3F) {
            scale *= random.nextFloat() * 2.7f + 0.3F;
        }
        scale = Math.min(scale, 20);
    }

    @Override
    protected void animateEntity() {
        entity.setPos(entity.position().add(entity.getDeltaMovement()));
        entity.setYRot(Mth.wrapDegrees(entity.getYRot() + rotationYawPlus));
        if (entity instanceof LivingEntity l && l.canBreatheUnderwater()) {
           ((TouchingWaterAccessor)entity).setTouchingWater(true);
        }
    }
}
