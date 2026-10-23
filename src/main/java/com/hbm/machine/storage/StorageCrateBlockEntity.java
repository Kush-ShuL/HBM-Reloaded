package com.hbm.machine.storage;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import com.hbm.registry.HbmSounds;

/** Source TileEntityCrateBase and its exact concrete sizes, lock state, names and one-shot spider payload. */
public class StorageCrateBlockEntity extends LockableBlockEntity implements WorldlyContainer,MenuProvider,Nameable {
    public final StorageCrateKind kind;protected final NonNullList<ItemStack> slots;private Component customName;
    public boolean hasSpiders;boolean suppressDropOnRemoval;
    public StorageCrateBlockEntity(StorageCrateKind kind,BlockPos pos,BlockState state){super(StorageCrateContent.ENTITY.get(kind).get(),pos,state);this.kind=kind;slots=NonNullList.withSize(kind.slots,ItemStack.EMPTY);}
    @Override public int getContainerSize(){return slots.size();}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var stack=slots.get(slot);if(stack.isEmpty())return ItemStack.EMPTY;if(stack.getCount()<=count){slots.set(slot,ItemStack.EMPTY);return stack;}var result=stack.split(count);if(stack.isEmpty())slots.set(slot,ItemStack.EMPTY);setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);stack.limitSize(64);setChanged();}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public int getMaxStackSize(){return 64;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.range(0,slots.size()).toArray();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack)&&!isLocked();}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return !isLocked();}
    @Override public void startOpen(Player player){sound("block.crateOpen");}
    @Override public void stopOpen(Player player){sound("block.crateClose");}
    private void sound(String event){if(level!=null)level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get(event),SoundSource.BLOCKS,1,1);}
    public void setCustomName(Component name){customName=name;setChanged();}
    @Override public boolean hasCustomName(){return customName!=null&&!customName.getString().isEmpty();}
    @Override public Component getCustomName(){return customName;}
    @Override public Component getName(){return hasCustomName()?customName:Component.translatable(kind.titleKey);}
    @Override public Component getDisplayName(){return getName();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){if(player.isSpectator()&&isLocked()&&!hasMatchingKey(player))return null;return new StorageCrateMenu(id,inventory,this,kind,worldPosition);}
    /** NeoForge also invokes this for vanilla spectator opens, which bypass block interaction callbacks. */
    @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBoolean(false);buffer.writeEnum(kind);buffer.writeBlockPos(worldPosition);}
    public void fillWithSpiders(){hasSpiders=true;}
    public void spawnSpiders(Player player){if(hasSpiders){CrateContents.spiders(player,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ());hasSpiders=false;setChanged();}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<slots.size();i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);tag.putBoolean("spiders",hasSpiders);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));
    }
    /** Public block tracking carries visual/lock state, never a protected container's item stacks. Menus sync separately. */
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        var tag=super.getUpdateTag(lookup);
        if(kind==StorageCrateKind.SAFE||isLocked())tag.remove("items");
        return tag;
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);if(tag.contains("items",Tag.TAG_LIST)){slots.clear();for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<slots.size())slots.set(slot,ItemStack.parseOptional(lookup,item));}}hasSpiders=tag.getBoolean("spiders");customName=null;
        if(tag.contains("name")){var name=tag.getString("name");try{customName=Component.Serializer.fromJson(name,lookup);}catch(com.google.gson.JsonParseException error){customName=Component.literal(name);}if(customName==null&&!name.isEmpty())customName=Component.literal(name);}
    }
}
