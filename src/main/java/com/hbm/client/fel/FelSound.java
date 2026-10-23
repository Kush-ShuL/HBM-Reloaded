package com.hbm.client.fel;
import com.hbm.machine.fel.FelBlockEntity;import com.hbm.registry.HbmSounds;import java.util.*;import net.minecraft.client.Minecraft;import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;import net.minecraft.sounds.SoundSource;import net.minecraft.util.RandomSource;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.event.level.LevelEvent;
/** Literal +2/-3 duration and10tick start threshold, source integercore/range10/volume2/ramp pitch. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FelSound {
 private static final Map<FelBlockEntity,State> STATES=new IdentityHashMap<>();
 public static void tick(FelBlockEntity machine){var state=STATES.computeIfAbsent(machine,ignored->new State());state.duration=net.minecraft.util.Mth.clamp(state.duration+(machine.visuallyPowered()&&machine.distance-3>0?2:-3),0,60);var manager=Minecraft.getInstance().getSoundManager();if(state.duration>10){if(state.loop==null||!manager.isActive(state.loop)){if(state.loop!=null){state.loop.finish();manager.stop(state.loop);}state.loop=new Loop(machine,state);manager.play(state.loop);}state.loop.update();state.loop.keepAlive=0;}else if(state.loop!=null){state.loop.finish();manager.stop(state.loop);state.loop=null;}}
 public static void removed(FelBlockEntity machine){var state=STATES.remove(machine);if(state!=null&&state.loop!=null){state.loop.finish();Minecraft.getInstance().getSoundManager().stop(state.loop);}}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide())for(var machine:List.copyOf(STATES.keySet()))removed(machine);}
 private static final class State {int duration;Loop loop;}
 private static final class Loop extends AbstractTickableSoundInstance {
  final FelBlockEntity machine;final State state;int keepAlive;
  Loop(FelBlockEntity machine,State state){super(HbmSounds.get("block.fel"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;this.state=state;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();looping=true;attenuation=Attenuation.NONE;update();}
  void update(){var player=Minecraft.getInstance().player;volume=2*(machine.isMuffled()?.1F:1)*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/10));pitch=(state.duration-10)/100F+.5F;}
  @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();else update();}void finish(){stop();}
 }
 private FelSound(){}
}
