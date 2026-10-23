package com.hbm.world.oldlantern;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source networkPackNT(250): one damaged flag at the original 250-block radius. */
public record OldLanternPacket(BlockPos pos,boolean broken) implements CustomPacketPayload {
    public static final Type<OldLanternPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:old_lantern_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf,OldLanternPacket> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeBlockPos(packet.pos);buffer.writeBoolean(packet.broken);},buffer->new OldLanternPacket(buffer.readBlockPos(),buffer.readBoolean()));
    public static Consumer<OldLanternPacket> CLIENT=packet->{};
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)));}
}
