package com.hbm.block.bobble;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source non-ticking tile, including its byte type NBT and full description packet. */
public final class BobbleBlockEntity extends BlockEntity {
    public BobbleType type=BobbleType.NONE;
    public BobbleBlockEntity(BlockPos pos,BlockState state){super(BobbleContent.ENTITY.get(),pos,state);}
    public void setType(int metadata){type=BobbleType.fromMetadata(metadata);setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putByte("type",(byte)type.ordinal());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);type=BobbleType.fromMetadata(tag.getByte("type"));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
