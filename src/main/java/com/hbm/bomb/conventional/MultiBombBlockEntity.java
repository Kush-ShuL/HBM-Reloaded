package com.hbm.bomb.conventional;

import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

/** TileEntityBombMulti: six unrestricted manual slots, one item each, no sided automation slots. */
public final class MultiBombBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider {
    private final NonNullList<ItemStack> slots=NonNullList.withSize(6,ItemStack.EMPTY);
    private String customName;
    public MultiBombBlockEntity(BlockPos pos,BlockState state){super(MultiBombContent.ENTITY.get(),pos,state);}
    public boolean isLoaded(){return slots.get(0).is(Items.TNT)&&slots.get(1).is(Items.TNT)&&slots.get(3).is(Items.TNT)&&slots.get(4).is(Items.TNT);}
    public static int type(ItemStack stack){if(stack.isEmpty())return 0;if(stack.is(Items.GUNPOWDER))return 1;if(stack.is(Items.TNT))return 2;String[] symbols={"pellet_cluster","powder_fire","powder_poison","pellet_gas"};for(int i=0;i<symbols.length;i++)if(stack.is(HbmContent.item(symbols[i])))return i+3;return 0;}
    public int type(int slot){return type(slots.get(slot));}
    @Override public int getContainerSize(){return 6;}
    @Override public int getMaxStackSize(){return 1;}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var stack=slots.get(slot);if(stack.isEmpty())return ItemStack.EMPTY;ItemStack result;if(stack.getCount()<=count){result=stack;slots.set(slot,ItemStack.EMPTY);}else{result=stack.split(count);if(stack.isEmpty())slots.set(slot,ItemStack.EMPTY);}setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var stack=slots.get(slot);slots.set(slot,ItemStack.EMPTY);return stack;}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>1)stack.setCount(1);setChanged();}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return side!=Direction.DOWN||slot!=1||stack.is(Items.BUCKET);}
    public void setCustomName(String name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName!=null&&!customName.isEmpty()?Component.literal(customName):Component.translatable("container.bombMulti");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new MultiBombMenu(id,inventory,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<slots.size();i++)if(!slots.get(i).isEmpty()){var entry=(CompoundTag)slots.get(i).save(lookup);entry.putByte("slot",(byte)i);list.add(entry);}tag.put("items",list);if(customName!=null)tag.putString("name",customName);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);slots.clear();var list=tag.getList("items",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var entry=list.getCompound(i);int slot=entry.getByte("slot");if(slot>=0&&slot<6)slots.set(slot,ItemStack.parseOptional(lookup,entry));}customName=tag.getString("name");}
}
