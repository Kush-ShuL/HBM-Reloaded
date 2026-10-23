package com.hbm.machine.assembler;
import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original17+36 slots and exact SlotNonRetarded/SlotCraftingOutput insertion semantics. */
public final class AssemblerMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;private final BlockPos pos;
    public AssemblerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,new SimpleContainer(17),new SimpleContainerData(14),buffer.readBlockPos());}
    public AssemblerMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){super(AssemblerContent.MENU.get(),id);checkContainerSize(machine,17);checkContainerDataCount(data,14);this.machine=machine;this.data=data;this.pos=pos;machine.startOpen(player.player);addSlot(input(machine,0,152,81));addSlot(input(machine,1,35,126));for(int i=0;i<2;i++)addSlot(input(machine,i+2,152,108+i*18));for(int i=0;i<12;i++)addSlot(input(machine,i+4,8+(i%3)*18,18+(i/3)*18));addSlot(new GenericOutputSlot(player.player,machine,16,98,45));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,174+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,232));addDataSlots(data);}
    private static Slot input(Container container,int id,int x,int y){return new Slot(container,id,x,y){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(id,stack);}};}
    public static boolean upgrade(ItemStack stack){return stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem;}
    private long bits(int start){long value=0;for(int i=0;i<4;i++)value|=(data.get(start+i)&65535L)<<(16*i);return value;}
    public BlockPos position(){return pos;}public long power(){return bits(0);}public long capacity(){return bits(4);}public double progress(){return Double.longBitsToDouble(bits(8));}public boolean processing(){return data.get(12)!=0;}public boolean restricted(){return data.get(13)!=0;}
    public void selectRecipe(int index,String selection){if(machine instanceof AssemblerBlockEntity assembler)assembler.selectRecipe(index,selection);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;Slot slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack stack=slot.getItem(),copy=stack.copy();if(index<=16){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,17,slots.size(),true))return ItemStack.EMPTY;}else{int start=stack.getItem() instanceof HbmBattery?0:stack.getItem()==Blueprints.ITEM.get()?1:upgrade(stack)?2:4,end=start==2?4:start==4?16:start+1;if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
