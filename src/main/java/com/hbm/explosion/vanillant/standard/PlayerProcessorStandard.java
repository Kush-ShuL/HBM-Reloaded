package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.ExplosionKnockback;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IPlayerProcessor;
import java.util.HashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
public class PlayerProcessorStandard implements IPlayerProcessor {
 @Override public void process(ExplosionVNT explosion,Level world,double x,double y,double z,HashMap<Player,Vec3> affectedPlayers){for(var entry:affectedPlayers.entrySet())if(entry.getKey() instanceof ServerPlayer player){var impulse=entry.getValue();PacketDistributor.sendToPlayer(player,new ExplosionKnockback((float)impulse.x,(float)impulse.y,(float)impulse.z));}}
}
