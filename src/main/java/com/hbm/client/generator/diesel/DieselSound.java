package com.hbm.client.generator.diesel;
import com.hbm.machine.generator.diesel.DieselBlockEntity;
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
public final class DieselSound {
    private static final Map<DieselBlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(DieselBlockEntity engine){if(!engine.wasOn){removed(engine);return;}var manager=Minecraft.getInstance().getSoundManager();var loop=SOUNDS.get(engine);if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(engine);SOUNDS.put(engine,loop);manager.play(loop);}loop.keepAlive=0;loop.update();}
    public static void removed(DieselBlockEntity engine){var loop=SOUNDS.remove(engine);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {private final DieselBlockEntity engine;private int keepAlive;Loop(DieselBlockEntity engine){super(HbmSounds.get("block.engine"),SoundSource.BLOCKS,RandomSource.create());this.engine=engine;looping=true;attenuation=Attenuation.NONE;var pos=engine.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();update();}void update(){pitch=1;float maximum=engine.muffled?.1F:1F;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,distance/10F*-maximum+maximum);}@Override public void tick(){if(engine.isRemoved()||keepAlive>10)finish();keepAlive++;update();}void finish(){stop();}}
}
