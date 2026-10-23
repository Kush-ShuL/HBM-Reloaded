package com.hbm.item.containment;

import com.hbm.machine.massstorage.MassStorageItem;
import com.hbm.machine.storage.CrateContents;
import com.hbm.machine.storage.CrateStackData;
import com.hbm.machine.storage.StorageCrateItem;
import com.hbm.registry.HbmSounds;
import java.io.IOException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** ItemLeadBox.InventoryLeadBox and ItemInventory, reusing the sole crate component/overflow/ejection helpers. */
public final class ContainmentBoxInventory implements Container {
    public final Player player;
    public final ItemStack target;
    private final NonNullList<ItemStack> slots=NonNullList.withSize(20,ItemStack.EMPTY);
    public ContainmentBoxInventory(Player player,ItemStack target){
        this.player=player;this.target=target;
        if(!target.has(DataComponents.CUSTOM_DATA))target.set(DataComponents.CUSTOM_DATA,CustomData.EMPTY);
        var list=CrateStackData.data(target).getList("items",Tag.TAG_COMPOUND);
        // ItemStackUtil.readStacksFromNBT reads the original fixed twenty entries, preserving sparse slot bytes.
        for(int i=0;i<20;i++){
            var tag=list.getCompound(i);int slot=tag.getByte("slot");
            var item=ItemStack.parseOptional(player.registryAccess(),tag);
            if(slot>=0&&slot<20&&!item.isEmpty())slots.set(slot,item);
        }
    }
    @Override public int getContainerSize(){return 20;}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){
        var item=slots.get(slot);if(item.isEmpty())return ItemStack.EMPTY;
        if(item.getCount()>count){var part=item.split(count);setChanged();return part;}
        setItem(slot,ItemStack.EMPTY);return item;
    }
    @Override public ItemStack removeItemNoUpdate(int slot){var item=slots.get(slot);setItem(slot,ItemStack.EMPTY);return item;}
    @Override public void setItem(int slot,ItemStack stack){stack.limitSize(getMaxStackSize());slots.set(slot,stack);setChanged();}
    @Override public int getMaxStackSize(){return 1;}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean stillValid(Player viewer){return true;} // Original ItemInventory always permits use; menu locks its open item.
    @Override public boolean canPlaceItem(int slot,ItemStack stack){
        // The six original ItemBlockStorageCrate registrations: five crate/safe kinds and mass_storage.
        return !(stack.getItem() instanceof StorageCrateItem)&&!(stack.getItem() instanceof MassStorageItem);
    }
    @Override public void setChanged(){
        var tag=CrateStackData.data(target);var list=new ListTag();
        for(int i=0;i<20;i++){
            var item=slots.get(i);if(item.isEmpty()){slots.set(i,ItemStack.EMPTY);continue;}
            var child=(CompoundTag)item.save(player.registryAccess());child.putByte("slot",(byte)i);list.add(child);
        }
        tag.put("items",list);CrateStackData.data(target,tag);
        if(player.level().isClientSide)return;
        try{
            if(CrateStackData.compressedSize(target,player.registryAccess())>6000){
                player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED),false);
                CrateContents.ejectHeld(player,this);
                // Source checkNBT returns null, clearing all source NBT, including display-name/enchantment tags.
                CrateStackData.clearSourceData(target);slots.clear();
            }
        }catch(IOException ignored){}
    }
    public Component displayName(){return target.has(DataComponents.CUSTOM_NAME)?target.getHoverName():Component.translatable("container.leadBox");}
    @Override public void startOpen(Player ignored){sound("block.crateOpen");}
    @Override public void stopOpen(Player ignored){sound("block.crateClose");}
    private void sound(String event){player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(event),SoundSource.PLAYERS,1,.8F);}
}
