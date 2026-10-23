package com.hbm.machine.reactor.rbmk.control;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source GUI controls, bound to the actual open menu and original strict20m permission. */
public record RbmkControlPacket(int menu,int action,int selection,double a,double b,double c,double d)implements CustomPacketPayload {
    public static final Type<RbmkControlPacket> TYPE=new Type<>(ResourceLocation.parse("hbm:rbmk_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,RbmkControlPacket> CODEC=StreamCodec.of((buf,p)->{buf.writeVarInt(p.menu);buf.writeByte(p.action);buf.writeByte(p.selection);buf.writeDouble(p.a);buf.writeDouble(p.b);buf.writeDouble(p.c);buf.writeDouble(p.d);},buf->new RbmkControlPacket(buf.readVarInt(),buf.readUnsignedByte(),buf.readUnsignedByte(),buf.readDouble(),buf.readDouble(),buf.readDouble(),buf.readDouble()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(TYPE,CODEC,(p,context)->context.enqueueWork(()->{if(!(context.player() instanceof ServerPlayer player)||!(player.containerMenu instanceof RbmkControlMenu menu)||menu.containerId!=p.menu||!player.serverLevel().hasChunkAt(menu.position)||!(player.serverLevel().getBlockEntity(menu.position) instanceof RbmkControlEntity control)||!control.hasPermission(player)||!Double.isFinite(p.a)||!Double.isFinite(p.b)||!Double.isFinite(p.c)||!Double.isFinite(p.d))return;var data=new CompoundTag();switch(p.action){case 0->{if(control.automatic()||p.a<0||p.a>1)return;data.putDouble("level",p.a);}case 1->{if(control.automatic()||p.selection>4)return;data.putInt("color",p.selection);}case 2->{if(!control.automatic()||p.selection>2)return;data.putInt("function",p.selection);}case 3->{if(!control.automatic()||p.a<0||p.a>100||p.b<0||p.b>100||p.c<0||p.c>9999||p.d<0||p.d>9999)return;data.putDouble("levelUpper",p.a);data.putDouble("levelLower",p.b);data.putDouble("heatUpper",p.c);data.putDouble("heatLower",p.d);}default->{return;}}control.receiveControl(player,data);})));}
}
