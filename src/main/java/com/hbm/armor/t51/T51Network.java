package com.hbm.armor.t51;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Actual shared source enableHUD field, persistent on player and server-authoritative ToggleHead key edge. */
public final class T51Network {
 public static Consumer<Boolean> CLIENT_HUD=value->{};
 public record Toggle() implements CustomPacketPayload {
  public static final Type<Toggle> TYPE=new Type<>(T51Content.id("fsb_toggle_hud"));
  public static final StreamCodec<RegistryFriendlyByteBuf,Toggle>CODEC=StreamCodec.unit(new Toggle());
  @Override public Type<Toggle>type(){return TYPE;}
 }
 public record State(boolean enabled) implements CustomPacketPayload {
  public static final Type<State>TYPE=new Type<>(T51Content.id("fsb_hud_state"));
  public static final StreamCodec<RegistryFriendlyByteBuf,State>CODEC=StreamCodec.of((out,state)->out.writeBoolean(state.enabled()),in->new State(in.readBoolean()));
  @Override public Type<State>type(){return TYPE;}
 }
 public static boolean enabled(Player player){var data=player.getPersistentData();return !data.contains("enableHUD")||data.getBoolean("enableHUD");}
 public static void sync(ServerPlayer player){PacketDistributor.sendToPlayer(player,new State(enabled(player)));}
 public static void register(IEventBus bus){bus.addListener(T51Network::payloads);}
 private static void payloads(RegisterPayloadHandlersEvent event){var registrar=event.registrar("1");registrar.playToServer(Toggle.TYPE,Toggle.CODEC,(packet,context)->context.enqueueWork(()->{
  if(!(context.player()instanceof ServerPlayer player)||!player.isAlive())return;boolean enabled=!enabled(player);player.getPersistentData().putBoolean("enableHUD",enabled);sync(player);
  player.displayClientMessage(net.minecraft.network.chat.Component.translatable(enabled?"message.hbm.armor.hud_on":"message.hbm.armor.hud_off").withStyle(enabled?net.minecraft.ChatFormatting.GREEN:net.minecraft.ChatFormatting.RED),true);
 }));registrar.playToClient(State.TYPE,State.CODEC,(packet,context)->context.enqueueWork(()->CLIENT_HUD.accept(packet.enabled())));}
 private T51Network(){}
}
