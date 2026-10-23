package com.hbm.client.hephaestus;
import com.hbm.machine.hephaestus.HephaestusEntity;
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
/** Source non-expiring AudioDynamic loop: range10, volume.75, pitch1; no extra muffler multiplication. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HephaestusSound {
    private static final Map<HephaestusEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(HephaestusEntity machine){
        if(machine.bufferedHeat<=0){removed(machine);return;}
        if(!SOUNDS.containsKey(machine)){var sound=new Loop(machine);SOUNDS.put(machine,sound);Minecraft.getInstance().getSoundManager().play(sound);}
    }
    public static void removed(HephaestusEntity machine){var loop=SOUNDS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload e){if(e.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {
        private final HephaestusEntity machine;
        Loop(HephaestusEntity machine){super(HbmSounds.get("block.hephaestusRunning"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY()+5;z=p.getZ();volume=.75F;pitch=1;}
        @Override public boolean canStartSilent(){return true;}
        @Override public void tick(){if(machine.isRemoved()||machine.bufferedHeat<=0){finish();return;}var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,distance/10F*-.75F+.75F);}
        void finish(){stop();}
    }
}
