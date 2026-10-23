package com.hbm.client.precisionassembler;
import com.hbm.machine.precisionassembler.PrecisionAssemblerBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Mechanical relocation of TileEntityMachinePrecAss client branch: one shared arm pose, four independent strikers. */
public final class PrecisionAssemblerAnimation {
 private static final Map<PrecisionAssemblerBlockEntity,PrecisionAssemblerAnimation> STATES=new WeakHashMap<>();
 private static final double[] NULL_POSITION={45,-30,45},WORKING_POSITION={45,-15,-5};
 public final double[] armAngles={45,-15,-5},prevArmAngles={45,-15,-5},strikers=new double[4],prevStrikers=new double[4];
 private final boolean[] strikerDir=new boolean[4];private int strikerIndex,strikerDelay,ringDelay;private Loop sound;
 public boolean frame;public double prevRing,ring,ringSpeed,ringTarget;
 public static PrecisionAssemblerAnimation get(PrecisionAssemblerBlockEntity machine){return STATES.computeIfAbsent(machine,key->new PrecisionAssemblerAnimation());}
 public static void tick(PrecisionAssemblerBlockEntity machine){get(machine).update(machine);}
 public static void removed(PrecisionAssemblerBlockEntity machine){var state=STATES.remove(machine);if(state!=null&&state.sound!=null){state.sound.finish();Minecraft.getInstance().getSoundManager().stop(state.sound);}}
 private static void oneShot(PrecisionAssemblerBlockEntity machine,String id,float volume,float pitch){var level=machine.getLevel();var p=machine.getBlockPos();if(level!=null)level.playLocalSound(p.getX(),p.getY(),p.getZ(),HbmSounds.get(id),SoundSource.BLOCKS,volume,pitch,false);}
 private void update(PrecisionAssemblerBlockEntity machine){
  var level=machine.getLevel();if(level==null)return;var client=Minecraft.getInstance();var pos=machine.getBlockPos();
  if(level.getGameTime()%20==0)frame=!level.getBlockState(pos.above(3)).isAir();
  if(machine.processing()&&client.player!=null&&client.player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500){if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(machine);client.getSoundManager().play(sound);}sound.keepAlive=0;sound.update();}
  else if(sound!=null){sound.finish();client.getSoundManager().stop(sound);sound=null;}
  System.arraycopy(armAngles,0,prevArmAngles,0,3);System.arraycopy(strikers,0,prevStrikers,0,4);prevRing=ring;
  for(int i=0;i<4;i++){if(strikerDir[i]){strikers[i]=-.75D;strikerDir[i]=false;if(!machine.muffled)oneShot(machine,"block.assemblerstrike",machine.volume(.5F),1.25F);}else strikers[i]=Math.max(-.75D,Math.min(0D,strikers[i]+.5D));}
  // This section is deliberately outside didProcess: an already-started ring movement settles after processing stops.
  if(ring!=ringTarget){double delta=Math.abs(ringTarget-ring);if(delta<=ringSpeed)ring=ringTarget;if(ringTarget>ring)ring+=ringSpeed;if(ringTarget<ring)ring-=ringSpeed;if(ringTarget==ring){double sub=ringTarget>=360?-360D:360D;ringTarget+=sub;ring+=sub;prevRing+=sub;ringDelay=100+level.random.nextInt(21);}}
  if(machine.processing()){
   if(ring==ringTarget){if(ringDelay>0)ringDelay--;if(ringDelay<=0){ringTarget+=45*(level.random.nextBoolean()?-1:1);ringSpeed=10D+level.random.nextDouble()*5D;if(!machine.muffled)oneShot(machine,"block.assemblerstart",machine.volume(.25F),1.25F+level.random.nextFloat()*.25F);}}
   if(!working(armAngles)&&canArmsMove())move(WORKING_POSITION);
   if(working(armAngles)){strikerDelay--;if(strikerDelay<=0){strikerDir[strikerIndex]=true;strikerIndex=(strikerIndex+1)%4;strikerDelay=strikerIndex==3?10+level.random.nextInt(3):2;}}
  }else{Arrays.fill(strikerDir,false);if(canArmsMove())move(NULL_POSITION);}
  if(working(prevArmAngles)&&!working(armAngles)&&!machine.muffled)oneShot(machine,"block.assemblerstop",machine.volume(.25F),1.25F+level.random.nextFloat()*.25F);
 }
 private boolean canArmsMove(){for(double value:strikers)if(value!=0D)return false;return true;}
 private static boolean working(double[] angles){for(int i=0;i<3;i++)if(angles[i]!=WORKING_POSITION[i])return false;return true;}
 private boolean move(double[] target){boolean didMove=false;for(int i=0;i<3;i++){if(armAngles[i]==target[i])continue;didMove=true;if(Math.abs(armAngles[i]-target[i])<=15D){armAngles[i]=target[i];continue;}armAngles[i]+=armAngles[i]<target[i]?15D:-15D;}return !didMove;}
 public double arm(int index,float partial){return prevArmAngles[index]+(armAngles[index]-prevArmAngles[index])*partial;}
 public double striker(int index,float partial){return prevStrikers[index]+(strikers[index]-prevStrikers[index])*partial;}
 private static final class Loop extends AbstractTickableSoundInstance {
  final PrecisionAssemblerBlockEntity machine;int keepAlive;
  Loop(PrecisionAssemblerBlockEntity machine){super(HbmSounds.get("block.motor"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}
  void update(){pitch=.75F;var player=Minecraft.getInstance().player;volume=machine.volume(.5F)*(player==null?1:Math.max(0F,1F-(float)Math.sqrt(player.distanceToSqr(x,y,z))/15F));}
  @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}
 }
}
