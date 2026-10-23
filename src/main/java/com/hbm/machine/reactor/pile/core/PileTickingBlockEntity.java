package com.hbm.machine.reactor.pile.core;
import io.netty.buffer.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** A thin native BE bridge; no duplicate world, inventory, or thermal storage. */
public abstract class PileTickingBlockEntity extends BlockEntity implements com.hbm.multiblock.MuffledMachine {
 protected HolderLookup.Provider stackRegistries;
 public final int xCoord,yCoord,zCoord;protected PileWorld worldObj;public boolean muffled,tilted;
 protected PileTickingBlockEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);xCoord=pos.getX();yCoord=pos.getY();zCoord=pos.getZ();}
 @Override public void setLevel(Level level){super.setLevel(level);worldObj=new PileWorld(level);}
 public int getBlockMetadata(){return getBlockState().getValue(PileCoreContent.META);}
 public void markChanged(){setChanged();}public void markDirty(){setChanged();}public boolean isInvalid(){return isRemoved();}
 public float getVolume(float volume){return muffled?volume*.1F:volume;}
 public abstract void updateEntity();
 public static void tick(Level level,BlockPos pos,BlockState state,PileTickingBlockEntity entity){entity.updateEntity();}
 public void readFromNBT(CompoundTag tag){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 public void writeToNBT(CompoundTag tag){tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);stackRegistries=registries;readFromNBT(tag);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);stackRegistries=registries;writeToNBT(tag);}
 public void serialize(ByteBuf buffer){buffer.writeBoolean(muffled);buffer.writeBoolean(tilted);}
 public void deserialize(ByteBuf buffer){muffled=buffer.readBoolean();tilted=buffer.readBoolean();}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){var tag=new CompoundTag();var buffer=Unpooled.buffer();try{serialize(buffer);byte[] data=new byte[buffer.readableBytes()];buffer.readBytes(data);tag.putByteArray("hbm:pile_sync",data);}finally{buffer.release();}return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider registries){if(!tag.contains("hbm:pile_sync"))return;var buffer=Unpooled.wrappedBuffer(tag.getByteArray("hbm:pile_sync"));try{deserialize(buffer);}finally{buffer.release();}}
 @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider registries){handleUpdateTag(packet.getTag(),registries);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
 protected void networkPackNT(int range){if(getLevel() instanceof ServerLevel server){var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(xCoord+.5,yCoord+.5,zCoord+.5)<range*range)player.connection.send(packet);}}
 protected CompoundTag saveStack(ItemStack stack){return (CompoundTag)stack.save(stackRegistries!=null?stackRegistries:getLevel().registryAccess());}
 protected ItemStack readStack(CompoundTag tag){var stack=ItemStack.parseOptional(stackRegistries!=null?stackRegistries:getLevel().registryAccess(),tag);return stack.isEmpty()?null:stack;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
