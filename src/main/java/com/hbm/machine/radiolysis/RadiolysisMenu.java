package com.hbm.machine.radiolysis;
import net.minecraft.core.BlockPos;import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.world.*;import net.minecraft.world.entity.player.*;import net.minecraft.world.inventory.*;import net.minecraft.world.item.ItemStack;
/** Source15 plain machine slots except two TakeOnly outputs; original51 total slot and shift order. */
public final class RadiolysisMenu extends AbstractContainerMenu {
 private final Container machine;private final ContainerData data;private final BlockPos pos;
 public RadiolysisMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,resolve(inventory,buffer));}
 private RadiolysisMenu(int id,Inventory inventory,ClientInit init){this(id,inventory,init.container,new SimpleContainerData(5),init.pos);}
 private record ClientInit(Container container,BlockPos pos){}
 private static ClientInit resolve(Inventory inventory,RegistryFriendlyByteBuf buffer){var pos=buffer.readBlockPos();return new ClientInit(inventory.player.level().getBlockEntity(pos) instanceof RadiolysisBlockEntity machine?machine:new SimpleContainer(15),pos);}
 public RadiolysisMenu(int id,Inventory inventory,Container machine,ContainerData data,BlockPos pos){super(RadiolysisContent.MENU.get(),id);this.machine=machine;this.data=data;this.pos=pos;for(int i=0;i<2;i++)for(int j=0;j<5;j++)addSlot(new Slot(machine,j+i*5,188+i*18,8+j*18));addSlot(new Slot(machine,10,34,17));addSlot(output(machine,11,34,53));addSlot(new Slot(machine,12,148,17));addSlot(output(machine,13,148,53));addSlot(new Slot(machine,14,8,53));for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inventory,j+i*9+9,8+j*18,84+i*18));for(int j=0;j<9;j++)addSlot(new Slot(inventory,j,8+j*18,142));addDataSlots(data);}
 private static Slot output(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return false;}};}
 public BlockPos position(){return pos;}public long power(){long value=0;for(int i=0;i<4;i++)value|=(long)(data.get(i)&65535)<<(i*16);return value;}public int heat(){return data.get(4);}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=14){if(!moveItemStackTo(stack,15,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,15,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
