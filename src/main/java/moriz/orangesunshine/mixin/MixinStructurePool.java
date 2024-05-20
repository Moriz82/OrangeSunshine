package moriz.orangesunshine.mixin;

import java.util.List;

import moriz.orangesunshine.world.gen.structure.MutableStructurePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;

@Mixin(StructurePool.class)
interface MixinStructurePool extends MutableStructurePool {
    @Override
    @Accessor
    ObjectArrayList<StructurePoolElement> getElements();

    @Override
    @Accessor
    @Mutable
    void setElements(ObjectArrayList<StructurePoolElement> elements);

    @Override
    @Accessor
    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    @Override
    @Accessor
    @Mutable
    void setElementCounts(List<Pair<StructurePoolElement, Integer>> elementCounts);
}
