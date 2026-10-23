package com.hbm.client.largeturbine;
import com.hbm.machine.largeturbine.LargeTurbineEntity;
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
/** Original AudioDynamic range10/keepalive20 and .4*speed volume/.25+.75*speed pitch. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class LargeTurbineSound {
 private static final Map<LargeTurbineEntity,Loop> SOUNDS=new IdentityHashMap<>();
 public static void tick(LargeTurbineEntity turbine){var loop=SOUNDS.get(turbine);if(turbine.shouldTurn){if(loop==null){loop=new Loop(turbine);SOUNDS.put(turbine,loop);Minecraft.getInstance().getSoundManager().play(loop);}loop.keepAlive=0;loop.update();}else if(loop!=null){if(turbine.fanAcceleration>0)loop.update();else removed(turbine);}}
 public static void removed(LargeTurbineEntity turbine){var loop=SOUNDS.remove(turbine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
 @SubscribeEvent public static void unload(LevelEvent.Unload e){if(e.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
 private static final class Loop extends AbstractTickableSoundInstance {final LargeTurbineEntity turbine;int keepAlive;Loop(LargeTurbineEntity turbine){super(HbmSounds.get("block.largeTurbineRunning"),SoundSource.BLOCKS,RandomSource.create());this.turbine=turbine;looping=true;attenuation=Attenuation.NONE;var pos=turbine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}void update(){float speed=turbine.fanAcceleration/15,maximum=.4F*speed*(turbine.isMuffled()?.1F:1F);pitch=.25F+.75F*speed;var p=Minecraft.getInstance().player;float distance=p==null?0:(float)Math.sqrt(p.distanceToSqr(x,y,z));volume=Math.max(0,distance/10F*-maximum+maximum);}@Override public void tick(){if(turbine.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}}
}
