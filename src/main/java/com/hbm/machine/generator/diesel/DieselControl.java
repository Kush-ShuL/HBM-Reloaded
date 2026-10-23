package com.hbm.machine.generator.diesel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original receiveControl(turnOn) toggles; permission is source distance<25 from the actual machine. */
public record DieselControl(BlockPos position) implements CustomPacketPayload {
    public static final Type<DieselControl> TYPE=new Type<>(ResourceLocation.parse("hbm:diesel_toggle"));public static final StreamCodec<RegistryFriendlyByteBuf,DieselControl> CODEC=StreamCodec.of((buffer,packet)->buffer.writeBlockPos(packet.position),buffer->new DieselControl(buffer.readBlockPos()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<625&&player.level().hasChunkAt(pos)&&player.level().getBlockEntity(pos) instanceof DieselBlockEntity engine)engine.toggle();}));}
}
