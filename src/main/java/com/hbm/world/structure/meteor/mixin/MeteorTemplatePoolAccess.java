package com.hbm.world.structure.meteor.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The original pool removes a whole weighted entry after each attempted piece. */
@Mixin(value=StructureTemplatePool.class,remap=false)
public interface MeteorTemplatePoolAccess {
    @Accessor("rawTemplates") List<Pair<StructurePoolElement,Integer>> hbm$rawTemplates();
}
