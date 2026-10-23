package com.hbm.machine.oilflare;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source NBTControl valve/dial toggles; permission remains distance squared<=256 from actual core. */
public record FlareControl(BlockPos position,boolean valve) implements CustomPacketPayload {
    public static final Type<FlareControl> TYPE=new Type<>(ResourceLocation.parse("hbm:gas_flare_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,FlareControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeBoolean(p.valve);},b->new FlareControl(b.readBlockPos(),b.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();var pos=packet.position;if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=256&&player.level().hasChunkAt(pos)&&player.level().getBlockEntity(pos) instanceof FlareEntity flare){if(packet.valve)flare.isOn=!flare.isOn;else flare.doesBurn=!flare.doesBurn;flare.setChanged();}}));}
}
