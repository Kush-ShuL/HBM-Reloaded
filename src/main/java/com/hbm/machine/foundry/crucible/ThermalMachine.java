package com.hbm.machine.foundry.crucible;
import com.hbm.api.machine.MachineView;
import com.hbm.multiblock.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Inventory/save/sync adapter for the two actual source heat machines. */
public abstract class ThermalMachine extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,MachineView, com.hbm.multiblock.MuffledMachine {
 public final NonNullList<ItemStack> slots;private Component customName;private boolean muffledBase;
 protected ThermalMachine(BlockEntityType<?> type,BlockPos pos,BlockState state,int count){super(type,pos,state);slots=NonNullList.withSize(count,ItemStack.EMPTY);}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 protected abstract String title();protected abstract void saveMachine(CompoundTag tag);protected abstract void loadMachine(CompoundTag tag);
 @Override public void setCustomName(Component value){customName=value;setChanged();}@Override public Component getDisplayName(){return customName==null?Component.translatable(title()):customName;}
 protected void sync(int range){setChanged();if(level instanceof ServerLevel server){var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<range*range)player.connection.send(packet);}}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);tag.putBoolean("muffled",isMuffled());saveMachine(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,slots,lookup);setMuffled(tag.getBoolean("muffled"));loadMachine(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",isMuffled());saveMachine(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){setMuffled(tag.getBoolean("muffled"));loadMachine(tag);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public int getContainerSize(){return slots.size();}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots.get(slot);}@Override public ItemStack removeItem(int slot,int count){var out=ContainerHelper.removeItem(slots,slot,count);if(!out.isEmpty())setChanged();return out;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}@Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);stack.limitSize(getMaxStackSize());setChanged();}@Override public void clearContent(){slots.replaceAll(stack->ItemStack.EMPTY);setChanged();}/** Source TileEntityMachineBase.getUseRange; subclasses may retain their real source override. */
 public double getUseRange(){return 12D;}
 @Override public boolean stillValid(Player player){if(level==null||level.getBlockEntity(worldPosition)!=this)return false;double range=getUseRange();return player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=range*range;}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}

 @Override public boolean isMuffled(){return muffledBase;}
 @Override public void setMuffled(boolean value){muffledBase=value;}
}
