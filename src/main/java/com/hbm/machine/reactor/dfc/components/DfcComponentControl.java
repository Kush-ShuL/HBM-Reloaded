package com.hbm.machine.reactor.dfc.components;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original emitter/stabilizer AuxButton actions, constrained to the real open native menu. */
public record DfcComponentControl(BlockPos position,int value,int button) implements CustomPacketPayload {
 public static final Type<DfcComponentControl> TYPE=new Type<>(ResourceLocation.parse("hbm:dfc_component_control"));public static final StreamCodec<RegistryFriendlyByteBuf,DfcComponentControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeInt(p.value);b.writeInt(p.button);},b->new DfcComponentControl(b.readBlockPos(),b.readInt(),b.readInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.containerMenu instanceof DfcComponentMenu menu&&menu.position.equals(p.position)&&menu.stillValid(player)&&player.level().hasChunkAt(p.position)&&player.level().getBlockEntity(p.position) instanceof DfcComponentEntity component)component.control(p.value,p.button);}));}
}
