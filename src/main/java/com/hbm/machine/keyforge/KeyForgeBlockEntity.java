package com.hbm.machine.keyforge;

import com.hbm.item.lock.KeyPinItem;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source three-slot copying/randomizing table, with no power, progress bar or computer peripheral. */
public final class KeyForgeBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider {
    private final NonNullList<ItemStack> items=NonNullList.withSize(3,ItemStack.EMPTY);
    private Component customName;
    public KeyForgeBlockEntity(BlockPos pos,BlockState state){super(KeyForgeContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,KeyForgeBlockEntity machine){
        if(level.isClientSide)return;var first=machine.items.get(0);var second=machine.items.get(1);var random=machine.items.get(2);boolean changed=false;
        if(first.getItem() instanceof KeyPinItem a&&second.getItem() instanceof KeyPinItem b&&a.canTransfer()&&b.canTransfer()){int pins=KeyPinItem.getPins(first);if(!second.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)||!second.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().contains("pins")||KeyPinItem.getPins(second)!=pins){KeyPinItem.setPins(second,pins);changed=true;}}
        if(random.getItem() instanceof KeyPinItem pin&&pin.canTransfer()){KeyPinItem.setPins(random,level.random.nextInt(900)+100);changed=true;}
        if(changed)machine.setChanged();
    }
    public void setCustomName(Component value){customName=value;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.keyForge"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new KeyForgeMenu(id,inventory,this,worldPosition);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,items,lookup);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,items,lookup);customName=null;if(tag.contains("name")){var name=tag.getString("name");try{customName=Component.Serializer.fromJson(name,lookup);}catch(com.google.gson.JsonParseException error){customName=Component.literal(name);}if(customName==null&&!name.isEmpty())customName=Component.literal(name);}}
    @Override public int getContainerSize(){return 3;}
    @Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items.get(slot);}
    @Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(items,slot,amount);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}
    @Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);stack.limitSize(64);setChanged();}
    @Override public void clearContent(){items.clear();setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{side==Direction.UP?0:side==Direction.DOWN?1:2};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
}
