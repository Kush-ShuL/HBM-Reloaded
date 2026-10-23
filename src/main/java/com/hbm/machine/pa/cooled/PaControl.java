package com.hbm.machine.pa.cooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public record PaControl(int containerId,CompoundTag data)implements CustomPacketPayload{
 public static final Type<PaControl> TYPE=new Type<>(ResourceLocation.parse("hbm:pa_control"));public static final StreamCodec<RegistryFriendlyByteBuf,PaControl> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.containerId);b.writeNbt(p.data);},b->new PaControl(b.readVarInt(),b.readNbt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(packet.data!=null&&player.containerMenu instanceof PaMenu menu&&menu.containerId==packet.containerId&&menu.stillValid(player))menu.control(packet.data);}));}
}
