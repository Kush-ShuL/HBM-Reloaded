package com.hbm.machine.generator.combustion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Real server validated ignition/slider actions; value -1 is the original ignition toggle. */
public record CombustionControl(BlockPos position,int setting) implements CustomPacketPayload {
    public static final Type<CombustionControl> TYPE=new Type<>(ResourceLocation.parse("hbm:combustion_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,CombustionControl> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.position);buffer.writeInt(value.setting);},buffer->new CombustionControl(buffer.readBlockPos(),buffer.readInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof CombustionMenu menu&&menu.position.equals(packet.position)&&player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof CombustionBlockEntity engine)engine.control(player,packet.setting);}));}
}
