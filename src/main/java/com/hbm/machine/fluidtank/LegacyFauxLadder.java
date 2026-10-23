package com.hbm.machine.fluidtank;

import com.hbm.machine.fluidtank.mixin.FauxLadderConnectionAccess;
import java.util.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original HbmPlayerProps.isOnLadder and EntityEffectHandler.handleFauxLadder, consumed once per tick. */
@EventBusSubscriber(modid="hbm")
public final class LegacyFauxLadder {
    // Entity equality uses numeric id; the integrated server and client share that id.
    // Source player props were per-world instances, so isolate the pending flag by world too.
    private static final Map<Level,Map<Player,Boolean>> ACTIVE=new WeakHashMap<>();
    public static synchronized void mark(Player player){ACTIVE.computeIfAbsent(player.level(),ignored->new WeakHashMap<>()).put(player,true);}
    private static synchronized boolean consume(Player player){var players=ACTIVE.get(player.level());return players!=null&&players.remove(player)!=null;}
    @SubscribeEvent public static void tick(PlayerTickEvent.Pre event){
        var player=event.getEntity();if(!consume(player))return;var motion=player.getDeltaMovement();double x=Math.clamp(motion.x,-.15,.15),y=Math.max(motion.y,-.15),z=Math.clamp(motion.z,-.15,.15);player.fallDistance=0;if(player.isShiftKeyDown()&&y<0)y=0;if(player.horizontalCollision)y=.2;player.setDeltaMovement(new Vec3(x,y,z));if(player instanceof ServerPlayer server)((FauxLadderConnectionAccess)server.connection).hbm$resetFlightTime(0);
    }
    private LegacyFauxLadder(){}
}
