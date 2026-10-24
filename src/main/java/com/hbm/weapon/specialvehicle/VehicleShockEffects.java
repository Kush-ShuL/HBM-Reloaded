package com.hbm.weapon.specialvehicle;

import com.hbm.bomb.nuclear.NuclearMukeEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Exact source smoke/shock packet; payload position y+0.5 and 250-block distribution origin stay distinct. */
public record VehicleShockEffects(double x,double y,double z,int count,double strength) implements CustomPacketPayload {
    public static final Type<VehicleShockEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:vehicle_shock"));
    public static final StreamCodec<RegistryFriendlyByteBuf,VehicleShockEffects> CODEC=StreamCodec.of((b,p)->{b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeInt(p.count);b.writeDouble(p.strength);},b->new VehicleShockEffects(b.readDouble(),b.readDouble(),b.readDouble(),b.readInt(),b.readDouble()));
    @Override public Type<VehicleShockEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(p,c)->c.enqueueWork(()->com.hbm.client.bomb.conventional.SmokeBurstParticles.shock(p.x,p.y,p.z,p.count,p.strength)));}
    public static void shock(Level level,double x,double y,double z,int count,double strength){if(level instanceof ServerLevel server){var packet=new VehicleShockEffects(x,y+.5,z,count,strength);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(x,y,z)<=250D*250D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
    public static void balefire(Level level,double x,double y,double z,double ox,double oy,double oz){if(level instanceof ServerLevel server){var packet=new NuclearMukeEffects(x,y,z,"bf",false);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(ox,oy,oz)<=150D*150D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
