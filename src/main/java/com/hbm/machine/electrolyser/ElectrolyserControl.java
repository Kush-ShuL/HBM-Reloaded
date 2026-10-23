package com.hbm.machine.electrolyser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original sgm/sgf GUI tab request, authorized against the actual open server menu. */
public record ElectrolyserControl(int containerId,boolean metal) implements CustomPacketPayload {
 public static final Type<ElectrolyserControl> TYPE=new Type<>(ResourceLocation.parse("hbm:electrolyser_control"));
 public static final StreamCodec<RegistryFriendlyByteBuf,ElectrolyserControl> CODEC=StreamCodec.of((buf,p)->{buf.writeVarInt(p.containerId);buf.writeBoolean(p.metal);},buf->new ElectrolyserControl(buf.readVarInt(),buf.readBoolean()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(p,ctx)->ctx.enqueueWork(()->{var player=ctx.player();if(player.containerMenu instanceof ElectrolyserMenu menu&&menu.containerId==p.containerId&&menu.stillValid(player)&&menu.machine()!=null){var tag=new CompoundTag();tag.putBoolean(p.metal?"sgm":"sgf",true);menu.machine().receiveControl(player,tag);}}));}
}
