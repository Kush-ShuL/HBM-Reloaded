package com.hbm.client.fensu;

import com.hbm.machine.fensu.FensuBlockEntity;
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
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** Actual fensuHum: source wheel-motion/30-block activation check,25 attenuation range,5-tick keepalive and pitch curve. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FensuSound {
    private static final Map<FensuBlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(FensuBlockEntity machine){var client=Minecraft.getInstance();var pos=machine.getBlockPos();if(machine.prevRotation==machine.rotation||client.player==null||client.player.distanceToSqr(pos.getX()+.5,pos.getY()+5.5,pos.getZ()+.5)>=900){removed(machine);return;}var loop=SOUNDS.get(machine);if(loop==null||!client.getSoundManager().isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(machine);SOUNDS.put(machine,loop);client.getSoundManager().play(loop);}loop.keepAlive=0;loop.update();}
    public static void removed(FensuBlockEntity machine){var loop=SOUNDS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
    @SubscribeEvent public static void clock(RenderLevelStageEvent event){if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_LEVEL)FensuClient.renderClock=System.currentTimeMillis();}
    private static final class Loop extends AbstractTickableSoundInstance{
        private final FensuBlockEntity machine;private int keepAlive;
        Loop(FensuBlockEntity machine){super(HbmSounds.get("block.fensuHum"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}
        void update(){pitch=.5F+machine.speed()/15F*1.5F;var player=Minecraft.getInstance().player;volume=machine.volume(1.5F)*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/25));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>5)finish();else update();}void finish(){stop();}
    }
    private FensuSound(){}
}
