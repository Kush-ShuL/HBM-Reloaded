package com.hbm.machine.barrel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original mode button, authorized through the actual open container. */
public record BarrelModePacket(int containerId) implements CustomPacketPayload {
    public static final Type<BarrelModePacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","barrel_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf,BarrelModePacket> CODEC=StreamCodec.of((buf,payload)->buf.writeVarInt(payload.containerId),buf->new BarrelModePacket(buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof BarrelMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player))menu.cycleMode();}));}
}
