package com.hbm.machine.pneumaticstorage;

import api.hbm.ntl.IPneumaticConnector;
import com.hbm.api.machine.MachineView;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Native inventory/name/save bridge for the two original machine bases; no extra material cache. */
public abstract class PneumaticInventory extends BlockEntity implements WorldlyContainer,MenuProvider,IPneumaticConnector,MachineView {
    public final PneumaticKind kind;
    public final ItemStack[] slots;
    public boolean suppressDropOnRemoval;
    private Component customName;
    protected PneumaticInventory(PneumaticKind kind,BlockPos pos,BlockState state){super(PneumaticStorageContent.entity(kind),pos,state);this.kind=kind;slots=new ItemStack[kind.slots];Arrays.fill(slots,ItemStack.EMPTY);}
    public static void tick(Level level,BlockPos pos,BlockState state,PneumaticInventory machine){if(level instanceof ServerLevel server)machine.updateServer(server);}
    protected abstract void updateServer(ServerLevel server);
    protected void synchronize(ServerLevel server){var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225)player.connection.send(packet);}
    @Override public boolean canConnectPneumatic(Direction side){return true;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable(kind.title()):customName;}
    public void setCustomName(Component name){customName=name;setChanged();}
    public Component customName(){return customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return kind==PneumaticKind.ACCESS?new PneumaticAccessMenu(id,inventory,(PneumaticAccess)this):new PneumaticStorageMenu(id,inventory,this);}
    @Override public int getContainerSize(){return slots.length;}
    @Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int index){return slots[index];}
    @Override public ItemStack removeItem(int index,int count){var removed=slots[index].split(count);if(!removed.isEmpty())setChanged();return removed;}
    @Override public ItemStack removeItemNoUpdate(int index){var stack=slots[index];slots[index]=ItemStack.EMPTY;return stack;}
    @Override public void setItem(int index,ItemStack stack){slots[index]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int index,ItemStack stack,Direction side){return canPlaceItem(index,stack);}
    @Override public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return false;}
    protected void saveFields(CompoundTag tag){}
    protected void loadFields(CompoundTag tag){}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var inventory=NonNullList.withSize(slots.length,ItemStack.EMPTY);for(int i=0;i<slots.length;i++)inventory.set(i,slots[i]);ContainerHelper.saveAllItems(tag,inventory,lookup);saveFields(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var inventory=NonNullList.withSize(slots.length,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,inventory,lookup);for(int i=0;i<slots.length;i++)slots[i]=inventory.get(i);loadFields(tag);customName=tag.contains("CustomName")?Component.Serializer.fromJson(tag.getString("CustomName"),lookup):null;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveFields(tag);return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadFields(tag);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
}
