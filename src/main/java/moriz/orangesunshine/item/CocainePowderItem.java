package moriz.orangesunshine.item;

import moriz.orangesunshine.entity.drug.influence.DrugInfluence;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;

/**
 * Created by lukas on 14.11.14.
 */
public class CocainePowderItem extends EdibleItem {
    public CocainePowderItem(Settings settings, DrugInfluence influence) {
        super(settings, influence);
    }

    @Override
    public UseAction getUseAction(ItemStack par1ItemStack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
}
