package com.hbm.machine.reactor.iter.torus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source index0/name selection with actual opened native menu and source32-block permission. */
public record TorusSelection(int menuId,int index,String selection) implements CustomPacketPayload {
 private static final Type<TorusSelection> TYPE=new Type<>(ResourceLocation.parse("hbm:torus_selection"));private static final StreamCodec<RegistryFriendlyByteBuf,TorusSelection> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menuId);b.writeVarInt(p.index);b.writeUtf(p.selection);},b->new TorusSelection(b.readVarInt(),b.readVarInt(),b.readUtf()));
 @Override public Type<TorusSelection> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(packet.index==0&&player.containerMenu instanceof TorusMenu menu&&menu.containerId==packet.menuId&&player.level().hasChunkAt(menu.position)&&player.level().getBlockEntity(menu.position) instanceof TorusEntity torus&&torus.stillValid(player))torus.select(packet.selection);}));}
}
