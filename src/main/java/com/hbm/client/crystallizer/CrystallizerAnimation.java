package com.hbm.client.crystallizer;
import com.hbm.machine.crystallizer.CrystallizerBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Original spinner5degrees per subcycle, cloud1/20 and real chemplant .75pitch/15block attenuation loop. */
public final class CrystallizerAnimation {
 private static final Map<CrystallizerBlockEntity,Loop> SOUNDS=new WeakHashMap<>();
 public static void tick(CrystallizerBlockEntity machine){var level=machine.getLevel();if(level==null)return;machine.prevAngle=machine.angle;if(machine.isOn){machine.angle+=5F*machine.cycleCount();if(machine.angle>=360){machine.angle-=360;machine.prevAngle-=360;}var p=machine.getBlockPos();var player=Minecraft.getInstance().player;if(level.random.nextInt(20)==0&&player!=null&&player.distanceToSqr(p.getX()+.5,p.getY()+6,p.getZ()+.5)<2500)level.addParticle(ParticleTypes.CLOUD,p.getX()+level.random.nextDouble(),p.getY()+6.5,p.getZ()+level.random.nextDouble(),0,.1,0);if(player!=null&&player.distanceToSqr(p.getX(),p.getY(),p.getZ())<625){var manager=Minecraft.getInstance().getSoundManager();var sound=SOUNDS.get(machine);if(sound==null||!manager.isActive(sound)){if(sound!=null)sound.finish();sound=new Loop(machine);SOUNDS.put(machine,sound);manager.play(sound);}sound.keepAlive=0;sound.update();return;}}removed(machine);}
 public static void removed(CrystallizerBlockEntity machine){var sound=SOUNDS.remove(machine);if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}}
 private static final class Loop extends AbstractTickableSoundInstance {private final CrystallizerBlockEntity machine;private int keepAlive;Loop(CrystallizerBlockEntity machine){super(HbmSounds.get("block.chemicalPlant"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();pitch=.75F;update();}void update(){var player=Minecraft.getInstance().player;volume=(machine.muffled?.1F:1F)*Math.max(0,player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}@Override public void tick(){if(machine.isRemoved()||++keepAlive>15)finish();update();}void finish(){stop();}}
 private CrystallizerAnimation(){}
}
