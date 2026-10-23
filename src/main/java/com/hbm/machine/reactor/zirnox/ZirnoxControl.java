package com.hbm.machine.reactor.zirnox;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** The original control/vent button semantics, authorized by the actual open reactor menu. */
public record ZirnoxControl(int menuId,boolean control,boolean vent) implements CustomPacketPayload {
    public static final Type<ZirnoxControl> TYPE=new Type<>(ResourceLocation.parse("hbm:zirnox_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ZirnoxControl> CODEC=StreamCodec.of((buffer,value)->{buffer.writeVarInt(value.menuId);buffer.writeBoolean(value.control);buffer.writeBoolean(value.vent);},buffer->new ZirnoxControl(buffer.readVarInt(),buffer.readBoolean(),buffer.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(value,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ZirnoxMenu menu&&menu.containerId==value.menuId&&menu.stillValid(player))menu.control(value.control,value.vent);}));}
}
