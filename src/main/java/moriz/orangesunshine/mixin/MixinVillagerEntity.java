package moriz.orangesunshine.mixin;

import moriz.orangesunshine.PSDamageTypes;
import moriz.orangesunshine.advancement.PSCriteria;
import moriz.orangesunshine.entity.PSTradeOffers;
import moriz.orangesunshine.item.PSItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

@Mixin(Villager.class)
abstract class MixinVillagerEntity extends AbstractVillager {
    MixinVillagerEntity() { super(null, null); }

    @Inject(method = "mobInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInteractMob(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        ItemStack stack = player.getItemInHand(hand);
        Villager villager = (Villager)(Object)this;
        var villagerData = villager.getVillagerData();
        if (stack.is(PSItems.HASH_MUFFIN) && !isBaby()) {
            if (villagerData.profession().is(net.minecraft.world.entity.npc.villager.VillagerProfession.NITWIT)
                    || villagerData.profession().is(net.minecraft.world.entity.npc.villager.VillagerProfession.NONE)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (!level().isClientSide()) {
                    level().playSound(null, this, SoundEvents.GENERIC_EAT.value(), getSoundSource(),
                            1 + random.nextFloat(),
                            random.nextFloat() * 0.7F + 0.3F
                    );
                    villager.setVillagerData(villagerData.withProfession(level().registryAccess(), PSTradeOffers.DRUG_ADDICT_PROFESSION));
                    villager.refreshBrain((ServerLevel)level());
                    PSCriteria.FEED_VILLAGER.trigger(player);
                }
                info.setReturnValue(InteractionResult.SUCCESS);
            } else {
                info.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Inject(method = "rewardTradeXp(Lnet/minecraft/world/item/trading/MerchantOffer;)V", at = @At("RETURN"))
    private void onAfterUsing(MerchantOffer offer, CallbackInfo info) {
        if (!level().isClientSide() && ((Villager)(Object)this).getVillagerData().profession().is(PSTradeOffers.DRUG_ADDICT_PROFESSION)) {
            hurtServer((ServerLevel)level(), PSDamageTypes.create(level(), PSDamageTypes.OVERDOSE), (offer.getUses() * offer.getResult().getCount()) + 1);
        }
    }
}
