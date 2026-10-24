package com.hbm.machine.annihilator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Server-owned snapshots of the one real static milestone table; no recipe manager is added. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD)
public final class AnnihilatorRecipeSync {
    public record Row(AnnihilatorRecipes.Key key,List<AnnihilatorRecipes.Milestone> milestones) {}
    private record Pending(MinecraftServer server,UUID player,int deadline,CompletableFuture<Void> future) {}
    private static final Map<UUID,Pending> PENDING=new HashMap<>();
    public static Consumer<Snapshot> CLIENT=packet->{};

    public record Snapshot(UUID request,List<Row> rows) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE=new Type<>(ResourceLocation.parse("hbm:annihilator_recipe_snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Snapshot> CODEC=StreamCodec.of((out,packet)->{
            out.writeUUID(packet.request);out.writeVarInt(packet.rows.size());
            for(var row:packet.rows){out.writeVarInt(row.key.kind());out.writeUtf(row.key.value(),1048576);out.writeInt(row.key.meta());out.writeVarInt(row.milestones.size());
                for(var step:row.milestones){out.writeByteArray(step.amount().toByteArray());ItemStack.OPTIONAL_STREAM_CODEC.encode(out,step.payout());}}
        },in->{var request=in.readUUID();int size=in.readVarInt();var rows=new ArrayList<Row>();
            for(int i=0;i<size;i++){var key=new AnnihilatorRecipes.Key(in.readVarInt(),in.readUtf(1048576),in.readInt());int count=in.readVarInt();var steps=new ArrayList<AnnihilatorRecipes.Milestone>();
                for(int j=0;j<count;j++)steps.add(new AnnihilatorRecipes.Milestone(new BigInteger(in.readByteArray()),ItemStack.OPTIONAL_STREAM_CODEC.decode(in)));
                rows.add(new Row(key,List.copyOf(steps)));}return new Snapshot(request,List.copyOf(rows));});
        @Override public Type<Snapshot> type(){return TYPE;}
    }
    public record Ack(UUID request) implements CustomPacketPayload {
        public static final Type<Ack> TYPE=new Type<>(ResourceLocation.parse("hbm:annihilator_recipe_ack"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Ack> CODEC=StreamCodec.of((out,packet)->out.writeUUID(packet.request),in->new Ack(in.readUUID()));
        @Override public Type<Ack> type(){return TYPE;}
    }
    @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){
        event.registrar("1").playToClient(Snapshot.TYPE,Snapshot.CODEC,(packet,context)->context.enqueueWork(()->CLIENT.accept(packet)))
            .playToServer(Ack.TYPE,Ack.CODEC,(packet,context)->context.enqueueWork(()->{
                var pending=PENDING.get(packet.request);if(pending!=null&&pending.player.equals(context.player().getUUID())){PENDING.remove(packet.request);pending.future.complete(null);}
            }));
    }
    public static CompletableFuture<Void> sendToAll(MinecraftServer server){
        var futures=new ArrayList<CompletableFuture<Void>>();for(var player:server.getPlayerList().getPlayers())futures.add(send(player));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
    private static CompletableFuture<Void> send(ServerPlayer player){
        var request=UUID.randomUUID();var future=new CompletableFuture<Void>();var server=player.getServer();
        var rows=new ArrayList<Row>();AnnihilatorRecipes.snapshot().forEach((key,steps)->rows.add(new Row(key,steps)));
        PENDING.put(request,new Pending(server,player.getUUID(),server.getTickCount()+200,future));
        try{PacketDistributor.sendToPlayer(player,new Snapshot(request,List.copyOf(rows)));}
        catch(RuntimeException failure){PENDING.remove(request);future.completeExceptionally(failure);}
        return future;
    }
    @EventBusSubscriber(modid="hbm") public static final class Events {
        @SubscribeEvent public static void sync(OnDatapackSyncEvent event){
            var server=event.getPlayerList().getServer();
            CompletableFuture<Void> future=event.getPlayer()==null?sendToAll(server):send(event.getPlayer());
            future.whenComplete((ignored,error)->{if(error!=null)com.hbm.HbmReloaded.LOGGER.warn("Cannot synchronize Annihilator milestones",error);});
        }
        @SubscribeEvent public static void tick(ServerTickEvent.Post event){
            var expired=new ArrayList<UUID>();PENDING.forEach((request,pending)->{if(pending.server==event.getServer()&&event.getServer().getTickCount()-pending.deadline>=0)expired.add(request);});
            for(var request:expired){var pending=PENDING.remove(request);pending.future.completeExceptionally(new IllegalStateException("Annihilator milestone client acknowledgement timed out"));}
        }
        @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event){
            var requests=new ArrayList<UUID>();PENDING.forEach((request,pending)->{if(pending.player.equals(event.getEntity().getUUID()))requests.add(request);});
            for(var request:requests)PENDING.remove(request).future.completeExceptionally(new IllegalStateException("Player left before Annihilator milestone acknowledgement"));
        }
        @SubscribeEvent public static void stopped(ServerStoppedEvent event){
            var requests=new ArrayList<UUID>();PENDING.forEach((request,pending)->{if(pending.server==event.getServer())requests.add(request);});
            for(var request:requests)PENDING.remove(request).future.completeExceptionally(new IllegalStateException("Server stopped before Annihilator milestone acknowledgement"));
        }
    }
    private AnnihilatorRecipeSync(){}
}
