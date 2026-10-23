package com.hbm.client.reactor.iter.torus;
import com.hbm.machine.reactor.iter.torus.TorusEntity;
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
/** Original FEL loop: speed/30 pitch+volume,sourcecenter/range15 and30distance activation,20tickkeepalive. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class TorusSound {
    private static final Map<TorusEntity,Loop> LOOPS=new IdentityHashMap<>();
    public static void tick(TorusEntity machine){var player=Minecraft.getInstance().player;var pos=machine.getBlockPos();if(machine.magnetSpeed<=0||player==null||player.distanceToSqr(pos.getX()+.5,pos.getY()+2.5,pos.getZ()+.5)>=2500){removed(machine);return;}var client=Minecraft.getInstance();var loop=LOOPS.get(machine);if(loop==null||!client.getSoundManager().isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(machine);LOOPS.put(machine,loop);client.getSoundManager().play(loop);}else loop.pitchFromMachine();loop.keepAlive=0;loop.update();}
    public static void removed(TorusEntity machine){var loop=LOOPS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:LOOPS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}LOOPS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {
        final TorusEntity machine;int keepAlive;
        Loop(TorusEntity machine){super(HbmSounds.get("block.fusionReactorRunning"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;var pos=machine.getBlockPos();x=pos.getX()+.5;y=pos.getY()+2.5;z=pos.getZ()+.5;looping=true;attenuation=Attenuation.NONE;pitch=1;update();}
        void pitchFromMachine(){pitch=machine.magnetSpeed/30;}
        void update(){float speed=machine.magnetSpeed/30;var player=Minecraft.getInstance().player;volume=(machine.muffled?.1F:1)*speed*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/30));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();else update();}void finish(){stop();}
    }
    private TorusSound(){}
}
