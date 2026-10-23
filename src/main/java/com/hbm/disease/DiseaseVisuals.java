package com.hbm.disease;
import java.util.function.Consumer;import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import net.minecraft.server.level.ServerLevel;import net.minecraft.server.level.ServerPlayer;import net.minecraft.world.entity.LivingEntity;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.network.PacketDistributor;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source auxiliary vomit count/mode and keyed three-second PlayerInformPacket. No disease state is stored here. */
public final class DiseaseVisuals {
 public static final int NORMAL=0,BLOOD=1,SMOKE=2,ASBESTOS_WARNING=3,COAL_WARNING=4;
 public static Consumer<Effect> CLIENT=effect->{};
 public record Effect(int mode,int entity,int count)implements CustomPacketPayload {
  public static final Type<Effect> TYPE=new Type<>(ResourceLocation.parse("hbm:disease_effect"));
  public static final StreamCodec<RegistryFriendlyByteBuf,Effect> CODEC=StreamCodec.of((buffer,value)->{buffer.writeByte(value.mode);buffer.writeVarInt(value.entity);buffer.writeVarInt(value.count);},buffer->new Effect(buffer.readUnsignedByte(),buffer.readVarInt(),buffer.readVarInt()));
  @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 }
 public static void register(IEventBus bus){bus.addListener(DiseaseVisuals::payloads);}
 private static void payloads(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(Effect.TYPE,Effect.CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)));}
 public static void vomit(LivingEntity entity,int mode,int count){if(entity.level() instanceof ServerLevel level)PacketDistributor.sendToPlayersNear(level,null,entity.getX(),entity.getY(),entity.getZ(),25,new Effect(mode,entity.getId(),count));}
 public static void warning(LivingEntity entity,boolean asbestos){if(entity instanceof ServerPlayer player)PacketDistributor.sendToPlayer(player,new Effect(asbestos?ASBESTOS_WARNING:COAL_WARNING,player.getId(),0));}
 private DiseaseVisuals(){}
}
