package com.hbm.machine.launchpadsoyuz;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Actual IControlReceiver GUI-only cargo/launch protocol; no invented ROR/OC/CC interface. */
public record LaunchpadSoyuzControl(BlockPos pos,CompoundTag data) implements CustomPacketPayload {
    public static final Type<LaunchpadSoyuzControl> TYPE=new Type<>(LaunchpadSoyuzContent.id("launchpad_soyuz_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,LaunchpadSoyuzControl> CODEC=StreamCodec.of((buffer,packet)->{buffer.writeBlockPos(packet.pos);buffer.writeNbt(packet.data);},buffer->new LaunchpadSoyuzControl(buffer.readBlockPos(),buffer.readNbt()));
    public LaunchpadSoyuzControl{pos=pos.immutable();data=data==null?new CompoundTag():data.copy();}
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{if(context.player().level() instanceof ServerLevel server&&server.hasChunkAt(packet.pos)&&server.getBlockEntity(packet.pos) instanceof LaunchpadSoyuzEntity machine)machine.receiveControl(context.player(),packet.data);}));}
}
