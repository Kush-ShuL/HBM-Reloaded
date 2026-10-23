package com.hbm.door.sourceseal;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Only original absolute x1/y1/z1; native BlockEntity owns mandatory type/position envelope. No slots/GUI/lock/energy. */
public final class SourceSealHatchEntity extends BlockEntity {
    public int x,y,z;
    public SourceSealHatchEntity(BlockPos pos,BlockState state){super(SourceSealContent.ENTITY.get(),pos,state);}
    public BlockPos controller(){return new BlockPos(x,y,z);}
    public void setControllerPos(BlockPos controller){x=controller.getX();y=controller.getY();z=controller.getZ();setChanged();}
    public static void tick(Level level,BlockPos pos,BlockState state,SourceSealHatchEntity hatch){if(level instanceof ServerLevel server)SourceSealActions.validate(server,pos,hatch.controller());}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("x1",x);tag.putInt("y1",y);tag.putInt("z1",z);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);x=tag.getInt("x1");y=tag.getInt("y1");z=tag.getInt("z1");}
}
