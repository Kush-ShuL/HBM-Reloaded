package com.hbm.machine.electricpress;

import com.hbm.item.StampItem;
import com.hbm.item.battery.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Actual TileEntityMachineEPress integer head motion and paid delay/retraction,not a generic progress processor. */
public final class ElectricPressEntity extends PoweredPressEntity implements MenuProvider {
    public static final int MAX_PRESS=200;
    public int press;
    public double renderPress,lastPress;
    private int syncPress,turnProgress,delay;
    private boolean retracting;
    public ItemStack renderedInput=ItemStack.EMPTY;
    public ElectricPressEntity(BlockPos pos,BlockState state){super(ElectricPressContent.ELECTRIC_ENTITY.get(),pos,state,5);}
    @Override protected String sourceName(){return "container.epress";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ElectricPressMenu(id,inventory,this,worldPosition);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    private void chargeBattery(){var battery=slots.get(0);if(battery.getItem() instanceof CreativeBatteryItem){power=CAPACITY;return;}if(battery.getItem() instanceof HbmBattery item){long amount=Math.min(Math.min(CAPACITY.subtract(power).longValueExact(),item.getDischargeRate(battery)),item.getCharge(battery));if(amount>0){item.dischargeBattery(battery,amount);power=power.add(BigInteger.valueOf(amount));}}}
    private boolean canProcess(){if(power.compareTo(BigInteger.valueOf(100))<0||slots.get(1).isEmpty()||slots.get(2).isEmpty())return false;var output=ElectricPressRecipes.output(level,slots.get(2),slots.get(1));var existing=slots.get(3);return !output.isEmpty()&&(existing.isEmpty()||ElectricPressRecipes.sameLegacy(existing,output)&&existing.getCount()+output.getCount()<=existing.getMaxStackSize());}
    @Override protected void serverTick(ServerLevel server){
        if(getBlockState().getValue(com.hbm.multiblock.DummyableBlock.LEGACY_META)<12)server.scheduleTick(worldPosition,getBlockState().getBlock(),1);
        var oldPower=power;chargeBattery();if(!oldPower.equals(power))setChanged();boolean canProcess=canProcess();
        if((canProcess||retracting||delay>0)&&power.compareTo(BigInteger.valueOf(100))>=0){
            power=power.subtract(BigInteger.valueOf(100));
            if(delay<=0){int level=slots.get(4).getItem() instanceof FurnaceUpgradeItem upgrade&&upgrade.type()==FurnaceUpgradeItem.Type.SPEED?Math.min(3,upgrade.tier()):0;int speed=1+level;int stampSpeed=(int)((retracting?20:45)*(1D+speed/4D));
                if(retracting){press-=stampSpeed;if(press<=0){retracting=false;delay=5-speed+1;}}
                else if(canProcess){press+=stampSpeed;if(press>=MAX_PRESS){server.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("block.pressOperate"),SoundSource.BLOCKS,muffled?.15F:1.5F,1);var output=ElectricPressRecipes.output(server,slots.get(2),slots.get(1));if(slots.get(3).isEmpty())slots.set(3,output.copy());else slots.get(3).grow(output.getCount());removeItem(2,1);ElectricPressRecipes.wearStamp(slots.get(1));if(slots.get(1).isEmpty())slots.set(1,ItemStack.EMPTY);retracting=true;delay=5-speed+1;}}
                else if(press>0)retracting=true;
            }else delay--;
            setChanged();
        }
    }
    @Override protected void clientTick(){lastPress=renderPress;if(turnProgress>0){renderPress+=(syncPress-renderPress)/(double)turnProgress;turnProgress--;}else renderPress=syncPress;}
    public double renderedPress(float partial){return lastPress+(renderPress-lastPress)*partial;}
    @Override protected void savePress(CompoundTag tag){tag.putInt("press",press);tag.putBoolean("ret",retracting);}
    @Override protected void loadPress(CompoundTag tag){press=tag.getInt("press");retracting=tag.getBoolean("ret");}
    @Override protected void writeSync(CompoundTag tag,HolderLookup.Provider lookup){tag.putInt("press",press);if(!slots.get(2).isEmpty())tag.put("input",slots.get(2).save(lookup));}
    @Override protected void readSync(CompoundTag tag,HolderLookup.Provider lookup){syncPress=tag.getInt("press");press=syncPress;renderedInput=tag.contains("input")?ItemStack.parseOptional(lookup,tag.getCompound("input")):ItemStack.EMPTY;turnProgress=2;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return stack.getItem() instanceof StampItem?slot==1:slot==2;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{1,2,3};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==3;}
    @Override public List<ProgressView> progressBars(){return press>0||retracting?List.of(new ProgressView("gui.hbm.electricpress.progress",press,MAX_PRESS,0x71c78b)):List.of();}
}
