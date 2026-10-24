package com.hbm.machine.pilelegacy;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source AuxParticle vanillaExt/smoke with mY=.05,20block target radius. The existing true1.7 smoke provider remains unique. */
public record LegacyPileSmoke(double x,double y,double z) implements CustomPacketPayload {
    public static final Type<LegacyPileSmoke> TYPE=new Type<>(ResourceLocation.parse("hbm:pile_legacy_smoke"));
    public static final StreamCodec<RegistryFriendlyByteBuf,LegacyPileSmoke> CODEC=StreamCodec.of((buffer,p)->{buffer.writeDouble(p.x);buffer.writeDouble(p.y);buffer.writeDouble(p.z);},buffer->new LegacyPileSmoke(buffer.readDouble(),buffer.readDouble(),buffer.readDouble()));
    public static Consumer<LegacyPileSmoke> client=packet->{};
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->client.accept(packet)));}
    static void send(Level level,BlockPos source,double x,double y,double z){if(level instanceof ServerLevel server){var packet=new LegacyPileSmoke(x,y,z);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(source.getX()+.5,source.getY()+1,source.getZ()+.5)<400)PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}}}
}
