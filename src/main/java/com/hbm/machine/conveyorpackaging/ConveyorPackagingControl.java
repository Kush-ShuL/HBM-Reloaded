package com.hbm.machine.conveyorpackaging;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** The actual source toggle action; same strict <20 origin distance permission, server-owned byte mode. */
public record ConveyorPackagingControl(BlockPos position) implements CustomPacketPayload {
    public static final Type<ConveyorPackagingControl> TYPE=new Type<>(ResourceLocation.parse("hbm:crane_boxer_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,ConveyorPackagingControl> CODEC=StreamCodec.of((buffer,value)->buffer.writeBlockPos(value.position),buffer->new ConveyorPackagingControl(buffer.readBlockPos()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(message,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(message.position)&&player.level().getBlockEntity(message.position)instanceof ConveyorPackagingEntity crane&&!crane.unboxer&&crane.hasPermission(player))crane.toggle();}));}
}
