package moriz.orangesunshine.entity.drug;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.item.PSItems;
import moriz.orangesunshine.item.PaperBagItem;
import moriz.orangesunshine.util.NbtSerialisable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class Stomach implements NbtSerialisable {

    private final DrugProperties properties;

    private int vomitCount;
    private int vomitCooldown;
    private int vomitingTicks;

    private final Player entity;

    public Stomach(DrugProperties properties) {
        this.properties = properties;
        this.entity = properties.asEntity();
    }

    public LockableHungerManager getStomach() {
        return (LockableHungerManager)entity.getFoodData();
    }

    public GluttonyManager getGlut() {
        return (GluttonyManager)entity.getFoodData();
    }

    public void onTick() {
        final float hungerSuppression = Mth.clamp(properties.getModifier(Drug.HUNGER_SUPPRESSION), -1, 1);
        final boolean shouldLockHunger = Math.abs(hungerSuppression) > Mth.EPSILON;

        if (shouldLockHunger != (getStomach().getLockedState() != null)) {
            if (shouldLockHunger) {
                getStomach().lockHunger(hungerSuppression > 0, hungerSuppression);
            } else {
                getStomach().unlockHunger();
            }

            properties.markDirty();
        }

        if (getStomach().getLockedState() != null) {
            getStomach().getLockedState().setRate(hungerSuppression);
        }

        if (getGlut().getOvereating() >= 10) {
            vomit();
        }

        if (vomitingTicks > 0) {
            RandomSource random = entity.getRandom();
            if (entity.tickCount % (int)(1 + random.nextFloat() * 3) == 0) {
                int count = (int)(random.nextFloat() * (vomitingTicks / 2));
                for (int i = 0; i < count; i++) {
                    vomitingTicks--;
                    if (!entity.level().isClientSide()) {
                        var dropped = entity.drop(PSItems.VOMIT.getDefaultInstance(), true);
                        if (dropped != null) {
                            dropped.setNeverPickUp();
                        }
                        playBarfNoise();
                    }
                }
                properties.markDirty();
            }
        }
        if (vomitCooldown > 0) {
            properties.markDirty();
            if (vomitCooldown-- <= 0) {
                vomitCount = 0;
            }
        }
    }

    public void vomit() {
        ItemStack heldItem = entity.getItemInHand(InteractionHand.OFF_HAND);
        if (heldItem.is(PSItems.PAPER_BAG) && PaperBagItem.getContents(heldItem).isEmpty()) {
            playBarfNoise();

            if (!entity.isCreative()) {
                heldItem.shrink(1);
            }
            if (heldItem.isEmpty()) {
                entity.setItemInHand(InteractionHand.OFF_HAND, PSItems.BAG_O_VOMIT.getDefaultInstance());
            } else {
                entity.getInventory().placeItemBackInInventory(PSItems.BAG_O_VOMIT.getDefaultInstance());
            }
        } else {
            vomitingTicks = Mth.nextInt(entity.getRandom(), 10, 100);
            if (++vomitCount > 16) {
                entity.hurt(properties.damageOf(PSDamageTypes.OVER_EATING), Integer.MAX_VALUE);
            }
        }
        entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 1));
        getGlut().setOvereating(0);
        properties.markDirty();
    }

    private void playBarfNoise() {
        RandomSource random = entity.getRandom();
        float pitch = 0.5F + (random.nextFloat() - random.nextFloat()) * 0.25F;
        entity.level().playSound(null, entity, SoundEvents.VILLAGER_DEATH, entity.getSoundSource(), 1, pitch);
    }

    @Override
    public void fromNbt(CompoundTag compound) {
        compound.getCompound("hunger").ifPresentOrElse(
                tag -> getStomach().setLockedState(LockableHungerManager.State.fromNbt(tag)),
                getStomach()::unlockHunger
        );
        vomitCount = compound.getIntOr("vomitCount", 0);
        vomitCooldown = compound.getIntOr("vomitCooldown", 0);
        vomitingTicks = compound.getIntOr("vomitingTicks", 0);
    }

    @Override
    public void toNbt(CompoundTag compound) {
        LockableHungerManager.State lockedHungerState = getStomach().getLockedState();
        if (lockedHungerState != null) {
            compound.put("hunger", lockedHungerState.toNbt());
        }
        compound.putInt("vomitCount", vomitCount);
        compound.putInt("vomitCooldown", vomitCooldown);
        compound.putInt("vomitingTicks", vomitingTicks);
    }
}
