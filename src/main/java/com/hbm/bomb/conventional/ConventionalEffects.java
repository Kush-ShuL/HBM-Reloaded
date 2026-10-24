package com.hbm.bomb.conventional;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** ExplosionSmallCreator's server composition and original 200-block recipient radius. */
public record ConventionalEffects(double x,double y,double z,int clouds,float scale,float speed) implements CustomPacketPayload {
    public static final Type<ConventionalEffects> TYPE=new Type<>(ResourceLocation.parse("hbm:conventional_small_explosion"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ConventionalEffects> CODEC=StreamCodec.of((buffer,p)->{buffer.writeDouble(p.x);buffer.writeDouble(p.y);buffer.writeDouble(p.z);buffer.writeInt(p.clouds);buffer.writeFloat(p.scale);buffer.writeFloat(p.speed);},buffer->new ConventionalEffects(buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readInt(),buffer.readFloat(),buffer.readFloat()));
    @Override public Type<ConventionalEffects> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(value,context)->context.enqueueWork(()->com.hbm.client.bomb.conventional.SmallExplosionEffects.receive(value)));}
    public static void compose(Level level,double x,double y,double z,int count,float scale,float speed){
        if(!(level instanceof ServerLevel server))return;
        var packet=new ConventionalEffects(x,y,z,count,scale,speed);
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(x,y,z)<=200D*200D)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}
    }
}
