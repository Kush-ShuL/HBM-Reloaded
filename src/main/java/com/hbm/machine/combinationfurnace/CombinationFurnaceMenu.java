package com.hbm.machine.combinationfurnace;
import com.hbm.fluid.*;
import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original four slots, 176x186 screen topology and standard zero-extra-payload opening. */
public final class CombinationFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public CombinationFurnaceMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(4),new SimpleContainerData(10));}
    public CombinationFurnaceMenu(int id,Inventory inventory,Container machine,ContainerData data){super(CombinationFurnaceContent.MENU.get(),id);checkContainerSize(machine,4);checkContainerDataCount(data,10);this.machine=machine;this.data=data;addSlot(new Slot(machine,0,26,36));addSlot(new ElectricFurnaceMenu.SmeltingSlot(inventory.player,machine,1,89,36));addSlot(new Slot(machine,2,136,18));addSlot(new GenericOutputSlot(inventory.player,machine,3,136,54));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,104+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,162));addDataSlots(data);}
    private int integer(int low){return data.get(low)&65535|(data.get(low+1)&65535)<<16;}public int progress(){return integer(0);}public int heat(){return integer(2);}
    public FluidTank tank(){FluidTank tank=new FluidTank(HbmFluids.fromID(data.get(8)&65535),Math.max(1,integer(6)));tank.setFill(integer(4));tank.withPressure(data.get(9));return tank;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack stack=slot.getItem(),original=stack.copy();if(index<=3){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;slot.onQuickCraft(stack,original);}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
