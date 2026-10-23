package com.hbm.machine.supercomputer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source index0/name selection with actual opened native menu and source8-block permission. */
public record SuperComputerSelection(int menuId,int index,String selection) implements CustomPacketPayload {
 private static final Type<SuperComputerSelection> TYPE=new Type<>(ResourceLocation.parse("hbm:supercomputer_selection"));private static final StreamCodec<RegistryFriendlyByteBuf,SuperComputerSelection> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.menuId);b.writeVarInt(p.index);b.writeUtf(p.selection);},b->new SuperComputerSelection(b.readVarInt(),b.readVarInt(),b.readUtf()));
 @Override public Type<SuperComputerSelection> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(packet.index==0&&player.containerMenu instanceof SuperComputerMenu menu&&menu.containerId==packet.menuId&&player.level().hasChunkAt(menu.position)&&player.level().getBlockEntity(menu.position) instanceof SuperComputerEntity torus&&torus.stillValid(player))torus.select(packet.selection);}));}
}
