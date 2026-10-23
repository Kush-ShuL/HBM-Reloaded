package com.hbm.client.woodprocessing;
import com.hbm.machine.woodprocessing.AutosawEntity;
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
/** Source block.engine,1+world.rand*.1 pitch, linear10block volume and10tick audio keepalive. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class AutosawSound {
    private static final Map<net.minecraft.world.level.block.entity.BlockEntity,Loop> SOUNDS=new IdentityHashMap<>();
    public static void tick(AutosawEntity saw){tick(saw,saw.isOn&&!saw.isSuspended,saw.muffled);}
    public static void tick(net.minecraft.world.level.block.entity.BlockEntity saw,boolean running,boolean muffled){var mc=Minecraft.getInstance();if(!running||mc.player==null||mc.player.distanceToSqr(saw.getBlockPos().getCenter())>=225){removed(saw);return;}var manager=mc.getSoundManager();var loop=SOUNDS.get(saw);if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(saw,muffled);SOUNDS.put(saw,loop);manager.play(loop);}loop.keepAlive=0;loop.muffled=muffled;loop.update();}
    public static void removed(AutosawEntity saw){removed((net.minecraft.world.level.block.entity.BlockEntity)saw);}
    public static void removed(net.minecraft.world.level.block.entity.BlockEntity saw){var loop=SOUNDS.remove(saw);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel().isClientSide()){for(var loop:SOUNDS.values()){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}SOUNDS.clear();}}
    private static final class Loop extends AbstractTickableSoundInstance {private final net.minecraft.world.level.block.entity.BlockEntity saw;private int keepAlive;private boolean muffled;Loop(net.minecraft.world.level.block.entity.BlockEntity saw,boolean muffled){super(HbmSounds.get("block.engine"),SoundSource.BLOCKS,RandomSource.create());this.saw=saw;this.muffled=muffled;looping=true;attenuation=Attenuation.NONE;var p=saw.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();pitch=1+saw.getLevel().random.nextFloat()*.1F;update();}void update(){float maximum=muffled?.1F:1;var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=Math.max(0,maximum*(1-distance/10F));}@Override public void tick(){if(saw.isRemoved()||keepAlive>10)finish();keepAlive++;update();}void finish(){stop();}}
}
