package com.hbm.machine.reactor.dfc.components;
import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.energy.*;
import com.hbm.item.dfc.*;
import com.hbm.machine.reactor.dfc.DfcCoreEntity;
import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
/** Original watts^4 HE demand, actual AMS lens long wear, max-field arbitration and15-block unobstructed ray. */
public final class DfcStabilizerEntity extends DfcComponentEntity implements EnergyReceiver,SidedEnergyConnector,IRORValueProvider,IRORInteractive {
 public DfcStabilizerEntity(BlockPos pos,BlockState state){super(DfcComponentKind.STABILIZER,pos,state);}
 private ItemStack lens(){return slots.get(0);}private boolean actualLens(){return lens().is(DfcMaterials.item("ams_lens"));}private long maxDamage(){return ((DfcLensItem)DfcMaterials.item("ams_lens")).maxDamage;}private boolean usable(){return actualLens()&&DfcLensItem.getLensDamage(lens())<maxDamage();}
 @Override public void tickServer(ServerLevel server){ports(server);watts=Mth.clamp(watts,1,100);int demand=(int)Math.pow(watts,4);beam=0;if(power.compareTo(BigInteger.valueOf(demand))>=0&&usable()){var dir=direction();if(dir!=null)for(int i=1;i<=15;i++){var pos=worldPosition.relative(dir,i);if(server.getBlockEntity(pos) instanceof DfcCoreEntity core){core.field=Math.max(core.field,watts);power=power.subtract(BigInteger.valueOf(demand));beam=i;long damage=DfcLensItem.getLensDamage(lens())+watts;if(damage>=maxDamage())slots.set(0,ItemStack.EMPTY);else DfcLensItem.setLensDamage(lens(),damage);setChanged();break;}if(!server.getBlockState(pos).isAir())break;}}sync(250);}
 @Override protected void saveMachine(CompoundTag tag){writeExact(tag,"power",power);tag.putInt("watts",watts);}
 @Override protected void loadMachine(CompoundTag tag){power=readExact(tag,"power");watts=tag.getInt("watts");}
 @Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getInput","getDurability","getInfo","setInput"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{getPower(),getMaxPower()};case "getInput"->new Object[]{watts};case "getDurability"->new Object[]{usable()?DfcLensItem.getLensDamage(lens()):"N/A"};case "getInfo"->new Object[]{getPower(),getMaxPower(),watts,usable()?DfcLensItem.getLensDamage(lens()):"N/A"};case "setInput"->{watts=Mth.clamp(args.checkInteger(0),0,100);setChanged();yield new Object[]{};}default->throw new ComputerException("No such method: "+method);};}
 @Override public void control(int value,int button){if(button==0){watts=Mth.clamp(value,1,100);setChanged();}}
 @Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"durability",PREFIX_VALUE+"durabilitypercent",PREFIX_FUNCTION+"setpower"+NAME_SEPARATOR+"percent"};}
 @Override public String provideRORValue(String name){if((PREFIX_VALUE+"durability").equals(name))return actualLens()?Long.toString(maxDamage()-DfcLensItem.getLensDamage(lens())):"0";if((PREFIX_VALUE+"durabilitypercent").equals(name))return actualLens()?Long.toString((maxDamage()-DfcLensItem.getLensDamage(lens()))*100/maxDamage()):"0";return null;}
 @Override public String runRORFunction(String name,String[] params){if((PREFIX_FUNCTION+"setpower").equals(name)&&params.length>0){watts=IRORInteractive.parseInt(params[0],0,100);setChanged();}return null;}
}
