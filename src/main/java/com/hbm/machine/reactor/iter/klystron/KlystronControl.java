package com.hbm.machine.reactor.iter.klystron;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original amount control, source20blockcenterpermission plus actual native openedmenu check. */
public record KlystronControl(BlockPos position,long amount) implements CustomPacketPayload {
    private static final Type<KlystronControl> TYPE=new Type<>(ResourceLocation.parse("hbm:klystron_control"));private static final StreamCodec<RegistryFriendlyByteBuf,KlystronControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeLong(p.amount);},b->new KlystronControl(b.readBlockPos(),b.readLong()));
    @Override public Type<KlystronControl> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof KlystronMenu menu&&menu.position.equals(packet.position)&&player.distanceToSqr(packet.position.getX()+.5,packet.position.getY()+2.5,packet.position.getZ()+.5)<400&&player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof KlystronEntity machine)machine.target(packet.amount);}));}
}
