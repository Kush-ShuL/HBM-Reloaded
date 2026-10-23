package com.hbm.client.generator.leviathan;
import com.hbm.machine.generator.leviathan.LeviathanEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
/** Source10cloud/tick, sourcevolume/pitch/range20, noactivationradiusgate, keepaliveonlywhilerunning. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LeviathanSound {
 private static final Map<LeviathanEntity,Loop> SOUNDS=new IdentityHashMap<>();
 public static void tick(LeviathanEntity machine){var mc=Minecraft.getInstance();if(machine.turnTimer>0){var pos=machine.getBlockPos();var dir=DummyableBlock.facing(machine.getBlockState());var side=dir.getClockWise();var random=machine.getLevel().random;for(int i=0;i<10;i++){double x=pos.getX()+.5+dir.getStepX()*(random.nextDouble()+1.25)+random.nextGaussian()*side.getStepX()*.65,y=pos.getY()+2.5+random.nextGaussian()*.65,z=pos.getZ()+.5+dir.getStepZ()*(random.nextDouble()+1.25)+random.nextGaussian()*side.getStepZ()*.65;mc.particleEngine.add(new LeviathanSteam(mc.level,x,y,z,-dir.getStepX()*.2,0,-dir.getStepZ()*.2));}var sound=SOUNDS.get(machine);if(sound==null){sound=new Loop(machine);SOUNDS.put(machine,sound);mc.getSoundManager().play(sound);}sound.update();sound.keepAlive=0;}else{var sound=SOUNDS.get(machine);if(sound!=null){if(machine.fanAcceleration>0)sound.update();else removed(machine);}}}
 public static void removed(LeviathanEntity machine){var sound=SOUNDS.remove(machine);if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var sound:SOUNDS.values()){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);}SOUNDS.clear();}}
 private static final class Loop extends AbstractTickableSoundInstance {
  final LeviathanEntity machine;int keepAlive;
  Loop(LeviathanEntity machine){super(HbmSounds.get("block.chungusTurbineRunning"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();looping=true;attenuation=Attenuation.NONE;update();}
  void update(){float speed=machine.fanAcceleration/25F;pitch=.25F+.75F*speed;float maximum=.5F*speed*(machine.muffled?.1F:1);var player=Minecraft.getInstance().player;volume=maximum*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/20));}
  @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}
 }
 private LeviathanSound(){}
}
