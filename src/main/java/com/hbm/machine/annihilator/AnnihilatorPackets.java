package com.hbm.machine.annihilator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class AnnihilatorPackets {
 public record Control(BlockPos pos,String pool)implements CustomPacketPayload{static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:annihilator_control"));static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.pos);b.writeUtf(p.pool);},b->new Control(b.readBlockPos(),b.readUtf()));@Override public Type<? extends CustomPacketPayload> type(){return TYPE;}}
 public static void register(IEventBus bus){bus.addListener((RegisterPayloadHandlersEvent event)->event.registrar("1").playToServer(Control.TYPE,Control.CODEC,(p,c)->c.enqueueWork(()->{var player=c.player();if(player.level().hasChunkAt(p.pos)&&player.level().getBlockEntity(p.pos) instanceof AnnihilatorEntity machine&&machine.stillValid(player)){var tag=new CompoundTag();tag.putString("pool",p.pool);machine.receiveControl(player,tag);}})));}
}
