package com.hbm.machine.conveyorrouter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Original six NBTControlPacket toggle actions, bounded and checked with the source <20-block permission. */
public record CraneRouterControl(BlockPos position,int side) implements CustomPacketPayload {
    public static final Type<CraneRouterControl> TYPE=new Type<>(ResourceLocation.parse("hbm:crane_router_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf,CraneRouterControl> CODEC=StreamCodec.of((buffer,value)->{buffer.writeBlockPos(value.position);buffer.writeVarInt(value.side);},buffer->new CraneRouterControl(buffer.readBlockPos(),buffer.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToServer(TYPE,CODEC,(message,context)->context.enqueueWork(()->{var player=context.player();if(message.side>=0&&message.side<6&&player.level().hasChunkAt(message.position)&&player.level().getBlockEntity(message.position)instanceof CraneRouterEntity router){var data=new CompoundTag();data.putInt("toggle",message.side);router.receiveControl(player,data);}}));}
}
