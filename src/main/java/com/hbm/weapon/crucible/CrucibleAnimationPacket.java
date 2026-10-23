package com.hbm.weapon.crucible;
import com.hbm.client.animation.bus.AnimationEnums.ToolAnimation;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original HbmAnimationPacket item rail0 ToolAnimation, using the existing shared bus/timer backend. No input packet. */
public record CrucibleAnimationPacket(ToolAnimation animation) implements CustomPacketPayload {
 public static Consumer<ToolAnimation> CLIENT=type->{};
 public static final Type<CrucibleAnimationPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:crucible_tool_animation"));
 public static final StreamCodec<RegistryFriendlyByteBuf,CrucibleAnimationPacket> CODEC=StreamCodec.of((out,packet)->out.writeEnum(packet.animation),in->new CrucibleAnimationPacket(in.readEnum(ToolAnimation.class)));
 @Override public Type<CrucibleAnimationPacket> type(){return TYPE;}
 public static void send(ServerPlayer player,ToolAnimation type){PacketDistributor.sendToPlayer(player,new CrucibleAnimationPacket(type));}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet.animation)));}
}
