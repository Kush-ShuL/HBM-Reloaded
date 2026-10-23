package com.hbm.machine.conveyorpartitioner;

import com.hbm.conveyor.MovingItem;
import com.hbm.machine.crystallizer.CrystallizerRecipeConfig;
import com.hbm.multiblock.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual nested TileEntityCranePartitioner: two 45-slot halves, not a CraneBase or invented menu. */
public final class ConveyorPartitionerEntity extends BlockEntity implements WorldlyContainer,NamedMultiblock,MuffledMachine {
    public static final int SLOT_COUNT=45;
    public final ItemStack[] slots=new ItemStack[SLOT_COUNT*2];
    private static final int[] ACCESS=java.util.stream.IntStream.range(0,SLOT_COUNT*2).toArray();
    private Component customName;private boolean muffled;
    public ConveyorPartitionerEntity(BlockPos pos,BlockState state){super(ConveyorPartitionerContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    public static void tick(Level level,BlockPos pos,BlockState state,ConveyorPartitionerEntity partitioner){
        if(level.isClientSide)return;
        var stacks=new ArrayList<ItemStack>();for(int i=0;i<SLOT_COUNT;i++)if(!partitioner.slots[i].isEmpty())stacks.add(partitioner.slots[i]);
        stacks.sort((first,second)->Integer.signum(first.getCount()-second.getCount()));
        for(var stack:stacks){
            int amount=CrystallizerRecipeConfig.getAmount(level.getRecipeManager(),stack);if(amount==0)amount=stack.getCount();
            while(stack.getCount()>=amount){var output=stack.copyWithCount(amount);stack.shrink(amount);var item=new MovingItem(level);item.setItemStack(output);item.setPos(pos.getX()+.5,pos.getY()+.25,pos.getZ()+.5);level.addFreshEntity(item);}
        }
        for(int i=0;i<SLOT_COUNT;i++)if(partitioner.slots[i].getCount()<=0)partitioner.slots[i]=ItemStack.EMPTY;
        // Source's markDirty boolean was never set. This source tick deliberately does not dirty the chunk.
    }
    public boolean accepts(ItemStack stack){return level!=null&&CrystallizerRecipeConfig.getAmount(level.getRecipeManager(),stack)>0;}
    /** Source InventoryUtil overload ignores automation validity, merges first, then stores the remainder object in one empty slot. */
    public ItemStack accept(ItemStack stack){
        int start=accepts(stack)?0:SLOT_COUNT,end=start+SLOT_COUNT;
        for(int i=start;i<end;i++){var existing=slots[i];if(!existing.isEmpty()&&ItemStack.isSameItemSameComponents(existing,stack)){int count=Math.min(stack.getCount(),existing.getMaxStackSize()-existing.getCount());if(count>0){existing.grow(count);stack.shrink(count);if(stack.isEmpty())return ItemStack.EMPTY;}}}
        for(int i=start;i<end;i++)if(slots[i].isEmpty()){setItem(i,stack);return ItemStack.EMPTY;}
        return stack;
    }
    public Component getDisplayName(){return customName==null?Component.translatable("container.partitioner"):customName;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    public double getUseRange(){return 12D;}
    @Override public boolean stillValid(Player player){double range=getUseRange();return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=range*range;}
    @Override public int getContainerSize(){return slots.length;}@Override public int getMaxStackSize(){return 64;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int index){return slots[index];}
    @Override public ItemStack removeItem(int index,int count){var result=slots[index].split(count);if(slots[index].isEmpty())slots[index]=ItemStack.EMPTY;return result;}
    @Override public ItemStack removeItemNoUpdate(int index){var result=slots[index];slots[index]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int index,ItemStack stack){slots[index]=stack;if(stack.getCount()>64)stack.setCount(64);}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return index<SLOT_COUNT&&accepts(stack);}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS.clone();}
    @Override public boolean canPlaceItemThroughFace(int index,ItemStack stack,Direction side){return canPlaceItem(index,stack);}
    @Override public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return index>=SLOT_COUNT;}
    @Override protected void saveAdditional(CompoundTag data,HolderLookup.Provider lookup){super.saveAdditional(data,lookup);var items=NonNullList.withSize(slots.length,ItemStack.EMPTY);for(int i=0;i<slots.length;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(data,items,lookup);data.putBoolean("muffled",muffled);if(customName!=null){data.putString("name",customName.getString());data.putString("CustomName",Component.Serializer.toJson(customName,lookup));}}
    @Override protected void loadAdditional(CompoundTag data,HolderLookup.Provider lookup){super.loadAdditional(data,lookup);var items=NonNullList.withSize(slots.length,ItemStack.EMPTY);ContainerHelper.loadAllItems(data,items,lookup);for(int i=0;i<slots.length;i++)slots[i]=items.get(i);muffled=data.getBoolean("muffled");customName=data.contains("CustomName")?Component.Serializer.fromJson(data.getString("CustomName"),lookup):data.getString("name").isEmpty()?null:Component.literal(data.getString("name"));}
}
