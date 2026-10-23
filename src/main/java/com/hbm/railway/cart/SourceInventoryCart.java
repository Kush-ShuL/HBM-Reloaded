package com.hbm.railway.cart;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
/** Source ContainerBase's own exact inventory, not native fixed36 slots or a new item-storage component. */
public abstract class SourceInventoryCart extends SourceCartEntity implements Container,MenuProvider {
    protected final NonNullList<ItemStack> items;
    protected SourceInventoryCart(EntityType<?> type,Level level,SourceCartKind kind,int slots){super(type,level,kind);items=NonNullList.withSize(slots,ItemStack.EMPTY);}
    @Override public int getContainerSize(){return items.size();}
    @Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int index){return items.get(index);}
    @Override public ItemStack removeItem(int index,int count){return ContainerHelper.removeItem(items,index,count);}
    @Override public ItemStack removeItemNoUpdate(int index){return ContainerHelper.takeItem(items,index);}
    @Override public void setItem(int index,ItemStack stack){items.set(index,stack);if(stack.getCount()>64)stack.setCount(64);}
    @Override public int getMaxStackSize(){return 64;}
    @Override public void setChanged(){}
    @Override public void clearContent(){items.clear();}
    @Override public boolean stillValid(Player player){return !isRemoved()&&player.distanceToSqr(this)<=64;}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return true;}
    @Override public Component getDisplayName(){return hasCustomName()?getCustomName():Component.translatable("container.minecart");}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf data){data.writeInt(getId());}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);ContainerHelper.saveAllItems(tag,items,registryAccess());}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);items.clear();ContainerHelper.loadAllItems(tag,items,registryAccess());}
}
