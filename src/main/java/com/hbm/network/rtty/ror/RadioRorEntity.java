package com.hbm.network.rtty.ror;
import com.hbm.api.computer.ComputerControl;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** LoadedBase persistence/transport only; reader/controller retain separate real state machines. */
public abstract class RadioRorEntity extends BlockEntity implements ComputerControl,MuffledMachine {
 public boolean polling,muffled,tilted;
 protected RadioRorEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 protected abstract boolean signalTick(ServerLevel level);protected abstract void saveState(CompoundTag tag);protected abstract void loadState(CompoundTag tag);public abstract void control(CompoundTag tag);
 protected final void serverTick(ServerLevel server){if(!signalTick(server))return;var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);}
 public final BlockEntity support(){if(level==null)return null;var dir=RadioRorBlock.outward(getBlockState());var pos=dir==null?worldPosition:worldPosition.relative(dir.getOpposite());return level.hasChunkAt(pos)?level.getBlockEntity(pos):null;}
 private void save(CompoundTag tag){saveState(tag);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}private void load(CompoundTag tag){loadState(tag);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);save(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);load(tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();save(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){load(tag);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
