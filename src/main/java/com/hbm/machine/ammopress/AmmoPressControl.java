package com.hbm.machine.ammopress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Authenticated native counterpart of source NBT selection control. */
public record AmmoPressControl(int containerId,int selection) implements CustomPacketPayload {
 public static final Type<AmmoPressControl> TYPE=new Type<>(AmmoPressContent.id("ammo_press_select"));public static final StreamCodec<RegistryFriendlyByteBuf,AmmoPressControl> CODEC=StreamCodec.of((buf,p)->{buf.writeVarInt(p.containerId);buf.writeInt(p.selection);},buf->new AmmoPressControl(buf.readVarInt(),buf.readInt()));
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof AmmoPressMenu menu&&menu.containerId==payload.containerId&&menu.stillValid(player)&&menu.machine instanceof AmmoPressEntity machine){var recipes=AmmoPressRecipes.recipes(player.level().getRecipeManager());if(payload.selection>= -1&&payload.selection<recipes.size())machine.select(payload.selection);}}));}
}
