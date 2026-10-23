package com.hbm.client.turbofan;
import com.hbm.machine.turbofan.TurbofanEntity;
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
/** Actual range50/keepalive20 turbofan loop, with source momentum volume/pitch and restart behavior. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class TurbofanSound {
 private static final Map<TurbofanEntity,Loop> SOUNDS=new IdentityHashMap<>();public static void tick(TurbofanEntity t){if(t.momentum<=0){removed(t);return;}var sounds=Minecraft.getInstance().getSoundManager();var loop=SOUNDS.get(t);if(loop==null||!sounds.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(t);SOUNDS.put(t,loop);sounds.play(loop);}loop.keepAlive=0;loop.update();}public static void removed(TurbofanEntity t){var loop=SOUNDS.remove(t);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
 @SubscribeEvent public static void unload(LevelEvent.Unload e){if(e.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
 private static final class Loop extends AbstractTickableSoundInstance{final TurbofanEntity t;int keepAlive;Loop(TurbofanEntity t){super(HbmSounds.get("block.turbofanOperate"),SoundSource.BLOCKS,RandomSource.create());this.t=t;looping=true;attenuation=Attenuation.NONE;var p=t.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}void update(){float maximum=t.momentum/50F*(t.isMuffled()?.1F:1F);pitch=t.momentum/200F+.5F+t.afterburner*.16F;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,distance/50F*-maximum+maximum);}@Override public void tick(){if(t.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}}
}
