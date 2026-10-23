package com.hbm.machine.steelfurnace;
import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source three input/output lanes and original transfer ranges, native15 integer halves. */
public final class SteelFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public SteelFurnaceMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(6),new SimpleContainerData(15));}
    public SteelFurnaceMenu(int id,Inventory inventory,Container machine,ContainerData data){super(SteelFurnaceContent.MENU.get(),id);checkContainerSize(machine,6);checkContainerDataCount(data,15);this.machine=machine;this.data=data;for(int i=0;i<3;i++)addSlot(new Slot(machine,i,35,17+18*i));for(int i=0;i<3;i++)addSlot(new ElectricFurnaceMenu.SmeltingSlot(inventory.player,machine,i+3,125,17+18*i));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,84+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,142));addDataSlots(data);}
    private int integer(int low){return data.get(low)&65535|(data.get(low+1)&65535)<<16;}
    public int progress(int lane){return integer(lane*2);}public int bonus(int lane){return integer(6+lane*2);}public int heat(){return integer(12);}public boolean wasOn(){return data.get(14)!=0;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack stack=slot.getItem(),original=stack.copy();if(index<=5){if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;slot.onQuickCraft(stack,original);}else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
