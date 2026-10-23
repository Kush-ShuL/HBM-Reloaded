package com.hbm.machine.excavator;
import net.minecraft.core.BlockPos;import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Original NBTControl five toggles, authenticated to this actual open menu and original12-block range. */
public record ExcavatorControl(BlockPos pos,String key) implements CustomPacketPayload {
 public static final Type<ExcavatorControl> TYPE=new Type<>(ExcavatorContent.id("excavator_control"));public static final StreamCodec<RegistryFriendlyByteBuf,ExcavatorControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeUtf(p.key,16);},b->new ExcavatorControl(b.readBlockPos(),b.readUtf(16)));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
 static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(payload,context)->context.enqueueWork(()->{var player=context.player();if(player.containerMenu instanceof ExcavatorMenu menu&&menu.position().equals(payload.pos)&&player.level().hasChunkAt(payload.pos)&&player.level().getBlockEntity(payload.pos) instanceof ExcavatorBlockEntity machine&&machine.stillValid(player))machine.toggle(payload.key);}));}
}
