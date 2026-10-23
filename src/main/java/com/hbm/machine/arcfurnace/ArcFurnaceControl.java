package com.hbm.machine.arcfurnace;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original liquid-mode button, authorized by the real open source machine inventory. */
public record ArcFurnaceControl(int container) implements CustomPacketPayload {
 public static final Type<ArcFurnaceControl> TYPE=new Type<>(ResourceLocation.parse("hbm:arc_furnace_control"));public static final StreamCodec<RegistryFriendlyByteBuf,ArcFurnaceControl> CODEC=StreamCodec.of((b,p)->b.writeVarInt(p.container),b->new ArcFurnaceControl(b.readVarInt()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(value,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ArcFurnaceMenu menu&&menu.containerId==value.container&&menu.stillValid(player))menu.machine.toggleMode();}));}
}
