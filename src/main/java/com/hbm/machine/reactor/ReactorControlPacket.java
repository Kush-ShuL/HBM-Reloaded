package com.hbm.machine.reactor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Opening the actual usable reactor menu authorizes its original rod target control. */
public record ReactorControlPacket(int containerId,double target) implements CustomPacketPayload {
    public static final Type<ReactorControlPacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","research_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ReactorControlPacket> CODEC=StreamCodec.of((buffer,value)->{buffer.writeVarInt(value.containerId);buffer.writeDouble(value.target);},buffer->new ReactorControlPacket(buffer.readVarInt(),buffer.readDouble()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(Double.isFinite(payload.target)&&player.containerMenu instanceof ResearchReactorMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player))menu.setTarget(payload.target);}));}
}
