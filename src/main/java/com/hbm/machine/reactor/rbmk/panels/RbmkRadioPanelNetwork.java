package com.hbm.machine.reactor.rbmk.panels;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class RbmkRadioPanelNetwork{
    public record Control(BlockPos pos,CompoundTag data)implements CustomPacketPayload{public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_radio_panel_control"));public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.pos);buffer.writeNbt(value.data);},buffer->new Control(buffer.readBlockPos(),buffer.readNbt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(packet.data!=null&&player.level().hasChunkAt(packet.pos)&&player.level().getBlockEntity(packet.pos) instanceof RbmkRadioPanelEntity panel&&panel.hasPermission(player))panel.receiveControl(player,packet.data);})));}
    private RbmkRadioPanelNetwork(){}
}
