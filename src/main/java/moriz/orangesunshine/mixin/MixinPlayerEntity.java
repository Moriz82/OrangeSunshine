package moriz.orangesunshine.mixin;

import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugPropertiesContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import moriz.orangesunshine.entity.drug.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(Player.class)
abstract class MixinPlayerEntity extends LivingEntity implements DrugPropertiesContainer {
    MixinPlayerEntity() {super(null, null);}

    @Nullable
    private DrugProperties drugProperties;

    @Override
    public DrugProperties getDrugProperties() {
        if (drugProperties == null) {
            drugProperties = new DrugProperties((Player)(Object)this);
        }
        return drugProperties;
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void afterTick(CallbackInfo info) {
        getDrugProperties().onTick();
    }

    @Inject(method = "stopSleepInBed(ZZ)V", at = @At("HEAD"), cancellable = true)
    private void onWakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo info) {
        if (!getDrugProperties().onAwoken()) {
            info.cancel();
        }
    }

    @Inject(method = "startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;",
            at = @At("HEAD"),
            cancellable = true)
    private void onTrySleep(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> info) {
        if (!level().isClientSide()) {
            getDrugProperties().trySleep(pos).ifPresent(reason -> {
                ((Player)(Object)this).displayClientMessage(reason, true);

                info.setReturnValue(Either.right(Unit.INSTANCE));
            });
        }
    }

    @Inject(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", at = @At("RETURN"), cancellable = true)
    private void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(info.getReturnValue() * getDrugProperties().getModifier(Drug.DIG_SPEED));
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("HEAD"))
    private void onWriteCustomDataToTag(ValueOutput output, CallbackInfo info) {
        output.store("orangesunshine_drug_properties", CompoundTag.CODEC, getDrugProperties().toNbt());
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("HEAD"))
    private void onReadCustomDataFromTag(ValueInput input, CallbackInfo info) {
        input.read("orangesunshine_drug_properties", CompoundTag.CODEC)
                .ifPresent(tag -> getDrugProperties().fromNbt(tag));
    }
}
