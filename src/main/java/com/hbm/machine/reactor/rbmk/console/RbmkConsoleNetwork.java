package com.hbm.machine.reactor.rbmk.console;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class RbmkConsoleNetwork {
    public record Update(BlockPos pos,CompoundTag data)implements CustomPacketPayload{public static final Type<Update> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_console_sync"));public static final StreamCodec<RegistryFriendlyByteBuf,Update> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeNbt(p.data);},b->new Update(b.readBlockPos(),b.readNbt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}}
    public record Control(BlockPos pos,CompoundTag data)implements CustomPacketPayload{public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_console_control"));public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeNbt(p.data);},b->new Control(b.readBlockPos(),b.readNbt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->{var registrar=event.registrar("1");registrar.playToClient(Update.TYPE,Update.CODEC,(packet,context)->context.enqueueWork(()->com.hbm.client.reactor.rbmk.console.RbmkConsoleClient.update(packet)));registrar.playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(packet.data!=null&&player.level().hasChunkAt(packet.pos)&&player.level().getBlockEntity(packet.pos) instanceof RbmkConsoleEntity console&&console.hasPermission(player))console.receiveControl(packet.data);}));});}
    public static void sync(ServerLevel level,RbmkConsoleEntity console,boolean full){var packet=new Update(console.getBlockPos(),console.syncData(full));for(var player:level.players())if(player.distanceToSqr(console.getBlockPos().getX()+.5,console.getBlockPos().getY()+.5,console.getBlockPos().getZ()+.5)<2500)PacketDistributor.sendToPlayer(player,packet);}
    private RbmkConsoleNetwork(){}
}
