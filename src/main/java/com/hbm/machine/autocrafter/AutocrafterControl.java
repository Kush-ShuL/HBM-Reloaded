package com.hbm.machine.autocrafter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Native equivalent of the real source IControlReceiverFilter slot/stack packet, never an item insertion. */
public record AutocrafterControl(BlockPos position,CompoundTag data) implements CustomPacketPayload {
    public static final Type<AutocrafterControl> TYPE=new Type<>(ResourceLocation.parse("hbm:autocrafter_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf,AutocrafterControl> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeNbt(p.data);},b->{var pos=b.readBlockPos();var tag=b.readNbt();return new AutocrafterControl(pos,tag==null?new CompoundTag():tag);});
    public AutocrafterControl{position=position.immutable();data=data.copy();}
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()->{var player=context.player();if(player.level().hasChunkAt(packet.position)&&player.level().getBlockEntity(packet.position) instanceof AutocrafterBlockEntity machine&&machine.hasPermission(player)&&packet.data.contains("slot"))machine.setFilterContents(packet.data);}));}
}
