package com.hbm.bomb.nuclear.biome;

import java.util.*;
import java.util.concurrent.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact world queries share one persistent column layer; raw quart data remains a native compatibility view. */
public final class ColumnBiomes {
    private static final Map<Level,WorldState> WORLDS=new ConcurrentHashMap<>();
    private static boolean registered;
    static final class WorldState {
        final Level level;final Registry<Biome> registry;final Map<Long,ColumnBiomeChunk> chunks;
        final Map<ResourceLocation,Holder<Biome>> resolved=new ConcurrentHashMap<>();
        final BiomeManager pristine;
        WorldState(Level level,Map<Long,ColumnBiomeChunk> chunks){this.level=level;this.chunks=chunks;registry=level.registryAccess().registryOrThrow(Registries.BIOME);pristine=level.getBiomeManager().withDifferentSource(this::originalNoise);}
        Holder<Biome> holder(ResourceLocation key){return resolved.computeIfAbsent(key,id->registry.getHolderOrThrow(ResourceKey.create(Registries.BIOME,id)));}
        Holder<Biome> originalNoise(int qx,int qy,int qz){var changed=chunks.get(ChunkPos.asLong(qx>>2,qz>>2));return changed!=null&&changed.touchesQuart(qx,qz)?changed.original.sample(registry,qx,qy,qz):level.getNoiseBiome(qx,qy,qz);}
        Holder<Biome> exact(int x,int z){var chunk=chunks.get(ChunkPos.asLong(x>>4,z>>4));var key=chunk==null?null:chunk.at(x,z);return key==null?null:holder(key);}
        boolean nearOriginal(BlockPos pos){int x=(pos.getX()-2)>>2,z=(pos.getZ()-2)>>2;for(int dx=0;dx<2;dx++)for(int dz=0;dz<2;dz++){int qx=x+dx,qz=z+dz;var chunk=chunks.get(ChunkPos.asLong(qx>>2,qz>>2));if(chunk!=null&&chunk.touchesQuart(qx,qz))return true;}return false;}
    }
    private static WorldState server(ServerLevel level){return WORLDS.computeIfAbsent(level,value->new WorldState(value,ColumnBiomeSavedData.get(level).chunks));}
    public static void register(IEventBus bus){if(registered)return;registered=true;bus.addListener(ColumnBiomePacket::register);NeoForge.EVENT_BUS.addListener(ColumnBiomes::load);NeoForge.EVENT_BUS.addListener(ColumnBiomes::unload);NeoForge.EVENT_BUS.addListener(ColumnBiomes::sent);NeoForge.EVENT_BUS.addListener(ColumnBiomes::unwatch);NeoForge.EVENT_BUS.addListener(ColumnBiomes::stop);}
    private static void load(LevelEvent.Load event){if(event.getLevel() instanceof ServerLevel level)server(level);else if(event.getLevel() instanceof Level level&&level.isClientSide)WORLDS.put(level,new WorldState(level,new ConcurrentHashMap<>()));}
    private static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof Level level)WORLDS.remove(level);}
    private static void stop(ServerStoppedEvent event){WORLDS.clear();}
    /** Null means the native manager can be used unchanged. */
    public static Holder<Biome> biome(Level level,BlockPos pos){var world=WORLDS.get(level);if(world==null||world.chunks.isEmpty())return null;var exact=world.exact(pos.getX(),pos.getZ());if(exact!=null)return exact;return world.nearOriginal(pos)?world.pristine.getBiome(pos):null;}
    /** Block-position noise queries are exact too; raw quart-coordinate APIs retain their native contract. */
    public static Holder<Biome> noiseBiome(Level level,double x,double y,double z){var world=WORLDS.get(level);if(world==null||world.chunks.isEmpty())return null;int bx=Mth.floor(x),by=Mth.floor(y),bz=Mth.floor(z);var exact=world.exact(bx,bz);if(exact!=null)return exact;int qx=bx>>2,qy=by>>2,qz=bz>>2;var chunk=world.chunks.get(ChunkPos.asLong(bx>>4,bz>>4));return chunk!=null&&chunk.touchesQuart(qx,qz)?chunk.original.sample(world.registry,qx,qy,qz):null;}
    public static void column(ServerLevel level,LevelChunk chunk,int x,int z,Holder<Biome> biome){
        if(x>>4!=chunk.getPos().x||z>>4!=chunk.getPos().z)throw new IllegalArgumentException("Column is outside supplied chunk");
        var state=server(level);var columns=state.chunks.computeIfAbsent(chunk.getPos().toLong(),ignored->new ColumnBiomeChunk(BiomePalette.capture(chunk)));if(columns.set(x,z,biome))ColumnBiomeSavedData.get(level).setDirty();
    }
    /** One native palette pass per section, then exact snapshot before vanilla biome packets. */
    public static void sync(ServerLevel level,LevelChunk chunk){var state=server(level);var columns=state.chunks.get(chunk.getPos().toLong());if(columns!=null){columns.applyNative(chunk);var packet=snapshot(level,chunk.getPos(),columns);for(var player:level.getChunkSource().chunkMap.getPlayers(chunk.getPos(),false))PacketDistributor.sendToPlayer(player,packet);}level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(chunk));}
    private static ColumnBiomePacket snapshot(ServerLevel level,ChunkPos pos,ColumnBiomeChunk columns){return new ColumnBiomePacket(level.dimension(),pos.x,pos.z,columns==null?null:columns.original,columns==null?null:columns.targets());}
    private static void sent(ChunkWatchEvent.Sent event){var state=server(event.getLevel());var columns=state.chunks.get(event.getPos().toLong());if(columns!=null)PacketDistributor.sendToPlayer(event.getPlayer(),snapshot(event.getLevel(),event.getPos(),columns));}
    private static void unwatch(ChunkWatchEvent.UnWatch event){var state=WORLDS.get(event.getLevel());if(state!=null&&state.chunks.containsKey(event.getPos().toLong()))PacketDistributor.sendToPlayer(event.getPlayer(),snapshot(event.getLevel(),event.getPos(),null));}
    public static void receive(Level level,ColumnBiomePacket packet){if(!level.isClientSide||!level.dimension().equals(packet.dimension()))return;var state=WORLDS.computeIfAbsent(level,value->new WorldState(value,new ConcurrentHashMap<>()));long key=ChunkPos.asLong(packet.chunkX(),packet.chunkZ());if(packet.original()==null)state.chunks.remove(key);else state.chunks.put(key,ColumnBiomeChunk.from(packet.original(),packet.targets()));}
    private ColumnBiomes(){}
}
