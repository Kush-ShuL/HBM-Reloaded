package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerBlockEntity;
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
/** Source boiler loop,10range,20audioTime and keepAlive. Source updateVolume(1) replaces initial0.125. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HeatBoilerSound {
 private static final Map<HeatBoilerBlockEntity,State> STATES=new IdentityHashMap<>();
 public static void tick(HeatBoilerBlockEntity boiler){STATES.computeIfAbsent(boiler,ignored->new State()).update(boiler);}
 public static void removed(HeatBoilerBlockEntity boiler){var state=STATES.remove(boiler);if(state!=null)state.stop();}
 @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){STATES.values().forEach(State::stop);STATES.clear();}}
 private static final class State {private int audioTime;private Loop sound;void update(HeatBoilerBlockEntity boiler){if(boiler.isOn)audioTime=20;if(audioTime>0){audioTime--;var manager=Minecraft.getInstance().getSoundManager();if(sound==null||!manager.isActive(sound)){if(sound!=null)sound.finish();sound=new Loop(boiler);manager.play(sound);}sound.keepAlive=0;sound.update();}else stop();}void stop(){if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);sound=null;}}}
 private static final class Loop extends AbstractTickableSoundInstance{private final HeatBoilerBlockEntity boiler;private int keepAlive;Loop(HeatBoilerBlockEntity boiler){super(HbmSounds.get("block.boiler"),SoundSource.BLOCKS,RandomSource.create());this.boiler=boiler;looping=true;attenuation=Attenuation.NONE;var pos=boiler.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}void update(){pitch=1;var player=Minecraft.getInstance().player;float maximum=boiler.muffled?.1F:1F,distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=distance/10F*-maximum+maximum;}@Override public void tick(){if(boiler.isRemoved()||keepAlive>20)finish();keepAlive++;update();}void finish(){stop();}}
}
