package com.hbm.client.weapon.stinger;
import java.util.*;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.stinger.ItemGunStinger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Actual ORCHESTRA_STINGER client branch: source range15, pitch1, keepalive10. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class StingerSound {
    private static final Map<LivingEntity,Loop> LOOPS=new IdentityHashMap<>();
    private StingerSound(){}
    public static void orchestra(ItemStack stack,LambdaContext context){update(stack,context.entity);}
    private static void update(ItemStack stack,LivingEntity entity){
        var manager=Minecraft.getInstance().getSoundManager();var running=LOOPS.get(entity);
        if(ItemGunStinger.getLockonProgress(stack)>0&&!ItemGunBaseNT.getIsLockedOn(stack)){
            if(running==null||!manager.isActive(running)){
                if(running!=null)running.finish();var loop=new Loop(entity);LOOPS.put(entity,loop);manager.play(loop);
            }
            if(running!=null&&manager.isActive(running))running.keepAlive();
        }else if(running!=null&&manager.isActive(running)){running.finish();manager.stop(running);}
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();
        if(mc.level==null){LOOPS.values().forEach(Loop::finish);LOOPS.clear();return;}
        for(var player:mc.level.players())if(player!=mc.player&&player.getMainHandItem().getItem() instanceof ItemGunStinger)update(player.getMainHandItem(),player);
        LOOPS.entrySet().removeIf(entry->entry.getValue().isStopped());
    }
    private static final class Loop extends AbstractTickableSoundInstance {
        private final LivingEntity owner;private int sinceKeepAlive;
        Loop(LivingEntity owner){super(HbmSounds.get("hbm:weapon.fire.lockon"),SoundSource.PLAYERS,RandomSource.create());this.owner=owner;looping=true;attenuation=Attenuation.NONE;pitch=1;keepAlive();}
        void keepAlive(){sinceKeepAlive=0;x=owner.getX();y=owner.getY();z=owner.getZ();updateVolume();}
        private void updateVolume(){var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=distance/15F*-1F+1F;}
        @Override public void tick(){if(owner.isRemoved()||!owner.isAlive()||sinceKeepAlive>10)finish();sinceKeepAlive++;x=owner.getX();y=owner.getY();z=owner.getZ();updateVolume();}
        void finish(){stop();}
    }
}
