package com.hbm.pneumatic.tube;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Fifteen real non-consuming ghost patterns; right-click cycles original matcher and shift transfer is disabled. */
public final class PneumoTubeMenu extends AbstractContainerMenu {
 public final PneumoTubeBlockEntity tube;public final boolean endpointOnly;
 public PneumoTubeMenu(int id,Inventory inv,RegistryFriendlyByteBuf buffer){this(id,inv,resolve(inv,buffer));}private PneumoTubeMenu(int id,Inventory inv,Init init){this(id,inv,init.tube,init.endpointOnly);}private record Init(PneumoTubeBlockEntity tube,boolean endpointOnly){}private static Init resolve(Inventory inv,RegistryFriendlyByteBuf buffer){var pos=buffer.readBlockPos();boolean endpoint=buffer.readBoolean();var tile=inv.player.level().getBlockEntity(pos);return new Init(tile instanceof PneumoTubeBlockEntity tube?tube:new PneumoTubeBlockEntity(pos,PneumaticTubeContent.TUBE.get().defaultBlockState()),endpoint);}
 public PneumoTubeMenu(int id,Inventory inv,PneumoTubeBlockEntity tube,boolean endpointOnly){super(PneumaticTubeContent.MENU.get(),id);this.tube=tube;this.endpointOnly=endpointOnly;for(int i=0;i<3;i++)for(int j=0;j<5;j++)addSlot(new Slot(tube,i*5+j,35+j*18,17+i*18){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}});for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inv,j+i*9+9,8+j*18,103+i*18));for(int i=0;i<9;i++)addSlot(new Slot(inv,i,8+i*18,161));}
 @Override public void clicked(int index,int button,ClickType mode,Player player){if(index<0||index>=15){super.clicked(index,button,mode,player);return;}var slot=slots.get(index);if(button==1&&mode==ClickType.PICKUP&&slot.hasItem()){if(!player.level().isClientSide)tube.pattern.nextMode(slot.getItem(),index);}else{slot.set(getCarried());if(!player.level().isClientSide)tube.pattern.initPatternSmart(slot.getItem(),index);}tube.changed();broadcastChanges();}
 @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
 @Override public boolean clickMenuButton(Player player,int button){if(!stillValid(player)||button<0||button>4||endpointOnly&&button!=4)return false;if(!player.level().isClientSide)tube.control(button);return true;}
 @Override public boolean stillValid(Player player){return tube.stillValid(player);}
}
