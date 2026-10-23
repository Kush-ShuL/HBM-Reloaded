package com.hbm.machine.reactor.iter.utilities;
import com.hbm.api.machine.MachineView;
import com.hbm.multiblock.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** The source LoadedBase flags and packet lifecycle for these two GUI-less fusion machines; no inventory/menu capability. */
public abstract class FusionUtilityEntity extends BlockEntity implements MachineView,MuffledMachine {
 public boolean muffled,tilted;
 protected FusionUtilityEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
 protected abstract void saveMachine(CompoundTag tag);protected abstract void loadMachine(CompoundTag tag);
 protected void writeSync(CompoundTag tag){saveMachine(tag);}protected void readSync(CompoundTag tag){loadMachine(tag);}
 protected final void sync(int radius){setChanged();if(level instanceof ServerLevel server){var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<radius*radius)player.connection.send(packet);}}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);saveMachine(tag);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");loadMachine(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);writeSync(tag);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");readSync(tag);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
