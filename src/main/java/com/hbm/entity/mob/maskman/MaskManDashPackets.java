package com.hbm.entity.mob.maskman;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.player.Player;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Source DASH key state only; actual stamina/cooldown/equipped armor are always server-owned. */
public final class MaskManDashPackets {
 public record Dash(boolean pressed)implements CustomPacketPayload{public static final Type<Dash> TYPE=new Type<>(ResourceLocation.parse("hbm:armor_dash"));public static final StreamCodec<RegistryFriendlyByteBuf,Dash> CODEC=StreamCodec.of((b,p)->b.writeBoolean(p.pressed),b->new Dash(b.readBoolean()));@Override public Type<Dash> type(){return TYPE;}}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(Dash.TYPE,Dash.CODEC,(packet,context)->context.enqueueWork(()->{if(context.player() instanceof Player player)CloudDashRuntime.state(player).pressed=packet.pressed;}));}private MaskManDashPackets(){}
}
