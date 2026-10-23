package com.hbm.item.structurewand;

import com.hbm.world.structure.StructureAuthoringFiles;
import com.hbm.world.structure.StructureAuthoringJobs;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** The source has no container. A one-use real-interaction token authenticates the standalone GUI's close packet. */
public final class StructureWandPackets {
    public static Consumer<Open> CLIENT_OPEN=packet->{};
    private record Session(UUID token,ResourceLocation dimension,BlockPos pos){}
    private static final Map<ServerPlayer,Session> SESSIONS=new WeakHashMap<>();
    public record Open(BlockPos position,UUID token,boolean loading,CompoundTag settings,List<String> files) implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(ResourceLocation.parse("hbm:structure_authoring_open"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Open> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeUUID(p.token);b.writeBoolean(p.loading);b.writeNbt(p.settings);b.writeVarInt(p.files.size());for(String file:p.files)b.writeUtf(file);},b->{var pos=b.readBlockPos();var token=b.readUUID();boolean loading=b.readBoolean();var tag=read(b);int count=b.readVarInt();if(count<0)throw new IllegalArgumentException("Negative structure file count");var files=new ArrayList<String>();for(int i=0;i<count;i++)files.add(b.readUtf());return new Open(pos,token,loading,tag,List.copyOf(files));});
        public Open{position=position.immutable();settings=settings.copy();files=List.copyOf(files);}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Control(BlockPos position,UUID token,CompoundTag settings,boolean save,boolean load) implements CustomPacketPayload {
        public static final Type<Control> TYPE=new Type<>(ResourceLocation.parse("hbm:structure_authoring_control"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Control> CODEC=StreamCodec.of((b,p)->{b.writeBlockPos(p.position);b.writeUUID(p.token);b.writeNbt(p.settings);b.writeBoolean(p.save);b.writeBoolean(p.load);},b->new Control(b.readBlockPos(),b.readUUID(),read(b),b.readBoolean(),b.readBoolean()));
        public Control{position=position.immutable();settings=settings.copy();}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    private static CompoundTag read(RegistryFriendlyByteBuf buffer){var tag=buffer.readNbt();if(tag==null)throw new IllegalArgumentException("Missing structure settings");return tag;}
    public static void open(ServerLevel level,Player player,StructureAuthoringEntity tile){if(player instanceof ServerPlayer actor){var session=new Session(UUID.randomUUID(),level.dimension().location(),tile.getBlockPos().immutable());SESSIONS.put(actor,session);PacketDistributor.sendToPlayer(actor,new Open(session.pos,session.token,((StructureAuthoringBlock)tile.getBlockState().getBlock()).loading,tile.settings(),StructureAuthoringFiles.names()));}}
    public static void register(RegisterPayloadHandlersEvent event){var registrar=event.registrar("1");registrar.playToClient(Open.TYPE,Open.CODEC,(packet,context)->context.enqueueWork(()->CLIENT_OPEN.accept(packet)));registrar.playToServer(Control.TYPE,Control.CODEC,(packet,context)->context.enqueueWork(()->{
        if(!(context.player() instanceof ServerPlayer actor))return;var session=SESSIONS.get(actor);if(session==null||!session.token.equals(packet.token)||!session.dimension.equals(actor.serverLevel().dimension().location())||!session.pos.equals(packet.position)||actor.distanceToSqr(packet.position.getCenter())>64)return;
        if(actor.serverLevel().getChunkSource().getChunkNow(packet.position.getX()>>4,packet.position.getZ()>>4)==null)return;
        if(!(actor.serverLevel().getBlockEntity(packet.position) instanceof StructureAuthoringEntity tile))return;SESSIONS.remove(actor);tile.readSettings(packet.settings);tile.setChanged();
        if(packet.save)StructureAuthoringJobs.save(actor.serverLevel(),actor.getUUID(),tile);
        if(packet.load)StructureAuthoringJobs.load(actor.serverLevel(),actor.getUUID(),tile);
    }));}
    private StructureWandPackets(){}
}
