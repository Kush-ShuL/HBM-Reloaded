package com.hbm.machine.ashpit;
import com.hbm.heat.AshReceiver;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.fuel.HbmFuels;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original five-channel ash accumulator with exact first-empty-slot behavior. No fluid or energy interface. */
public final class AshpitBlockEntity extends ThermalMachine implements AshReceiver {
 public final int[] ash=new int[5];public int playersUsing;public float doorAngle,previousDoorAngle;public boolean isFull;
 public AshpitBlockEntity(BlockPos pos,BlockState state){super(AshpitContent.ENTITY.get(),pos,state,5);}
 @Override protected String title(){return "container.ashpit";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new AshpitMenu(id,inventory,this,worldPosition);}
 public static int ashFromFuel(ItemStack stack){for(String name:HbmFuels.tags(stack)){if(HbmFuels.isCoalLikeTag(name))return 1;if(HbmFuels.isLogTag(name)||HbmFuels.isWoodTag(name)||HbmFuels.isSaplingTag(name))return 0;}return stack.is(Items.CHARCOAL)?0:2;}
 @Override public void addFuelAsh(ItemStack fuel,int sourceBurnTime){ash[ashFromFuel(fuel)]+=sourceBurnTime;setChanged();}
 /** ChimneyBase's compound long-to-int addition is intentionally retained. */
 public void addSmokeAsh(long fly,long soot){ash[3]+=fly;ash[4]+=soot;setChanged();}
 public static ItemStack ashStack(int type){return com.hbm.item.ash.FlatAshContent.stack(type,1);}
 public static void tick(Level level,BlockPos pos,BlockState state,AshpitBlockEntity self){if(!level.isClientSide){for(int type=0;type<5;type++)if(self.process(self.ash[type],type,AshpitConfig.THRESHOLDS[type]))self.ash[type]-=AshpitConfig.THRESHOLDS[type];self.isFull=!self.isEmpty();self.sync(50);}else{self.previousDoorAngle=self.doorAngle;float speed=self.doorAngle/10F+3;self.doorAngle=net.minecraft.util.Mth.clamp(self.doorAngle+(self.playersUsing>0?speed:-speed),0,135);}}
 private boolean process(int level,int type,int threshold){if(level>=threshold)for(int i=0;i<5;i++){var slot=slots.get(i);if(slot.isEmpty()){slots.set(i,ashStack(type));/* X5846 also debits WOOD here, even for another ash type. Keep source semantics. */ash[0]-=threshold;return true;}if(slot.getCount()<slot.getMaxStackSize()&&slot.getItem()==ashStack(type).getItem()){slot.grow(1);return true;}}return false;}
 @Override public void startOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing++;}@Override public void stopOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing--;}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4};}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
 @Override protected void saveMachine(CompoundTag tag){for(int i=0;i<5;i++)tag.putInt("ashLevel"+AshpitConfig.NAMES[i],ash[i]);tag.putInt("playersUsing",playersUsing);tag.putBoolean("isFull",isFull);}@Override protected void loadMachine(CompoundTag tag){for(int i=0;i<5;i++)ash[i]=tag.getInt("ashLevel"+AshpitConfig.NAMES[i]);if(level!=null&&level.isClientSide){playersUsing=tag.getInt("playersUsing");isFull=tag.getBoolean("isFull");}}
 @Override public List<ProgressView> progressBars(){var list=new ArrayList<ProgressView>();for(int i=0;i<5;i++)if(ash[i]>0)list.add(new ProgressView("jade.hbm.ashpit."+AshpitConfig.NAMES[i].toLowerCase(Locale.ROOT),ash[i],AshpitConfig.THRESHOLDS[i],0xB0A38E));return list;}
}
