package com.hbm.machine.reactor.rbmk.boiler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record RbmkBoilerControl(int menu)implements CustomPacketPayload {
    public static final Type<RbmkBoilerControl> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_boiler_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RbmkBoilerControl> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.menu),b->new RbmkBoilerControl(b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{if(context.player() instanceof ServerPlayer player&&player.containerMenu instanceof RbmkBoilerMenu menu&&menu.containerId==packet.menu&&player.serverLevel().hasChunkAt(menu.position)&&player.serverLevel().getBlockEntity(menu.position) instanceof RbmkBoilerEntity boiler&&boiler.hasPermission(player))boiler.cycleCompressor();})));}
}
