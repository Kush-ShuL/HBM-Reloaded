package com.hbm.client.reactor.iter.mhdt;
import com.hbm.machine.reactor.iter.mhdt.MhdtEntity;
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
/** Original large turbine loop: speed/15 pitch+volume, Y1.5 origin/range20 and Y2.5 30distance activation,20tickkeepalive. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MhdtSound {
    private static final Map<MhdtEntity,Loop> LOOPS=new IdentityHashMap<>();
    public static void tick(MhdtEntity machine){var player=Minecraft.getInstance().player;var pos=machine.getBlockPos();if(machine.rotorSpeed<=0||player==null||player.distanceToSqr(pos.getX()+.5,pos.getY()+2.5,pos.getZ()+.5)>=900){removed(machine);return;}var client=Minecraft.getInstance();var loop=LOOPS.get(machine);if(loop==null||!client.getSoundManager().isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(machine);LOOPS.put(machine,loop);client.getSoundManager().play(loop);}else loop.pitchFromMachine();loop.keepAlive=0;loop.update();}
    public static void removed(MhdtEntity machine){var loop=LOOPS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:LOOPS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}LOOPS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {
        final MhdtEntity machine;int keepAlive;
        Loop(MhdtEntity machine){super(HbmSounds.get("block.largeTurbineRunning"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;var pos=machine.getBlockPos();x=pos.getX()+.5;y=pos.getY()+1.5;z=pos.getZ()+.5;looping=true;attenuation=Attenuation.NONE;pitch=machine.rotorSpeed/15;update();}
        void pitchFromMachine(){pitch=machine.rotorSpeed/15;}
        void update(){float speed=machine.rotorSpeed/15;var player=Minecraft.getInstance().player;volume=(machine.muffled?.1F:1)*speed*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/20));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();else update();}void finish(){stop();}
    }
    private MhdtSound(){}
}
