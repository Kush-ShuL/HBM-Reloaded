package com.hbm.client.pyrooven;
import com.hbm.machine.pyrooven.PyroOvenEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Source local cloud draws, separate vent plume and actual20-tick keepalive loop/range15. */
public final class PyroOvenEffects {
 private static final Map<PyroOvenEntity,Loop> AUDIO=new WeakHashMap<>();
 public static void tick(PyroOvenEntity machine){var mc=Minecraft.getInstance();var level=machine.getLevel();if(level==null)return;var p=machine.getBlockPos();var dir=DummyableBlock.facing(machine.getBlockState());var rot=dir.getCounterClockWise();if(machine.isProgressing){var manager=mc.getSoundManager();var loop=AUDIO.get(machine);if(loop==null||!manager.isActive(loop)){loop=new Loop(machine);AUDIO.put(machine,loop);manager.play(loop);}loop.keepAlive=0;loop.update();if(mc.player!=null&&mc.player.distanceToSqr(p.getX()+.5,p.getY()+3,p.getZ()+.5)<2500)for(double offset:new double[]{-.875,-2.375,.875,2.375})if(level.random.nextInt(20)==0)level.addParticle(ParticleTypes.CLOUD,p.getX()+.5-rot.getStepX()+dir.getStepX()*offset,p.getY()+3,p.getZ()+.5-rot.getStepZ()+dir.getStepZ()*offset,0,.05,0);}else removed(machine);
  if(machine.isVenting&&level.getGameTime()%2==0)LegacyFluidEffects.tower(level,p.getX()+.5-rot.getStepX(),p.getY()+3,p.getZ()+.5-rot.getStepZ(),new LegacyFluidEffects.Tower(10,.25F,2.5F,100+level.random.nextInt(20)).color(0x202020));
 }
 public static void removed(PyroOvenEntity machine){var loop=AUDIO.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
 private static final class Loop extends AbstractTickableSoundInstance{private final PyroOvenEntity machine;private int keepAlive;Loop(PyroOvenEntity machine){super(HbmSounds.get("block.pyroOperate"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}void update(){pitch=1;var player=Minecraft.getInstance().player;volume=(machine.isMuffled()?.1F:1F)*(player==null?1:Math.max(0,(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15)));}@Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}}
 private PyroOvenEffects(){}
}
