package com.hbm.machine.reactor.rbmk.storage;
import com.hbm.machine.reactor.rbmk.channel.RbmkLoadable;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem;
import com.hbm.multiblock.NamedMultiblock;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Actual twelve-slot storage: one forward bubble pass every10ticks, no invented per-rod cooling/burn. */
public final class RbmkStorageEntity extends RbmkColumnEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,RbmkLoadable {
    private final NonNullList<ItemStack> items=NonNullList.withSize(12,ItemStack.EMPTY);private Component customName;
    public RbmkStorageEntity(BlockPos pos,BlockState state){super(RbmkStorageContent.ENTITY.get(),pos,state);}
    @Override public RbmkFluxColumn.Type neutronType(){return RbmkFluxColumn.Type.OTHER;}@Override public String consoleType(){return "STORAGE";}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkStorageEntity machine){if(level instanceof ServerLevel world){if(world.getGameTime()%10==0)for(int i=0;i<11;i++)if(machine.items.get(i).isEmpty()&&!machine.items.get(i+1).isEmpty()){machine.items.set(i,machine.items.get(i+1));machine.items.set(i+1,ItemStack.EMPTY);machine.setChanged();}machine.baseTick(world);}}
    @Override public boolean canLoad(ItemStack stack){return items.get(11).isEmpty();}@Override public void load(ItemStack stack){items.set(11,stack.copy());setChanged();}@Override public boolean canUnload(){return !items.getFirst().isEmpty();}@Override public ItemStack provideNext(){return items.getFirst();}@Override public void unload(){items.set(0,ItemStack.EMPTY);setChanged();}@Override public boolean coldEnoughForAutoloader(){return true;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,items,lookup);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,items,lookup);if(tag.contains("name"))customName=Component.Serializer.fromJson(tag.getString("name"),lookup);}
    @Override public int getContainerSize(){return 12;}@Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return items.get(slot);}@Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(items,slot,amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}@Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){items.clear();setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=128;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return stack.getItem() instanceof RbmkFuelRodItem;}@Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.range(0,12).toArray();}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.rbmkStorage"):customName;}@Override public void setCustomName(Component name){customName=name;setChanged();}@Override public AbstractContainerMenu createMenu(int id,Inventory player,Player entity){return new RbmkStorageMenu(id,player,this,worldPosition);}
}
