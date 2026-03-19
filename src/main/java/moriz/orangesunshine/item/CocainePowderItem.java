package moriz.orangesunshine.item;

import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

/**
 * Created by lukas on 14.11.14.
 */
public class CocainePowderItem extends EdibleItem {
    public CocainePowderItem(Item.Properties settings, DrugInfluence influence) {
        super(settings, influence);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }
}
