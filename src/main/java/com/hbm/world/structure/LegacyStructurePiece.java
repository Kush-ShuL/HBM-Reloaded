package com.hbm.world.structure;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.block.Block;

/** One original sparse template; vanilla persists and clips it across chunk generation/reloads. */
public final class LegacyStructurePiece extends StructurePiece {
    private final String sourceName;
    private final int turns,expectedY;
    private volatile int baseY=Integer.MIN_VALUE;
    LegacyStructurePiece(NaturalStructures.Spawn spawn,LegacyStructureTemplate template,int turns,LevelHeightAccessor height,BlockPos position){
        // Conforming source columns and first-generation height alignment can use any Y.
        // Keep a fixed full-height reference box; never move a cached StructureStart box.
        super(NaturalStructures.PIECE.get(),0,new BoundingBox(position.getX(),height.getMinBuildHeight(),position.getZ(),position.getX()+((turns&1)==1?template.sizeZ:template.sizeX)-1,height.getMaxBuildHeight()-1,position.getZ()+((turns&1)==1?template.sizeX:template.sizeZ)-1));
        sourceName=spawn.name();this.turns=turns;expectedY=position.getY();
    }
    LegacyStructurePiece(StructurePieceSerializationContext context,CompoundTag tag){super(NaturalStructures.PIECE.get(),tag);sourceName=tag.getString("source");turns=tag.getInt("turns");expectedY=tag.getInt("expectedY");baseY=tag.contains("baseY")?tag.getInt("baseY"):Integer.MIN_VALUE;}
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context,CompoundTag tag){tag.putString("source",sourceName);tag.putInt("turns",turns);tag.putInt("expectedY",expectedY);if(baseY!=Integer.MIN_VALUE)tag.putInt("baseY",baseY);}
    @Override public BlockPos getLocatorPosition(){return new BlockPos(boundingBox.minX(),baseY==Integer.MIN_VALUE?expectedY:baseY,boundingBox.minZ());}
    @Override public void postProcess(WorldGenLevel level,StructureManager manager,ChunkGenerator generator,RandomSource random,BoundingBox clipping,ChunkPos chunk,BlockPos pivot){
        if(!NaturalStructures.enabled()||level.getLevel().dimension()!=Level.OVERWORLD)return;
        var spawn=NaturalStructures.spawn(sourceName);var template=LegacyStructureTemplate.get(spawn.template());var states=template.states(turns);
        if(!spawn.conform()&&baseY==Integer.MIN_VALUE)synchronized(this){if(baseY==Integer.MIN_VALUE){
            long total=0;int count=0;for(int z=clipping.minZ();z<=clipping.maxZ();z++)for(int x=clipping.minX();x<=clipping.maxX();x++){total+=topSolid(level,x,z);count++;}
            int average=(count==0?64:(int)(total/count))+spawn.heightOffset();
            baseY=generator instanceof FlatLevelSource?average:Math.clamp(average,spawn.minHeight(),spawn.maxHeight());
        }
        }
        var columnBases=new HashMap<Long,Integer>();var placed=new ArrayList<BlockPos>();
        for(var cell:template.cells){
            int x=boundingBox.minX()+template.x(cell,turns),z=boundingBox.minZ()+template.z(cell,turns);
            if(x<clipping.minX()||x>clipping.maxX()||z<clipping.minZ()||z>clipping.maxZ())continue;
            int floor=spawn.conform()?columnBases.computeIfAbsent(BlockPos.asLong(x,0,z),key->topSolid(level,x,z)+spawn.heightOffset()):baseY;
            int y=floor+cell.y();if(y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight())continue;
            var pos=new BlockPos(x,y,z);var state=states.get(cell.palette());level.setBlock(pos,state,2);placed.add(pos);
            if(state.getBlock() instanceof net.minecraft.world.level.block.StairBlock||state.getBlock() instanceof net.minecraft.world.level.block.CrossCollisionBlock)level.getChunk(pos).markPosForPostprocessing(pos);
        }
        // Legacy stairs/bars derived their join geometry from live neighbors. Modern
        // states encode those joins; refresh only authored cells after all are placed.
        for(var pos:placed){var state=level.getBlockState(pos);var updated=Block.updateFromNeighbourShapes(state,level,pos);if(updated!=state)level.setBlock(pos,updated,2);}
    }
    @SuppressWarnings("deprecation") private static int topSolid(WorldGenLevel level,int x,int z){
        int y=level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z)-1;
        for(;y>level.getMinBuildHeight();y--){var state=level.getBlockState(new BlockPos(x,y,z));if(state.blocksMotion()&&!state.is(BlockTags.LEAVES))return y+1;}return level.getMinBuildHeight()-1;
    }
}
