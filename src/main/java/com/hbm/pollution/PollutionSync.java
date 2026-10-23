package com.hbm.pollution;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source PermaSync's four pollution floats, plus authoritative modern fog settings and detector notice flag. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public record PollutionSync(ResourceLocation dimension,float soot,float poison,float heavyMetal,float fallout,boolean sootFog,double threshold,double divisor,boolean detector) implements CustomPacketPayload {
    public static final Type<PollutionSync> TYPE=new Type<>(ResourceLocation.parse("hbm:pollution_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf,PollutionSync> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeResourceLocation(packet.dimension);buffer.writeFloat(packet.soot);buffer.writeFloat(packet.poison);buffer.writeFloat(packet.heavyMetal);buffer.writeFloat(packet.fallout);buffer.writeBoolean(packet.sootFog);buffer.writeDouble(packet.threshold);buffer.writeDouble(packet.divisor);buffer.writeBoolean(packet.detector);},buffer->new PollutionSync(buffer.readResourceLocation(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),buffer.readFloat(),buffer.readBoolean(),buffer.readDouble(),buffer.readDouble(),buffer.readBoolean()));
    @Override public Type<PollutionSync> type(){return TYPE;}
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->com.hbm.pollution.client.PollutionClient.receive(packet)));}
    public static void send(ServerPlayer player,boolean detector){
        float[] levels=PollutionSystem.snapshot(player.serverLevel(),player.blockPosition());
        PacketDistributor.sendToPlayer(player,new PollutionSync(player.level().dimension().location(),levels[0],levels[1],levels[2],levels[3],PollutionConfig.SOOT_FOG.get(),PollutionConfig.sootFogThreshold(),PollutionConfig.SOOT_FOG_DIVISOR.get(),detector));
    }
}
