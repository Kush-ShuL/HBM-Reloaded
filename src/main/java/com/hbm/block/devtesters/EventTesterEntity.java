package com.hbm.block.devtesters;
import com.hbm.multiblock.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Actual400source inventory; int slots prevent original byte alias/loss. No ticking/processing or replacement chest backend. */
public final class EventTesterEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,MuffledMachine {
    public static final int SIZE=400;private final NonNullList<ItemStack> items=NonNullList.withSize(SIZE,ItemStack.EMPTY);
    private Component customName;private boolean muffled,tilted;
    public EventTesterEntity(BlockPos pos,BlockState state){super(DevTestersContent.STORAGE_ENTITY.get(),pos,state);}
    @Override public int getContainerSize(){return SIZE;}
    @Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var result=ContainerHelper.removeItem(items,slot,count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}
    @Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public int getMaxStackSize(){return 64;}
    @Override public void clearContent(){items.clear();setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean stillValid(Player player){if(level instanceof net.minecraft.server.level.ServerLevel server&&server.getChunkSource().getChunkNow(worldPosition.getX()>>4,worldPosition.getZ()>>4)==null)return false;return SourceMachineMenuRange.isValid(this,player);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.literal("Balls"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new EventTesterMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf data){data.writeBlockPos(worldPosition);}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int slot=0;slot<SIZE;slot++)if(!items.get(slot).isEmpty()){var entry=(CompoundTag)items.get(slot).save(lookup);entry.putInt("slot",slot);list.add(entry);}tag.put("items",list);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null){tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));tag.putString("name",customName.getString());}
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);items.clear();for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var entry=(CompoundTag)value;int slot=entry.contains("slot",Tag.TAG_BYTE)?entry.getByte("slot")&255:entry.getInt("slot");if(slot>=0&&slot<SIZE)items.set(slot,ItemStack.parseOptional(lookup,entry));}
        muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");customName=tag.contains("CustomName",Tag.TAG_STRING)?Component.Serializer.fromJson(tag.getString("CustomName"),lookup):tag.contains("name",Tag.TAG_STRING)&&!tag.getString("name").isEmpty()?Component.literal(tag.getString("name")):null;
    }
}
