package com.hbm.weapon.sedna.gun.energy.bag;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
/** Original ItemStackUtil items list with slot bytes; the same inventory is used by GUI and live gun magazines. */
public final class AmmoBagInventory implements Container {
    public final ItemStack box;private final HolderLookup.Provider lookup;
    private final NonNullList<ItemStack> slots=NonNullList.withSize(8,ItemStack.EMPTY);
    public AmmoBagInventory(ItemStack bag,HolderLookup.Provider lookup){this.box=bag;this.lookup=lookup;if(!bag.has(DataComponents.CUSTOM_DATA))bag.set(DataComponents.CUSTOM_DATA,CustomData.EMPTY);var list=data().getList("items",Tag.TAG_COMPOUND);for(int i=0;i<8;i++){var tag=list.getCompound(i);int slot=tag.getByte("slot");var loaded=ItemStack.parseOptional(lookup,tag);if(slot>=0&&slot<8&&!loaded.isEmpty())slots.set(slot,loaded);}}
    private CompoundTag data(){return box.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    @Override public int getContainerSize(){return 8;}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var item=slots.get(slot);if(item.isEmpty())return ItemStack.EMPTY;if(item.getCount()>count){var result=item.split(count);setChanged();return result;}setItem(slot,ItemStack.EMPTY);return item;}
    @Override public ItemStack removeItemNoUpdate(int slot){var item=slots.get(slot);setItem(slot,ItemStack.EMPTY);return item;}
    @Override public void setItem(int slot,ItemStack stack){stack.limitSize(64);slots.set(slot,stack);setChanged();}
    @Override public void setChanged(){var tag=data();var items=new ListTag();for(int i=0;i<8;i++){var item=slots.get(i);if(item.isEmpty()){slots.set(i,ItemStack.EMPTY);continue;}var child=(CompoundTag)item.save(lookup);child.putByte("slot",(byte)i);items.add(child);}tag.put("items",items);box.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public int getMaxStackSize(){return 64;}@Override public boolean stillValid(Player player){return true;}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return stack.getItem() instanceof com.hbm.weapon.sedna.ammo.FlatAmmoItem && stack.getComponentsPatch().isEmpty();}
}
