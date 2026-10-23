package com.hbm.machine.reactor.rbmk.autoloader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record RbmkAutoloaderControl(int menu,boolean plus)implements CustomPacketPayload {
    public static final Type<RbmkAutoloaderControl> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_autoloader_control"));public static final StreamCodec<RegistryFriendlyByteBuf,RbmkAutoloaderControl> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menu);b.writeBoolean(p.plus);},b->new RbmkAutoloaderControl(b.readVarInt(),b.readBoolean()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof RbmkAutoloaderMenu menu&&menu.containerId==packet.menu&&player.level().getBlockEntity(menu.position) instanceof RbmkAutoloaderEntity machine&&machine.stillValid(player))machine.control(packet.plus);})));}
}
