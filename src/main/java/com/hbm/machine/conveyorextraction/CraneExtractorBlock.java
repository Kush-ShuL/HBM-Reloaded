package com.hbm.machine.conveyorextraction;

import com.hbm.conveyor.crane.*;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Extractor is not an enterable block; the source reverses the visible in/out arrows. */
public final class CraneExtractorBlock extends CraneBaseBlock {
    @Override public CraneExtractorEntity createCrane(BlockPos pos,BlockState state){return new CraneExtractorEntity(pos,state);}
    @Override public String[] sourceTextures(){return CraneExtractionIcons.EXTRACTOR.clone();}
    @Override public boolean canConnectRedstone(BlockState state,net.minecraft.world.level.BlockGetter world,BlockPos pos,Direction side){return true;}
    @Override public int sourceRotation(int meta,int override,int side){
        if(meta>1&&side==1){int hand=handRotation(override<6?direction(override):CraneBaseEntity.opposite(direction(meta)),direction(meta));
            if(hand==1)return switch(meta){case 2->2;case 3->1;case 4->3;default->0;};
            if(hand==0)return switch(meta){case 2->1;case 3->2;case 4->0;case 5->3;default->0;};
            return switch(meta){case 2->0;case 3->3;case 4->2;case 5->1;default->0;};
        }return 0;
    }
    @Override protected void dropContents(Level world,BlockPos pos,CraneBaseEntity crane,int start,int end){super.dropContents(world,pos,crane,9,20);}
}
