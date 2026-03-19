/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.hallucination;

import java.util.Optional;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RastaHeadHallucination extends AbstractEntityHallucination {
    private final LookControl lookControl;

    private final float distance;

    private final float planeRotationX;
    private final float planeRotationZ;

    public RastaHeadHallucination(Player playerEntity) {
        super(playerEntity, EntityType.PIG.create(playerEntity.level(), EntitySpawnReason.LOAD));

        maxAge = (random.nextInt(59) + 120) * 20;
        scale = 1 + random.nextFloat() / 2F;
        distance = 2 + random.nextFloat() * 5;

        planeRotationX = random.nextFloat() * Mth.HALF_PI;
        planeRotationZ = random.nextFloat() * Mth.HALF_PI;

        entity.setPos(playerEntity.position());
        lookControl = ((Mob)entity).getLookControl();

        chatBot = Optional.of(new ChatBot(new RastaheadPersonality(), playerEntity));
    }

    @Override
    protected void animateEntity() {
        this.lookControl.setLookAt(player);
        this.lookControl.tick();

        int seed = player.tickCount + (entity.getId() * 3);

        Vec3 offset = new Vec3(
                Mth.sin(seed / 50F) * distance,
                Mth.sin(seed / 10F) + (entity.getId() % 5) - 1,
                Mth.cos(seed / 50F) * distance
        ).yRot(planeRotationX).zRot(planeRotationZ);

        Vec3 wanted = player.getEyePosition().add(offset);

        double totalDist = wanted.distanceTo(entity.position());

        Vec3 vel = entity.getDeltaMovement().scale(0.9D);

        vel = wanted.subtract(entity.position()).normalize().scale(Math.log((float)totalDist));

        entity.setDeltaMovement(vel);
        entity.setPos(entity.position().add(vel));
    }
}
