package com.hbm.machine.reactor;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.machine.MachineView;
import com.hbm.multiblock.*;
import java.util.Arrays;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Shared original inventory persistence and model-state synchronization, without standard energy/fluid APIs. */
public abstract class ReactorInventory extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,ComputerControl,MachineView {
    protected final ItemStack[] slots;protected Component customName;
    protected ReactorInventory(BlockEntityType<?> type,BlockPos pos,BlockState state,int size){super(type,pos,state);slots=new ItemStack[size];Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    protected abstract String nameKey();
    protected abstract void writeState(CompoundTag tag);
    protected abstract void readState(CompoundTag tag);
    @Override public Component getDisplayName(){return customName==null?Component.translatable(nameKey()):customName;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public int getContainerSize(){return slots.length;}
    @Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=NonNullList.withSize(slots.length,ItemStack.EMPTY);for(int i=0;i<slots.length;i++)list.set(i,slots[i]);ContainerHelper.saveAllItems(tag,list,lookup);writeState(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var list=NonNullList.withSize(slots.length,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,list,lookup);for(int i=0;i<slots.length;i++)slots[i]=list.get(i);readState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
