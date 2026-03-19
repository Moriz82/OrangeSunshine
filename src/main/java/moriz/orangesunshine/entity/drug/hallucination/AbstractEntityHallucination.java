/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.hallucination;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractEntityHallucination extends Hallucination {

    protected final RandomSource random;
    protected final Entity entity;

    protected int maxAge;

    protected float[] color = {1, 1, 1, 1};

    protected float scale;

    public AbstractEntityHallucination(Player player, Entity entity) {
        super(player);
        this.random = player.getRandom();
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public int getMaxHallucinations() {
        return UNLIMITED;
    }

    @Override
    public boolean isDead() {
        return age >= maxAge;
    }

    @Override
    public void update() {
        super.update();

        entity.tickCount++;
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();

        if (entity instanceof LivingEntity living) {
            living.yHeadRotO = living.yHeadRot;
        }

        animateEntity();
    }

    protected abstract void animateEntity();

    @Override
    public void render(Object matrices, Object vertices, Object camera, float tickDelta, float alpha) {
    }
}
