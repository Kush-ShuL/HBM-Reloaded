package com.hbm.client.electricheater;
import com.hbm.machine.electricheater.ElectricHeaterBlockEntity;
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
/** Original ELECTRIC_HUM_LOOP and AudioDynamic:7.5 range,20 keepAlive, immediate idle stop.
 * The source updateVolume(getVolume(1)) replaces the initial0.25, giving steady1 or muffled0.1. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ElectricHeaterSound {
 private static final Map<ElectricHeaterBlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
 public static void tick(ElectricHeaterBlockEntity heater){if(!heater.isOn){removed(heater);return;}var manager=Minecraft.getInstance().getSoundManager();var loop=SOUNDS.get(heater);if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(heater);SOUNDS.put(heater,loop);manager.play(loop);}loop.keepAlive=0;loop.update();}
 public static void removed(ElectricHeaterBlockEntity heater){var loop=SOUNDS.remove(heater);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
 private static final class Loop extends AbstractTickableSoundInstance {private final ElectricHeaterBlockEntity heater;private int keepAlive;Loop(ElectricHeaterBlockEntity heater){super(HbmSounds.get("block.electricHum"),SoundSource.BLOCKS,RandomSource.create());this.heater=heater;looping=true;attenuation=Attenuation.NONE;var pos=heater.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}void update(){pitch=1;var player=Minecraft.getInstance().player;volume=(heater.muffled?.1F:1F)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/7.5));}@Override public void tick(){if(heater.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}}
}
