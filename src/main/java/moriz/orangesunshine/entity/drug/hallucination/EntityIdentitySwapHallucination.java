package moriz.orangesunshine.entity.drug.hallucination;

import moriz.orangesunshine.util.Pool;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntityIdentitySwapHallucination extends Hallucination {

    private final EntityType<?> targetType;
    private final Pool<EntityType<?>> transformedType;

    @Nullable
    private Selection selection;

    public EntityIdentitySwapHallucination(Player player, EntityType<?> targetType, Pool<EntityType<?>> transformedType) {
        super(player);
        this.targetType = targetType;
        this.transformedType = transformedType;
    }

    @Nullable
    public Entity matchOrAttach(Entity entity) {

        if (entity.getType() != targetType || (selection != null && !entity.getUUID().equals(selection.selection().getUUID()))) {
            return null;
        }

        if (selection == null) {
            selection = new Selection(entity, transformedType.get(entity.level().getRandom()));
        }

        return selection.attachment();
    }

    @Override
    public void update() {
        super.update();
        if (selection != null) {
            selection.update();
        }
    }

    @Override
    public void render(Object matrices, Object vertices, Object camera, float tickDelta, float alpha) {
    }

    @Override
    public boolean isDead() {
        if (age >= 300 && selection != null) {
            selection.selection().setSilent(false);
        }
        return age > 300;
    }

    @Override
    public int getMaxHallucinations() {
        return 100;
    }

    record Selection(Entity selection, Entity attachment) {
        Selection(Entity selection, EntityType<?> attachmentType) {
            this(selection, attachmentType.create(selection.level(), EntitySpawnReason.LOAD));
            attachment.setSilent(true);
            attachment.copyPosition(selection);
        }

        public void update() {
            attachment.tickCount++;
            attachment.copyPosition(selection);
            attachment.setYRot(selection.getYRot());
            attachment.setXRot(selection.getXRot());
            attachment.xo = selection.xo;
            attachment.yo = selection.yo;
            attachment.zo = selection.zo;
            attachment.yRotO = selection.yRotO;
            attachment.xRotO = selection.xRotO;
            attachment.setOnGround(selection.onGround());

            if (attachment instanceof LivingEntity living) {
                LivingEntity selected = (LivingEntity)selection;
                living.yHeadRotO = selected.yHeadRotO;
                living.yHeadRot = selected.yHeadRot;
                living.yBodyRotO = selected.yBodyRotO;
                living.setYBodyRot(selected.yBodyRot);
            }

            attachment.tick();
        }
    }
}
