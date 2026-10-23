package com.hbm.client.airintake;
import com.hbm.machine.airintake.AirIntakeBlockEntity;
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
/** Original block.motor,volume.25,10-block linear attenuation,pitch1,20-tick keepalive and restart. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class AirIntakeSound {
    private static final Map<AirIntakeBlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(AirIntakeBlockEntity machine){if(!machine.running()){removed(machine);return;}var manager=Minecraft.getInstance().getSoundManager();var loop=SOUNDS.get(machine);if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(machine);SOUNDS.put(machine,loop);manager.play(loop);}loop.keepAlive=0;loop.update();}
    public static void removed(AirIntakeBlockEntity machine){var loop=SOUNDS.remove(machine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {private final AirIntakeBlockEntity machine;private int keepAlive;Loop(AirIntakeBlockEntity machine){super(HbmSounds.get("block.motor"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var pos=machine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();pitch=1;update();}void update(){float maximum=machine.muffled?.025F:.25F;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,maximum*(1-distance/10F));}@Override public void tick(){if(machine.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}}
    private AirIntakeSound(){}
}
