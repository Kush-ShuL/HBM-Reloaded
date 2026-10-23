package com.hbm.machine.ironfurnace;

import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source slot coordinates and transfer order; input/fuel manual slots intentionally have no added filter. */
public final class IronFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public IronFurnaceMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(5),new SimpleContainerData(9));}
    public IronFurnaceMenu(int id,Inventory inventory,Container machine,ContainerData data){
        super(IronFurnaceContent.MENU.get(),id);checkContainerSize(machine,5);checkContainerDataCount(data,9);this.machine=machine;this.data=data;
        addSlot(new Slot(machine,0,53,17));addSlot(new Slot(machine,1,53,53));addSlot(new Slot(machine,2,71,53));
        addSlot(new ElectricFurnaceMenu.SmeltingSlot(inventory.player,machine,3,125,35));
        addSlot(new Slot(machine,4,17,35){@Override public boolean mayPlace(ItemStack stack){return PrecisionUpgrades.isUpgrade(stack);}});
        for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,84+y*18));
        for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,142));addDataSlots(data);
    }
    private int integer(int low){return data.get(low)&65535|(data.get(low+1)&65535)<<16;}
    public int maxBurnTime(){return integer(0);}public int burnTime(){return integer(2);}public int progress(){return integer(4);}public int processingTime(){return data.get(6);}public boolean canSmelt(){return data.get(8)!=0;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack current=slot.getItem(),original=current.copy();if(index<5){if(!moveItemStackTo(current,5,slots.size(),true))return ItemStack.EMPTY;slot.onQuickCraft(current,original);}else if(!moveItemStackTo(current,0,5,false))return ItemStack.EMPTY;if(current.getCount()==original.getCount())return ItemStack.EMPTY;if(current.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,current);return original;}
}
