package com.hbm.machine.forcefield;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source toggle only, authenticated against actual open menu and its original8-block usability range. */
public record ForceFieldControl(int containerId) implements CustomPacketPayload {
 public static final Type<ForceFieldControl> TYPE=new Type<>(ForceFieldContent.id("forcefield_control"));public static final StreamCodec<RegistryFriendlyByteBuf,ForceFieldControl> CODEC=StreamCodec.of((buffer,payload)->buffer.writeVarInt(payload.containerId),buffer->new ForceFieldControl(buffer.readVarInt()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ForceFieldMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player))menu.field.toggle();}));}
}
