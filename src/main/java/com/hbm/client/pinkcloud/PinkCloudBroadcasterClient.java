package com.hbm.client.pinkcloud;

import com.hbm.machine.pinkcloud.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PinkCloudBroadcasterClient {
    private static final Map<PinkCloudBroadcasterBlockEntity,Loop> LOOPS=new IdentityHashMap<>();
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(PinkCloudBroadcasterContent.ENTITY.get(),PinkCloudBroadcasterRenderer::new);
        PinkCloudBroadcasterBlockEntity.clientTick=PinkCloudBroadcasterClient::tick;
        PinkCloudBroadcasterBlockEntity.clientRemoved=PinkCloudBroadcasterClient::remove;
    }
    private static void tick(PinkCloudBroadcasterBlockEntity entity){var manager=Minecraft.getInstance().getSoundManager();var loop=LOOPS.get(entity);if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(entity);LOOPS.put(entity,loop);manager.play(loop);}loop.timeSinceKeepAlive=0;}
    private static void remove(PinkCloudBroadcasterBlockEntity entity){var loop=LOOPS.remove(entity);if(loop!=null){loop.finish();Minecraft.getInstance().getSoundManager().stop(loop);}}
    private static final class Loop extends AbstractTickableSoundInstance {
        private final PinkCloudBroadcasterBlockEntity entity;private int timeSinceKeepAlive;
        Loop(PinkCloudBroadcasterBlockEntity entity){
            super(HbmSounds.get("block.broadcast"+(new Random(entity.getBlockPos().getX()+entity.getBlockPos().getY()+entity.getBlockPos().getZ()).nextInt(3)+1)),SoundSource.BLOCKS,RandomSource.create());
            this.entity=entity;var pos=entity.getBlockPos();x=pos.getX();y=pos.getY();z=pos.getZ();looping=true;attenuation=Attenuation.NONE;pitch=1;volume=25;
        }
        @Override public void tick(){
            if(entity.isRemoved()||entity.getLevel()!=Minecraft.getInstance().level||timeSinceKeepAlive>20){finish();return;}
            timeSinceKeepAlive++;
            var player=Minecraft.getInstance().player;
            volume=player==null?25:Math.max(0,(float)(player.distanceToSqr(x,y,z)>0?25-Math.sqrt(player.distanceToSqr(x,y,z)):25));
        }
        void finish(){stop();}
    }
}
