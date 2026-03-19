package moriz.orangesunshine.mixin;

import moriz.orangesunshine.entity.AddictTaskListProvider;
import moriz.orangesunshine.entity.DealerTaskListProvider;
import moriz.orangesunshine.entity.PSTradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

@Mixin(ResetProfession.class)
abstract class MixinLoseJobOnSiteLossTask {
    @Inject(method = "lambda$create$0", at = @At("HEAD"), cancellable = true)
    private static void onTryLoseJobSite(ServerLevel world, Villager entity, long time,
            CallbackInfoReturnable<Boolean> info) {
        if (entity.getVillagerData().profession().is(PSTradeOffers.DRUG_ADDICT_PROFESSION)) {
            info.setReturnValue(false);
        }
    }
}

@Mixin(VillagerGoalPackages.class)
abstract class MixinVillagerTaskListProvider {
    @Shadow
    private static Pair<Integer, BehaviorControl<LivingEntity>> getFullLookBehavior() { return null; }

    @Inject(method = "getWorkPackage(Lnet/minecraft/core/Holder;F)Lcom/google/common/collect/ImmutableList;", at = @At("HEAD"), cancellable = true)
    private static void onCreateWorkTasks(Holder<VillagerProfession> profession, float speed,
            CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> info) {
        if (profession.is(PSTradeOffers.DRUG_DEALER_PROFESSION)) {
            info.setReturnValue(DealerTaskListProvider.createWorkTasks(getFullLookBehavior(), speed));
        }
        if (profession.is(PSTradeOffers.DRUG_ADDICT_PROFESSION)) {
            info.setReturnValue(AddictTaskListProvider.createWorkTasks(getFullLookBehavior(), speed));
        }
    }
}
