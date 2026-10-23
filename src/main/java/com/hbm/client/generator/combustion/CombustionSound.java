package com.hbm.client.generator.combustion;

import com.hbm.machine.generator.combustion.CombustionBlockEntity;
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

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CombustionSound {
    private static final Map<CombustionBlockEntity,Loop> LOOPS=new IdentityHashMap<>();
    public static void tick(CombustionBlockEntity machine){if(!machine.wasOn){removed(machine);return;}var client=Minecraft.getInstance();var loop=LOOPS.get(machine);if(loop==null||!client.getSoundManager().isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(machine);LOOPS.put(machine,loop);client.getSoundManager().play(loop);}loop.keepAlive=0;loop.update();}
    public static void removed(CombustionBlockEntity machine){var loop=LOOPS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:LOOPS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}LOOPS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance{
        private final CombustionBlockEntity machine;int keepAlive;
        Loop(CombustionBlockEntity machine){super(HbmSounds.get("block.igeneratorOperate"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();pitch=1;looping=true;attenuation=Attenuation.NONE;update();}
        void update(){var player=Minecraft.getInstance().player;volume=(machine.muffled?.1F:1)*(player==null?1:Math.max(0,1-(float)Math.sqrt(player.distanceToSqr(x,y,z))/10));}
        @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();else update();}void finish(){stop();}
    }
    private CombustionSound(){}
}
