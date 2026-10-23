package com.hbm.machine.conveyorsplitter;

import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** True TileEntityCraneSplitter byte ratio/cursor, carried across stacks and persisted across reloads. */
public final class ConveyorSplitterEntity extends BlockEntity implements MuffledMachine {
    private boolean position,muffled;private byte remaining;public byte leftRatio=1,rightRatio=1;
    public ConveyorSplitterEntity(BlockPos pos,BlockState state){super(ConveyorSplitterContent.ENTITY.get(),pos,state);}
    public ItemStack[] splitStack(ItemStack stack){int left=0,right=0,count=stack.getCount();if(remaining<=0)remaining=position?rightRatio:leftRatio;while(count>0){int extract=Math.min(remaining,count);remaining-=extract;count-=extract;if(position)right+=extract;else left+=extract;if(remaining<=0){position=!position;remaining=position?rightRatio:leftRatio;}}var a=stack.copy();var b=stack.copy();a.setCount(left);b.setCount(right);setChanged();return new ItemStack[]{a,b};}
    public static void tick(Level world,BlockPos pos,BlockState state,ConveyorSplitterEntity splitter){if(world instanceof ServerLevel server){var tag=new CompoundTag();tag.putBoolean("splitterRatioSync",true);tag.putByte("left",splitter.leftRatio);tag.putByte("right",splitter.rightRatio);var packet=ClientboundBlockEntityDataPacket.create(splitter,(entity,lookup)->tag);for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<225)player.connection.send(packet);}}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("pos",position);tag.putByte("count",remaining);tag.putByte("left",leftRatio);tag.putByte("right",rightRatio);tag.putBoolean("muffled",muffled);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);position=tag.getBoolean("pos");remaining=tag.getByte("count");leftRatio=(byte)Math.max(tag.getByte("left"),1);rightRatio=(byte)Math.max(tag.getByte("right"),1);muffled=tag.getBoolean("muffled");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){if(tag.getBoolean("splitterRatioSync")){leftRatio=tag.getByte("left");rightRatio=tag.getByte("right");}else loadWithComponents(tag,lookup);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
