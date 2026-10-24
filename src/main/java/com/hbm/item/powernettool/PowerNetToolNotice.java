package com.hbm.item.powernettool;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original20block TargetPoint and one type=debug/colorFFFF00/scale.5/text=id particle per real node position. */
public record PowerNetToolNotice(double x,double y,double z,String text) implements CustomPacketPayload {
    public static Consumer<PowerNetToolNotice> CLIENT = packet -> { };
    public static final Type<PowerNetToolNotice> TYPE = new Type<>(ResourceLocation.parse("hbm:power_net_tool_debug"));
    public static final StreamCodec<RegistryFriendlyByteBuf,PowerNetToolNotice> CODEC = StreamCodec.of((out,p)->{out.writeDouble(p.x);out.writeDouble(p.y);out.writeDouble(p.z);out.writeUtf(p.text);},in->new PowerNetToolNotice(in.readDouble(),in.readDouble(),in.readDouble(),in.readUtf(32)));
    @Override public Type<PowerNetToolNotice> type() { return TYPE; }
    public static void send(ServerLevel level,BlockPos pos,String text) {
        var packet = new PowerNetToolNotice(pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,text);
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginPayloadGroup(level.getServer(),packet)){for (var player : level.players()) if (player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<400) PacketDistributor.sendToPlayer(player,packet);dispatchScope.commit();}
    }
    public static void register(RegisterPayloadHandlersEvent event) { event.registrar("1").playToClient(TYPE,CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet))); }
}
