package com.hbm.machine.reactor.watz;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Source lockcontrol with actual opened-menu/core/distance checks at the server boundary. */
public record WatzControl(BlockPos position) implements CustomPacketPayload {
    private static final Type<WatzControl> TYPE=new Type<>(ResourceLocation.parse("hbm:watz_control"));
    private static final StreamCodec<RegistryFriendlyByteBuf,WatzControl> CODEC=StreamCodec.of((b,p)->b.writeBlockPos(p.position),b->new WatzControl(b.readBlockPos()));
    @Override public Type<WatzControl> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof WatzMenu menu&&menu.position.equals(packet.position)&&player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof WatzEntity machine&&machine.stillValid(player))machine.toggleLock();}));}
}
