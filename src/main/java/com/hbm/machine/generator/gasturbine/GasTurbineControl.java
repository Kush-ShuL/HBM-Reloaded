package com.hbm.machine.generator.gasturbine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Actions0=source state,1=automode,2=slider, applied only to the actual opened source core. */
public record GasTurbineControl(BlockPos position,int action,int value) implements CustomPacketPayload {
    public static final Type<GasTurbineControl> TYPE=new Type<>(ResourceLocation.parse("hbm:gas_turbine_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,GasTurbineControl> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeBlockPos(packet.position);buffer.writeInt(packet.action);buffer.writeInt(packet.value);},buffer->new GasTurbineControl(buffer.readBlockPos(),buffer.readInt(),buffer.readInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof GasTurbineMenu menu&&menu.position.equals(packet.position)&&player.level().hasChunkAt(packet.position)&&player.distanceToSqr(packet.position.getX(),packet.position.getY(),packet.position.getZ())<625&&player.level().getBlockEntity(packet.position) instanceof GasTurbineBlockEntity machine)machine.control(packet.action,packet.value);}));}
}
