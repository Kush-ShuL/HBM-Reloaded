package com.hbm.machine.custommachine;
import com.google.gson.JsonParser;
import com.hbm.pneumatic.PatternMatcher;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact source dynamic1+fluids+inputs+ghosts+outputs topology, disabled quick-move and count1 patterns. */
public final class CustomMachineMenu extends AbstractContainerMenu {
    public final CustomMachineEntity machine;
    public CustomMachineMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,read(inventory,buffer));}
    private static CustomMachineEntity read(Inventory inventory,RegistryFriendlyByteBuf buffer){BlockPos pos=buffer.readBlockPos();String type=buffer.readUtf();var definition=new CustomMachinesConfig.MachineConfiguration(JsonParser.parseString(buffer.readUtf(1048576)).getAsJsonObject());var machine=inventory.player.level().getBlockEntity(pos)instanceof CustomMachineEntity actual?actual:new CustomMachineEntity(pos,CustomMachineContent.BLOCK.get().defaultBlockState());machine.machineType=type;if(machine.config==null)machine.init(definition);return machine;}
    public CustomMachineMenu(int id,Inventory inventory,CustomMachineEntity machine){super(CustomMachineContent.MENU.get(),id);this.machine=machine;addSlot(unrestricted(0,150,72));for(int i=0;i<machine.inputTanks.length;i++)addSlot(unrestricted(1+i,8+18*i,54));for(int i=0;i<Math.min(machine.config.itemInCount,6);i++)addSlot(unrestricted(4+i,8+i%3*18,72+i/3*18));for(int i=0;i<Math.min(machine.config.itemInCount,6);i++)addSlot(new PatternSlot(machine,10+i,8+i%3*18,108+i/3*18));for(int i=0;i<Math.min(machine.config.itemOutCount,6);i++)addSlot(new GenericOutputSlot(inventory.player,machine,16+i,78+i%3*18,72+i/3*18));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,174+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,232));}
    private Slot unrestricted(int slot,int x,int y){return new Slot(machine,slot,x,y){@Override public boolean mayPlace(ItemStack stack){return true;}};}
    @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void clicked(int index,int button,ClickType type,Player player){if(index<0||index>=slots.size()||!(slots.get(index)instanceof PatternSlot)){super.clicked(index,button,type,player);return;}var slot=slots.get(index);int pattern=slot.getContainerSlot()-10;if(button==1&&type==ClickType.PICKUP&&slot.hasItem())machine.matcher.nextMode(slot.getItem(),pattern);else{slot.set(getCarried());machine.matcher.initPatternSmart(slot.getItem(),pattern);}machine.setChanged();}
    private static final class PatternSlot extends Slot {
        PatternSlot(CustomMachineEntity machine,int slot,int x,int y){super(machine,slot,x,y);}
        @Override public boolean mayPickup(Player player){return false;}
        @Override public int getMaxStackSize(){return 1;}
        @Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}
    }
}
