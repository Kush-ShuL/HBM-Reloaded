package com.hbm.world.structure.templates.funnel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source NBT control accepts any toggle data only while the actual machine is usable (12 blocks). */
public record FunnelControl(BlockPos position) implements CustomPacketPayload {
    public static final Type<FunnelControl> TYPE=new Type<>(ResourceLocation.parse("hbm:funnel_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,FunnelControl> CODEC=StreamCodec.of((buffer,value)->buffer.writeBlockPos(value.position),buffer->new FunnelControl(buffer.readBlockPos()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof FunnelBlockEntity machine&&machine.stillValid(player))machine.cycleMode();}));}
}
