package com.hbm.machine.reactor.dfc.components;
import api.hbm.block.ILaserable;
import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.api.computer.*;
import com.hbm.damage.HbmDamageSources;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import com.hbm.machine.reactor.dfc.DfcCoreEntity;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Source 95%-SPK transmission, cryogel20/tick,50-block axis scan and amsCore beam damage. */
public final class DfcEmitterEntity extends DfcComponentEntity implements EnergyReceiver,SidedEnergyConnector,ILaserable,StandardFluidReceiver,IRORInteractive {
 public final FluidTank tank=new FluidTank(HbmFluids.CRYOGEL,64000);public long joules,prev;public boolean isOn;
 public DfcEmitterEntity(BlockPos pos,BlockState state){super(DfcComponentKind.EMITTER,pos,state);}
 @Override public void tickServer(ServerLevel server){ports(server);watts=Mth.clamp(watts,1,100);long demand=kind.capacity*watts/2000;beam=0;if(joules>0||prev>0){if(tank.getFill()>=20)tank.setFill(tank.getFill()-20);else{server.setBlockAndUpdate(worldPosition,Blocks.LAVA.defaultBlockState());return;}}
  if(isOn){if(power.compareTo(java.math.BigInteger.valueOf(demand))>=0){power=power.subtract(java.math.BigInteger.valueOf(demand));joules+=watts*100L;}prev=joules;if(joules>0){long out=joules*95/100;var dir=direction();if(dir!=null)for(int i=1;i<=50;i++){beam=i;var p=worldPosition.relative(dir,i);var state=server.getBlockState(p);var tile=server.getBlockEntity(p);if(state.getBlock() instanceof ILaserable laser){laser.addEnergy(server,p.getX(),p.getY(),p.getZ(),out,dir);break;}if(tile instanceof ILaserable laser){laser.addEnergy(server,p.getX(),p.getY(),p.getZ(),out,dir);break;}if(tile instanceof DfcCoreEntity core){out=core.burn(out);continue;}if(!state.isAir()){if(state.liquid()){server.playSound(null,p,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,1,1);server.removeBlock(p,false);break;}if(state.getBlock().getExplosionResistance()<6000&&server.random.nextInt(20)==0)server.destroyBlock(p,false);break;}}joules=0;if(dir!=null){var p=worldPosition;var end=p.relative(dir,beam);var box=new AABB(Math.min(p.getX(),end.getX())+.2,Math.min(p.getY(),end.getY())+.2,Math.min(p.getZ(),end.getZ())+.2,Math.max(p.getX(),end.getX())+.8,Math.max(p.getY(),end.getY())+.8,Math.max(p.getZ(),end.getZ())+.8);for(Entity entity:server.getEntities((Entity)null,box)){entity.hurt(HbmDamageSources.source(server,HbmDamageSources.amsCore),50);entity.igniteForSeconds(10);}}}}else{joules=0;prev=0;}sync(250);
 }
 @Override public void addEnergy(Level level,int x,int y,int z,long energy,Direction direction){if(direction!=null&&direction.getOpposite()!=direction())joules+=energy;}
 @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getAllTanks(){return getReceivingTanks();}
 @Override protected void saveMachine(CompoundTag tag){writeExact(tag,"power",power);tag.putInt("watts",watts);tag.putLong("joules",joules);tag.putLong("prev",prev);tag.putBoolean("isOn",isOn);tank.writeToNBT(tag,"tank");}
 @Override protected void loadMachine(CompoundTag tag){power=readExact(tag,"power");watts=tag.getInt("watts");joules=tag.getLong("joules");prev=tag.getLong("prev");isOn=tag.getBoolean("isOn");tank.readFromNBT(tag,"tank");}
 @Override public String[] computerMethods(){return new String[]{"getEnergyInfo","getCryogel","getInput","getInfo","isActive","setActive","setInput"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getEnergyInfo"->new Object[]{getPower(),getMaxPower()};case "getCryogel"->new Object[]{tank.getFill()};case "getInput"->new Object[]{watts};case "getInfo"->new Object[]{getPower(),getMaxPower(),tank.getFill(),watts,isOn};case "isActive"->new Object[]{isOn};case "setActive"->{isOn=args.checkBoolean(0);setChanged();yield new Object[]{};}case "setInput"->{watts=Mth.clamp(args.checkInteger(0),0,100);setChanged();yield new Object[]{};}default->throw new ComputerException("No such method: "+method);};}
 @Override public void control(int value,int button){if(button==0)watts=Mth.clamp(value,1,100);if(button==1)isOn=!isOn;setChanged();}
 @Override public String[] getFunctionInfo(){return new String[]{PREFIX_FUNCTION+"setpower"+NAME_SEPARATOR+"percent",PREFIX_FUNCTION+"toggle",PREFIX_FUNCTION+"switch"+NAME_SEPARATOR+"on/off"};}
 @Override public String runRORFunction(String name,String[] params){if((PREFIX_FUNCTION+"setpower").equals(name)&&params.length>0){watts=IRORInteractive.parseInt(params[0],0,100);setChanged();}if((PREFIX_FUNCTION+"toggle").equals(name)){isOn=!isOn;setChanged();}if((PREFIX_FUNCTION+"switch").equals(name)&&params.length>0){if("on".equals(params[0])){isOn=true;setChanged();}if("off".equals(params[0])){isOn=false;setChanged();}}return null;}
}
