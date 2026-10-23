package com.hbm.machine.foundry.crucible;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source recipe control only while the player actually owns this open crucible container. */
public record CrucibleSelection(int containerId,String selection)implements CustomPacketPayload {
 public static final Type<CrucibleSelection> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","crucible_selection"));public static final StreamCodec<RegistryFriendlyByteBuf,CrucibleSelection> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeUtf(p.selection);},b->new CrucibleSelection(b.readVarInt(),b.readUtf()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ThermalMenu menu&&!menu.firebox&&menu.containerId==packet.containerId&&menu.stillValid(player))menu.selectRecipe(packet.selection);}));}
}
