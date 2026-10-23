package com.hbm.world.structure.mixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Native palettes group solid/other/BE cells while X5846 built x,z,y. Restore
 * that order before processors consume the original loot RNG, for marked HBM
 * templates only; vanilla and other mods' unmarked templates retain their order. */
@Mixin(value=StructureTemplate.class,remap=false)
public abstract class NativeSourceOrderMixin {
    @ModifyVariable(method="processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;",at=@At("HEAD"),argsOnly=true,index=4)
    private static List<StructureTemplate.StructureBlockInfo> hbm$sourceOrder(List<StructureTemplate.StructureBlockInfo> blocks){
        if(blocks.stream().noneMatch(block->order(block.nbt())!=Integer.MAX_VALUE))return blocks;
        var ordered=new ArrayList<>(blocks);ordered.sort(Comparator.comparingInt(block->order(block.nbt())));return ordered;
    }
    private static int order(CompoundTag tag){
        if(tag==null)return Integer.MAX_VALUE;
        if(tag.contains("hbm_source_block_order",Tag.TAG_INT))return tag.getInt("hbm_source_block_order");
        if(tag.contains("hbm_legacy_state",Tag.TAG_COMPOUND))return tag.getCompound("hbm_legacy_state").getInt("order");
        return Integer.MAX_VALUE;
    }
}
