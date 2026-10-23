package com.hbm.machine.reactor.fuelpool;

import java.util.Arrays;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original twelve independent one-item slots and adjacent-water stochastic cooling. */
public final class FuelPoolBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider {
    private static final int[] ALL={0,1,2,3,4,5,6,7,8,9,10,11};
    private final ItemStack[] slots=new ItemStack[12];private Component customName;
    public FuelPoolBlockEntity(BlockPos pos,BlockState state){super(FuelPoolContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    public int waterSides(){if(level==null)return 0;int water=0;for(var side:Direction.values())if(level.getBlockState(worldPosition.relative(side)).is(Blocks.WATER))water++;return water;}
    public void tick(){if(level==null||level.isClientSide)return;int water=waterSides();if(water==0)return;int range=60*60*20/water;boolean changed=false;for(int i=0;i<slots.length;i++){var stack=slots[i];if(stack.isEmpty())continue;if(stack.getItem() instanceof FuelPoolRod rod){rod.coolInFuelPool(level,stack);changed=true;}else if(level.random.nextInt(range)==0){var recipe=level.getRecipeManager().getRecipeFor(FuelPoolContent.TYPE.get(),new SingleRecipeInput(stack),level);if(recipe.isPresent()){slots[i]=recipe.get().value().assemble(new SingleRecipeInput(stack),level.registryAccess());changed=true;}}}if(changed)setChanged();}
    @Override public int getContainerSize(){return slots.length;}@Override public int getMaxStackSize(){return 1;}
    @Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){var removed=slots[slot].split(count);if(!removed.isEmpty())setChanged();return removed;}
    @Override public ItemStack removeItemNoUpdate(int slot){var removed=slots[slot];slots[slot]=ItemStack.EMPTY;return removed;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public int[] getSlotsForFace(Direction face){return ALL.clone();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return !stack.isEmpty()&&(stack.getItem() instanceof FuelPoolRod||level!=null&&level.getRecipeManager().getRecipeFor(FuelPoolContent.TYPE.get(),new SingleRecipeInput(stack),level).isPresent());}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){if(stack.getItem() instanceof FuelPoolRod rod)return rod.fuelPoolCoreHeat(stack)<50&&rod.fuelPoolHullHeat(stack)<50;return !canPlaceItem(slot,getItem(slot));}
    public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.wasteDrum"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FuelPoolMenu(id,inventory,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<slots.length;i++)if(!slots[i].isEmpty()){var row=(CompoundTag)slots[i].save(lookup);row.putByte("slot",(byte)i);list.add(row);}tag.put("items",list);if(customName!=null)tag.putString("name",customName.getString());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);Arrays.fill(slots,ItemStack.EMPTY);var list=tag.getList("items",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var row=list.getCompound(i);int slot=row.getByte("slot");if(slot>=0&&slot<slots.length)slots[slot]=ItemStack.parseOptional(lookup,row);}customName=tag.contains("name")?Component.literal(tag.getString("name")):null;}
}
