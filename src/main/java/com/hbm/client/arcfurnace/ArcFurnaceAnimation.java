package com.hbm.client.arcfurnace;
import com.hbm.client.particle.LegacyCoolingTowerParticle;
import com.hbm.machine.arcfurnace.ArcFurnaceBlockEntity;
import com.hbm.machine.reactor.zirnox.client.ZirnoxClient;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Original two-step lid interpolation, separate moving/processing loops, smoke and RBMK flame emissions. */
final class ArcFurnaceAnimation {
 private static final Map<ArcFurnaceBlockEntity,ArcFurnaceAnimation> STATES=new WeakHashMap<>();private Loop moving,working;
 static void removed(ArcFurnaceBlockEntity machine){var state=STATES.remove(machine);if(state!=null){state.stop(true);state.stop(false);}}
 static void tick(ArcFurnaceBlockEntity machine){STATES.computeIfAbsent(machine,key->new ArcFurnaceAnimation()).update(machine);}
 private void update(ArcFurnaceBlockEntity arc){var client=Minecraft.getInstance();if(arc.getLevel()==null||client.level==null)return;arc.prevLid=arc.lid;if(arc.approachNum>0){arc.lid+=(arc.syncLid-arc.lid)/arc.approachNum;arc.approachNum--;}else arc.lid=arc.syncLid;
  if(arc.lid!=arc.prevLid)keep(arc,true);else stop(true);
  var pos=arc.getBlockPos();if((arc.lid==1||arc.lid==0)&&arc.lid!=arc.prevLid&&!(arc.prevLid==0&&arc.lid==1))client.getSoundManager().play(new SimpleSoundInstance(HbmSounds.get("door.wgh_stop"),SoundSource.BLOCKS,arc.volume(1),1,client.level.random,pos));
  if(arc.isProgressing)keep(arc,false);else stop(false);
  boolean near=client.player!=null&&client.player.distanceToSqr(pos.getX()+.5,pos.getY()+4,pos.getZ()+.5)<2500;
  if(arc.lid!=arc.prevLid&&arc.lid>arc.prevLid&&!(arc.prevLid==0&&arc.lid==1)&&near){var random=client.level.random;int life=70+random.nextInt(30);double x=pos.getX()+.5+random.nextGaussian()*.5,z=pos.getZ()+.5+random.nextGaussian()*.5;for(int i=0;i<3;i++){var particle=new LegacyCoolingTowerParticle(client.level,x,pos.getY()+4,z);particle.setLift(.01F);particle.setBaseScale(.5F);particle.setMaxScale(2);particle.setLife(life);particle.noWind();particle.alphaMod(arc.prevLid/arc.lid);particle.setColor(0,0,0);particle.setStrafe(.05F);client.particleEngine.add(particle);}}
  if(arc.lid!=arc.prevLid&&arc.lid<arc.prevLid&&arc.lid>.5F&&arc.hasMaterial&&near&&client.level.random.nextInt(5)==0){double x=pos.getX()+.5+client.level.random.nextGaussian()*.5,z=pos.getZ()+.5+client.level.random.nextGaussian()*.5;for(int i=0;i<2;i++)ZirnoxClient.flame(x,pos.getY()+2.75,z,50);}
 }
 private void keep(ArcFurnaceBlockEntity arc,boolean lid){var client=Minecraft.getInstance();var sound=lid?moving:working;if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(arc,lid);client.getSoundManager().play(sound);if(lid)moving=sound;else working=sound;}sound.keepAlive=0;sound.update();}
 private void stop(boolean lid){var sound=lid?moving:working;if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);if(lid)moving=null;else working=null;}}
 private static final class Loop extends AbstractTickableSoundInstance {
  private final ArcFurnaceBlockEntity machine;private final boolean lid;private int keepAlive;
  Loop(ArcFurnaceBlockEntity machine,boolean lid){super(HbmSounds.get(lid?"door.wgh_start":"block.electricHum"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;this.lid=lid;looping=true;attenuation=Attenuation.NONE;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}
  void update(){pitch=lid?1:.75F;var player=Minecraft.getInstance().player;volume=machine.volume(lid?.75F:1.5F)*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/15));}
  @Override public void tick(){if(machine.isRemoved()||++keepAlive>5)finish();}void finish(){stop();}
 }
}
