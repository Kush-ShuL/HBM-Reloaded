package com.hbm.world.structure.meteor;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** The source spike follows each solid/non-leaf column at heightOffset=-3. */
public final class MeteorSpikeProcessor extends StructureProcessor {
    public static final MeteorSpikeProcessor INSTANCE=new MeteorSpikeProcessor();
    public static final MapCodec<MeteorSpikeProcessor> CODEC=MapCodec.unit(INSTANCE);
    @Override public List<StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level,BlockPos origin,BlockPos pivot,List<StructureBlockInfo> originals,List<StructureBlockInfo> processed,StructurePlaceSettings settings){
        var floors=new HashMap<Long,Integer>();
        //Snapshot every height before any cell in this piece is placed. Reading the height
        //again after building a lower cell would make its own pillar grow on itself.
        for(var info:processed){
            int x=info.pos().getX(),z=info.pos().getZ();
            var clip=settings.getBoundingBox();if(clip!=null&&(x<clip.minX()||x>clip.maxX()||z<clip.minZ()||z>clip.maxZ()))continue;
            long key=BlockPos.asLong(x,0,z);
            floors.computeIfAbsent(key,ignored->{
                int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
                var cursor=new BlockPos.MutableBlockPos(x,y,z);
                while(y>=level.getMinBuildHeight()){
                    cursor.setY(y);var state=level.getBlockState(cursor);
                    if(state.blocksMotion()&&!state.is(BlockTags.LEAVES))return y+1;
                    y--;
                }
                return level.getMinBuildHeight();
            });
        }
        var result=new ArrayList<StructureBlockInfo>(processed.size());
        for(var info:processed){
            var floor=floors.get(BlockPos.asLong(info.pos().getX(),0,info.pos().getZ()));if(floor==null)continue;
            int y=floor-3+info.pos().getY()-origin.getY();
            if(y<1||y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight())continue;
            result.add(new StructureBlockInfo(new BlockPos(info.pos().getX(),y,info.pos().getZ()),info.state(),info.nbt()));
        }
        return result;
    }
    @Override protected StructureProcessorType<?> getType(){return MeteorProcessors.SPIKE.get();}
    private MeteorSpikeProcessor(){}
}
