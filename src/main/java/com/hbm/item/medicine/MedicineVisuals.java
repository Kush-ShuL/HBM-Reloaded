package com.hbm.item.medicine;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original auxiliary particle dispatch, without loading client classes on a dedicated server. */
public final class MedicineVisuals {
    public static Consumer<Effect> clientHandler=effect->{};
    private MedicineVisuals() {}
    static void register(IEventBus bus) {bus.addListener(MedicineVisuals::payloads);}
    private static void payloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(Effect.TYPE,Effect.CODEC,(payload,context)->context.enqueueWork(()->clientHandler.accept(payload)));
    }
    public static void bang(LivingEntity entity) {send(entity,0,250);}
    public static void vomit(LivingEntity entity) {send(entity,1,25);}
    public static void smoke(LivingEntity entity) {send(entity,2,25);}
    public static void jolt(net.minecraft.server.level.ServerPlayer player,int time,int maximum) {
        PacketDistributor.sendToPlayer(player,new Effect(3,player.getId(),time,maximum,0));
    }
    private static void send(LivingEntity entity,int kind,double range) {
        PacketDistributor.sendToPlayersNear((net.minecraft.server.level.ServerLevel)entity.level(),null,entity.getX(),entity.getY(),entity.getZ(),range,new Effect(kind,entity.getId(),entity.getX(),entity.getY(),entity.getZ()));
    }
    public record Effect(int kind,int entity,double x,double y,double z) implements CustomPacketPayload {
        public static final Type<Effect> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","medicine_particles"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Effect> CODEC=StreamCodec.of((buf,value)->{buf.writeByte(value.kind);buf.writeVarInt(value.entity);buf.writeDouble(value.x);buf.writeDouble(value.y);buf.writeDouble(value.z);},buf->new Effect(buf.readUnsignedByte(),buf.readVarInt(),buf.readDouble(),buf.readDouble(),buf.readDouble()));
        @Override public Type<? extends CustomPacketPayload> type() {return TYPE;}
    }
}
