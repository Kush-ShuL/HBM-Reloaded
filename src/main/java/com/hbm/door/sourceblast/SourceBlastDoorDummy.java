package com.hbm.door.sourceblast;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityDummy's actual absolute tx/ty/tz parent, not a modern directional dummy chain. */
public final class SourceBlastDoorDummy extends BlockEntity {
    public int targetX,targetY,targetZ;
    public SourceBlastDoorDummy(BlockPos pos,BlockState state){super(SourceBlastDoorContent.DUMMY_ENTITY.get(),pos,state);}
    public BlockPos parent(){return new BlockPos(targetX,targetY,targetZ);}
    public void parent(BlockPos target){targetX=target.getX();targetY=target.getY();targetZ=target.getZ();setChanged();if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
    public SourceBlastDoorEntity door(){return level!=null&&SourceBlastDoorContent.full(level,parent())&&level.getBlockEntity(parent())instanceof SourceBlastDoorEntity door?door:null;}
    public static void tick(Level level,BlockPos pos,BlockState state,SourceBlastDoorDummy dummy){
        if(level.isClientSide||!SourceBlastDoorContent.neighborsReady(level,pos)||!SourceBlastDoorContent.full(level,dummy.parent()))return;
        var parent=level.getBlockState(dummy.parent()).getBlock();
        // Original IMultiblock parent test; the actual door and admitted directional multiblocks are valid parents.
        if(!(parent instanceof SourceBlastDoorBlock)&&!(parent instanceof com.hbm.multiblock.DummyableBlock))level.destroyBlock(pos,false);
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("tx",targetX);tag.putInt("ty",targetY);tag.putInt("tz",targetZ);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);targetX=tag.getInt("tx");targetY=tag.getInt("ty");targetZ=tag.getInt("tz");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
