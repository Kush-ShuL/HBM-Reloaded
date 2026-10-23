package com.hbm.world.structure.tandem;

import com.hbm.item.structuretandem.TandemBlock;
import com.hbm.item.structuretandem.TandemContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Source buildTileEntity arms only the actual tandem; no ordinary controller or jigsaw is substituted. */
public final class TandemStructureProcessor extends StructureProcessor {
    public static final TandemStructureProcessor INSTANCE=new TandemStructureProcessor();
    public static final MapCodec<TandemStructureProcessor> CODEC=MapCodec.unit(INSTANCE);
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo current,StructurePlaceSettings settings){
        if(!(current.state().getBlock() instanceof TandemBlock))return current;
        var tag=current.nbt()==null?new net.minecraft.nbt.CompoundTag():current.nbt().copy();
        String condition=TandemSourceScope.condition();
        tag.putBoolean("armed",condition!=null);tag.putString("condition",condition==null?"":condition);
        // A newly built tile is a new source action, not a duplicate of an in-flight current-world operation.
        tag.remove("pending");
        return new StructureBlockInfo(current.pos(),current.state(),tag);
    }
    @Override public List<StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level,BlockPos origin,BlockPos pivot,List<StructureBlockInfo> originals,List<StructureBlockInfo> processed,StructurePlaceSettings settings){
        // Source transforms/selects first, then skips y<1. Apply after the real spike/conform processor;
        // never suppress its RNG or interfere with332's separate null-context wipe/low-Y rules.
        return TandemSourceScope.condition()==null?processed:processed.stream().filter(info->info.pos().getY()>=1).toList();
    }
    @Override protected StructureProcessorType<?> getType(){return TandemContent.PROCESSOR.get();}
    private TandemStructureProcessor(){}
}
