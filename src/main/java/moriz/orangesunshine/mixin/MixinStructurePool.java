package moriz.orangesunshine.mixin;

import java.util.List;

import moriz.orangesunshine.world.gen.structure.MutableStructurePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@Mixin(StructureTemplatePool.class)
interface MixinStructurePool extends MutableStructurePool {
    @Override
    @Accessor("templates")
    ObjectArrayList<StructurePoolElement> getElements();

    @Override
    @Accessor("templates")
    @Mutable
    void setElements(ObjectArrayList<StructurePoolElement> elements);

    @Override
    @Accessor("rawTemplates")
    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    @Override
    @Accessor("rawTemplates")
    @Mutable
    void setElementCounts(List<Pair<StructurePoolElement, Integer>> elementCounts);
}
