package com.hbm.world.structure.templates.microwave;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record MicrowaveControl(BlockPos position,int button) implements CustomPacketPayload {
    public static final Type<MicrowaveControl> TYPE=new Type<>(ResourceLocation.parse("hbm:microwave_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,MicrowaveControl> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.position);buffer.writeByte(value.button);},buffer->new MicrowaveControl(buffer.readBlockPos(),buffer.readByte()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if((packet.button==0||packet.button==1)&&player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof MicrowaveBlockEntity microwave&&microwave.stillValid(player))microwave.button(packet.button);}));}
}
