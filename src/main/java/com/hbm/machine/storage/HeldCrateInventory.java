package com.hbm.machine.storage;

import com.hbm.registry.HbmSounds;
import java.io.IOException;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source InventoryCrate: target identity, unique stacklock, preserved lock/spiders, and close-time6kB ejection. */
public final class HeldCrateInventory implements Container {
    private static final Random RANDOM=new Random();
    public final Player player;public final ItemStack target;public final StorageCrateKind kind;private final NonNullList<ItemStack> slots;
    public HeldCrateInventory(Player player,ItemStack target,StorageCrateKind kind){
        this.player=player;this.target=target;this.kind=kind;slots=NonNullList.withSize(kind.slots,ItemStack.EMPTY);var tag=CrateStackData.data(target);
        if(!CrateStackData.hasSourceData(target)){tag.putLong("stacklock",RANDOM.nextLong());CrateStackData.data(target,tag);}
        for(int i=0;i<slots.size();i++)slots.set(i,CrateStackData.slot(tag,i,player.registryAccess()));
    }
    @Override public int getContainerSize(){return slots.size();}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var stack=slots.get(slot);if(stack.isEmpty())return ItemStack.EMPTY;if(stack.getCount()>count){var result=stack.split(count);setChanged();return result;}setItem(slot,ItemStack.EMPTY);return stack;}
    @Override public ItemStack removeItemNoUpdate(int slot){var stack=slots.get(slot);setItem(slot,ItemStack.EMPTY);return stack;}
    @Override public void setItem(int slot,ItemStack stack){stack.limitSize(64);slots.set(slot,stack);setChanged();}
    @Override public int getMaxStackSize(){return 64;}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean stillValid(Player viewer){return viewer.getMainHandItem()==target;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public void setChanged(){var tag=CrateStackData.data(target);tag.remove("stacklock");CrateStackData.saveSlots(tag,this,player.registryAccess());tag.putLong("stacklock",RANDOM.nextLong());CrateStackData.data(target,tag);}
    public Component displayName(){return target.has(DataComponents.CUSTOM_NAME)?target.get(DataComponents.CUSTOM_NAME):Component.translatable(kind.titleKey);}
    @Override public void startOpen(Player ignored){sound("block.crateOpen");}
    @Override public void stopOpen(Player ignored){
        sound("block.crateClose");if(player.level().isClientSide)return;
        var tag=CrateStackData.data(target);try{if(CrateStackData.compressedSize(target,player.registryAccess())>6000){player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED),false);CrateContents.ejectHeld(player,this);CrateStackData.clearSourceData(target);tag=new CompoundTag();}}catch(IOException ignoredError){}
        CrateStackData.data(target,tag);player.inventoryMenu.broadcastChanges();tag=CrateStackData.data(target);tag.remove("stacklock");if(!tag.isEmpty()||CrateStackData.hasNonPayloadData(target)){tag.putLong("stacklock",RANDOM.nextLong());CrateStackData.data(target,tag);}else target.remove(DataComponents.CUSTOM_DATA);
    }
    private void sound(String event){player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(event),SoundSource.PLAYERS,1,.8F);}
}
