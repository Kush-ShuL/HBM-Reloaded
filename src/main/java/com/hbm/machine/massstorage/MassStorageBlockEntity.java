package com.hbm.machine.massstorage;

import com.hbm.api.computer.*;
import com.hbm.api.machine.ICopiable;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Original three real slots, phantom type, stockpile algorithm, lock base, copy settings and computer API. */
public final class MassStorageBlockEntity extends LockableBlockEntity implements WorldlyContainer,MenuProvider,ComputerControl,ICopiable {
    private final NonNullList<ItemStack> slots=NonNullList.withSize(3,ItemStack.EMPTY);
    private int stockpile,capacity;
    public boolean output;
    public int redstone;
    public ItemStack displayType=ItemStack.EMPTY;
    public boolean hasSpiders;
    public String customName;
    boolean suppressDropOnRemoval;
    public MassStorageBlockEntity(BlockPos pos,BlockState state){super(MassStorageContent.ENTITY.get(),pos,state);capacity=MassStorageBlock.capacity(state.getValue(MassStorageBlock.META));}
    private static boolean compatible(ItemStack a,ItemStack b){return !a.isEmpty()&&!b.isEmpty()&&ItemStack.isSameItemSameComponents(a,b);}
    public int getStockpile(){return stockpile;}
    public int getCapacity(){return capacity;}
    public ItemStack getFilterType(){return slots.get(1).copy();}
    public void setStockpile(int value){stockpile=value;setChanged();}
    public boolean canInsert(ItemStack stack){return stockpile<capacity&&compatible(stack,slots.get(1));}
    public void tick(){
        int newRed=stockpile*15/capacity;
        if(newRed!=redstone){redstone=newRed;setChanged();level.updateNeighbourForOutputSignal(worldPosition,getBlockState().getBlock());}
        if(isItem(slots.get(0),"fluid_barrel_infinite"))stockpile=capacity;
        if(getFilterType().isEmpty())stockpile=0;
        if(canInsert(slots.get(0))){int amount=Math.min(capacity-stockpile,slots.get(0).getCount());removeItem(0,amount);stockpile+=amount;setChanged();}
        if(output&&!getFilterType().isEmpty()){
            if(!slots.get(2).isEmpty()&&!compatible(slots.get(2),getFilterType()))return;
            int amount=Math.min(stockpile,getFilterType().getMaxStackSize());
            if(amount>0){if(slots.get(2).isEmpty())slots.set(2,slots.get(1).copyWithCount(amount));else{amount=Math.min(amount,slots.get(2).getMaxStackSize()-slots.get(2).getCount());slots.get(2).grow(amount);}stockpile-=amount;setChanged();}
        }
        MassStoragePackets.synchronize(this);
    }
    public boolean quickInsert(ItemStack stack){if(!canInsert(stack)||capacity-stockpile<stack.getCount())return false;stockpile+=stack.getCount();stack.setCount(0);setChanged();return true;}
    public ItemStack quickExtract(){
        //An unconfigured source MSU dereferenced null here; the modern empty stack safely returns no item.
        if(!output||getFilterType().isEmpty())return ItemStack.EMPTY;int amount=getFilterType().getMaxStackSize();if(stockpile<amount)return ItemStack.EMPTY;stockpile-=amount;setChanged();return slots.get(1).copyWithCount(amount);
    }
    /** Exact source AE2-facing accounting, retained independently of any optional external storage mod. */
    public int getTotalStockpile(){var type=getFilterType();if(type.isEmpty())return 0;return stockpile+(compatible(type,slots.get(0))?slots.get(0).getCount():0)+(compatible(type,slots.get(2))?slots.get(2).getCount():0);}
    public int increaseTotalStockpile(int amount,boolean actually){return changeTotalStockpile(amount,actually,1);}
    public int decreaseTotalStockpile(int amount,boolean actually){return changeTotalStockpile(amount,actually,-1);}
    private int changeTotalStockpile(int amount,boolean actually,int sign){
        var type=getFilterType();if(type.isEmpty())return amount;int available=sign>0?capacity-stockpile:stockpile;
        if(amount>0&&available>0){int change=Math.min(amount,available);if(actually)stockpile+=sign*change;amount-=change;}
        for(int slot:new int[]{0,2}){var stack=slots.get(slot);available=compatible(type,stack)?(sign>0?stack.getMaxStackSize()-stack.getCount():stack.getCount()):stack.isEmpty()&&sign>0?type.getMaxStackSize():0;
            if(amount>0&&available>0){int change=Math.min(amount,available);if(actually){if(stack.isEmpty())slots.set(slot,type.copyWithCount(change));else{stack.grow(sign*change);if(stack.isEmpty())slots.set(slot,ItemStack.EMPTY);}}amount-=change;}
        }
        if(actually)setChanged();return amount;
    }
    public void provide(boolean fullStack){
        if(getFilterType().isEmpty()||stockpile==0)return;int amount=Math.min(fullStack?getFilterType().getMaxStackSize():1,stockpile);
        if(!slots.get(2).isEmpty()&&!compatible(slots.get(2),getFilterType()))return;
        if(slots.get(2).isEmpty())slots.set(2,slots.get(1).copyWithCount(amount));else{amount=Math.min(amount,slots.get(2).getMaxStackSize()-slots.get(2).getCount());slots.get(2).grow(amount);}stockpile-=amount;setChanged();
    }
    public void receiveControl(Player player,CompoundTag data){
        if(data.contains("provide")&&!getFilterType().isEmpty()){
            if(stockpile==0||!slots.get(2).isEmpty()&&!compatible(slots.get(2),getFilterType()))return;
            provide(data.getBoolean("provide"));
        }
        if(data.contains("toggle"))toggleOutput();
        if(data.contains("slot")&&stockpile<=0&&level!=null)receiveFilter(data.getInt("slot"),ItemStack.parseOptional(level.registryAccess(),data.getCompound("stack")));
    }
    public void toggleOutput(){output=!output;setChanged();}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    @Override public int getContainerSize(){return 3;}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int index){return slots.get(index);}
    @Override public ItemStack removeItem(int index,int amount){var stack=slots.get(index);if(stack.isEmpty())return ItemStack.EMPTY;if(stack.getCount()<=amount){slots.set(index,ItemStack.EMPTY);return stack;}var result=stack.split(amount);if(stack.isEmpty())slots.set(index,ItemStack.EMPTY);setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int index){return ContainerHelper.takeItem(slots,index);}
    @Override public void setItem(int index,ItemStack stack){slots.set(index,stack);stack.limitSize(64);setChanged();}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public int getMaxStackSize(){return 64;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return true;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,2};}
    @Override public boolean canPlaceItemThroughFace(int index,ItemStack stack,Direction side){return !isLocked()&&index==0&&(getFilterType().isEmpty()||compatible(stack,getFilterType()));}
    @Override public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return !isLocked()&&index==2;}
    @Override public void startOpen(Player player){sound("block.storageOpen");}
    @Override public void stopOpen(Player player){sound("block.storageClose");}
    private void sound(String sound){if(level!=null)level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get(sound),SoundSource.BLOCKS,.5F,1);}
    public void setCustomName(String name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return Component.translatable("container.massStorage");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new MassStorageMenu(id,inventory,this);}
    public void fillWithSpiders(){hasSpiders=true;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<3;i++)if(!slots.get(i).isEmpty()){var value=(CompoundTag)slots.get(i).save(lookup);value.putByte("slot",(byte)i);list.add(value);}tag.put("items",list);tag.putInt("stack",stockpile);tag.putBoolean("output",output);tag.putInt("capacity",capacity);tag.putByte("redstone",(byte)redstone);tag.putBoolean("spiders",hasSpiders);if(customName!=null)tag.putString("name",customName);
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);slots.clear();for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<3)slots.set(slot,ItemStack.parseOptional(lookup,item));}stockpile=tag.getInt("stack");output=tag.getBoolean("output");capacity=tag.getInt("capacity");if(capacity<=0)capacity=10_000;redstone=tag.getByte("redstone");hasSpiders=tag.getBoolean("spiders");customName=tag.getString("name");displayType=getFilterType();
    }
    @Override public CompoundTag getSettings(Level world,BlockPos pos){var tag=new CompoundTag();var list=new ListTag();if(!getFilterType().isEmpty()){var value=(CompoundTag)getFilterType().save(world.registryAccess());value.putByte("slot",(byte)0);list.add(value);}tag.put("items",list);return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level world,Player player,BlockPos pos){
        //Original IControlReceiverFilter copy path is independent of the GUI's nonempty-stockpile guard.
        var list=tag.getList("items",Tag.TAG_COMPOUND);if(list.size()>1&&index<list.size()){var value=list.getCompound(0);int slot=value.getByte("slot");boolean router=tag.contains("modes")&&slot>index*5&&slot<index*+5;if(slot<2||router){var stack=ItemStack.parseOptional(world.registryAccess(),value);if(!stack.isEmpty()&&slot+1>=0&&slot+1<3)setItem(slot+1,stack);}}
    }
    @Override public String[] infoForDisplay(Level world,BlockPos pos){return new String[]{"copytool.filter"};}
    public int[] getFilterSlots(){return new int[]{1,2};}
    public void receiveFilter(int slot,ItemStack stack){if(slot==1&&stockpile<=0&&!stack.isEmpty())setItem(slot,stack.copyWithCount(1));}
    public String[] getFunctionInfo(){return new String[]{"VAL:type","VAL:fill","VAL:fillpercent","FUN:toggleoutput"};}
    public String provideRORValue(String name){return switch(name){case "VAL:fill"->Integer.toString(stockpile);case "VAL:fillpercent"->Integer.toString(stockpile*100/capacity);case "VAL:type"->getFilterType().isEmpty()?"None":getFilterType().getHoverName().getString();default->null;};}
    public String runRORFunction(String name,String[] params){if(name.equals("FUN:toggleoutput"))toggleOutput();return null;}
    @Override public String computerType(){return "ntm_mass_storage";}
    @Override public String[] computerMethods(){return new String[]{"getFill","getCapacity","getType","getOutputMode","setOutputMode"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments)throws ComputerException{return switch(method){case "getFill"->new Object[]{stockpile};case "getCapacity"->new Object[]{capacity};case "getType"->new Object[]{getFilterType().isEmpty()?"None":getFilterType().getHoverName().getString()};case "getOutputMode"->new Object[]{output};case "setOutputMode"->{output=arguments.checkBoolean(0);setChanged();yield new Object[0];}default->throw new ComputerException("Unknown method");};}
}
