package com.hbm.machine.reactor.pile.core;
import io.netty.buffer.ByteBuf;import net.minecraft.core.BlockPos;import net.minecraft.world.level.block.entity.BlockEntityType;import net.minecraft.world.level.block.state.BlockState;
public abstract class TileEntityPileDeviceBase extends PileTickingBlockEntity {
 public int chanNum;
 protected TileEntityPileDeviceBase(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 public PileDirection getOrientation(){return PileDirection.getOrientation(getBlockMetadata()%4+2);}
 @Override public void serialize(ByteBuf buf){super.serialize(buf);buf.writeInt(chanNum);}
 @Override public void deserialize(ByteBuf buf){super.deserialize(buf);chanNum=buf.readInt();}
}
