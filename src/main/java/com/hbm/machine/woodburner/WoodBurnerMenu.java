package com.hbm.machine.woodburner;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
public final class WoodBurnerMenu extends AbstractContainerMenu {
    private final Container machine; private final ContainerData data; private final BlockPos pos;
    public WoodBurnerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer) { this(id,player,new SimpleContainer(6),new SimpleContainerData(18),buffer.readBlockPos()); }
    public WoodBurnerMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos) {
        super(WoodBurnerContent.MENU.get(),id); this.machine=machine; this.data=data; this.pos=pos; checkContainerSize(machine,6); checkContainerDataCount(data,18); machine.startOpen(player.player);
        addSlot(manual(machine,0,26,18)); addSlot(output(machine,1,26,54)); addSlot(manual(machine,2,98,54)); addSlot(manual(machine,3,98,18)); addSlot(output(machine,4,98,36)); addSlot(manual(machine,5,143,54));
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) addSlot(new Slot(player,9+row*9+col,8+col*18,104+row*18)); for(int col=0;col<9;col++) addSlot(new Slot(player,col,8+col*18,162)); addDataSlots(data);
    }
    private static Slot manual(Container machine,int index,int x,int y) { return new Slot(machine,index,x,y) { @Override public boolean mayPlace(ItemStack stack) { return true; } }; }
    private static Slot output(Container machine,int index,int x,int y) { return new Slot(machine,index,x,y) { @Override public boolean mayPlace(ItemStack stack) { return false; } }; }
    private int integer(int start) { return data.get(start)&65535|(data.get(start+1)&65535)<<16; }
    public long power() { long value=0; for(int i=0;i<4;i++) value|=(data.get(i)&65535L)<<(i*16); return value; }
    public int burnTime() { return integer(4); } public int maxBurnTime() { return integer(6); } public int powerGenerated() { return integer(8); } public boolean on() { return data.get(10)!=0; } public boolean liquidBurn() { return data.get(11)!=0; } public int tankFill() { return integer(12); } public int tankCapacity() { return integer(14); } public HbmFluidType tankType() { return HbmFluids.fromID(data.get(16)); } public int pressure() { return data.get(17); } public BlockPos position() { return pos; }
    @Override public boolean clickMenuButton(Player player,int button) { return stillValid(player)&&machine instanceof WoodBurnerBlockEntity burner&&burner.control(button); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player); machine.stopOpen(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size()||!getSlot(index).hasItem()) return ItemStack.EMPTY;
        Slot slot=getSlot(index); ItemStack stack=slot.getItem(),copy=stack.copy();
        if(index<=5) { if(!moveItemStackTo(stack,6,slots.size(),true)) return ItemStack.EMPTY; slot.onQuickCraft(stack,copy); }
        else { int target=stack.getItem() instanceof HbmBattery?5:stack.getItem() instanceof FluidIdentifier?2:stack.getBurnTime(RecipeType.SMELTING)>0?0:3; if(!moveItemStackTo(stack,target,target+1,false)) return ItemStack.EMPTY; }
        if(stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged(); return copy;
    }
}
