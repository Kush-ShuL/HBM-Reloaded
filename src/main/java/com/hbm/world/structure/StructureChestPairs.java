package com.hbm.world.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FenceBlock;
import com.hbm.block.meteordeco.MetalFenceBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Completes only new chests/panes/fences authored by an actual HBM StructureStart.
 * No inventory is read or moved, and no old/unmarked chest, pane or fence is changed. */
@EventBusSubscriber(modid="hbm")
public final class StructureChestPairs {
    private static final String ORIGIN="hbm:structure_chest_origin125", PENDING="hbm:structure_chest_pending125";
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    private static final DeferredHolder<AttachmentType<?>,AttachmentType<CompoundTag>> CONNECTIONS=ATTACHMENTS.register("new_structure_connections",()->AttachmentType.builder(()->new CompoundTag()).serialize(CompoundTag.CODEC).build());
    private static final ThreadLocal<Scope> CURRENT=new ThreadLocal<>();
    private static final Map<ServerLevel,Work> WORK=new ConcurrentHashMap<>();
    private static final Direction[] SIDES={Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};

    public static void register(IEventBus bus){ATTACHMENTS.register(bus);}

    private static final class Work {
        final LinkedHashSet<BlockPos> positions=new LinkedHashSet<>();
        // 已为 FULL 的结构写入沿同一个就绪队列补齐玻璃板与栅栏连接。
        final LinkedHashSet<BlockPos> fullChunkConnections=new LinkedHashSet<>();
        final LinkedHashSet<Long> loadedChunks=new LinkedHashSet<>();
    }
    private static final class Written {
        final boolean existed;
        boolean chest;
        boolean connection;
        Written(boolean existed){this.existed=existed;}
    }

    public static Scope begin(WorldGenLevel level,StructureStart start,BoundingBox clip) {
        var id=level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(start.getStructure());
        if(id==null||!id.getNamespace().equals("hbm"))return null;
        var origin=id+"@"+start.getChunkPos().x+","+start.getChunkPos().z;
        var scope=new Scope(level,clip,origin,CURRENT.get());
        CURRENT.set(scope);
        return scope;
    }

    /** Capture the real write and its actual chunk, without requesting any neighbor chunk. */
    public static void wrote(Object caller,ChunkAccess chunk,BlockPos position,BlockState previous,BlockState next) {
        var scope=CURRENT.get();
        boolean active=scope!=null&&scope.clip.isInside(position)
                &&(caller==scope.level||caller==scope.level.getLevel());
        boolean newborn=active&&connection(previous)&&owned(chunk,position,previous,scope.origin);
        if(previous.getBlock()!=next.getBlock()&&connection(previous))forget(chunk,position);
        if(!active)return;
        if(position.getX()==scope.clip.minX()||position.getX()==scope.clip.maxX()
                ||position.getZ()==scope.clip.minZ()||position.getZ()==scope.clip.maxZ())
            scope.borderWrites.add(position.immutable());
        var existing=scope.writes.get(position);
        if(existing==null&&!chest(previous)&&!chest(next)&&!connection(previous)&&!connection(next))return;
        if(existing==null){existing=new Written((chest(previous)||connection(previous))&&!newborn);scope.writes.put(position.immutable(),existing);}
        existing.chest=chest(next);
        existing.connection=connection(next);
    }

    public static final class Scope implements AutoCloseable {
        private final WorldGenLevel level;
        private final BoundingBox clip;
        private final String origin;
        private final Scope prior;
        private final Map<BlockPos,Written> writes=new LinkedHashMap<>();
        private final LinkedHashSet<BlockPos> borderWrites=new LinkedHashSet<>();
        private boolean closed;
        private Scope(WorldGenLevel level,BoundingBox clip,String origin,Scope prior){this.level=level;this.clip=clip;this.origin=origin;this.prior=prior;}
        public void complete() {
            var positions=new ArrayList<BlockPos>();
            var fullChunkConnections=new ArrayList<BlockPos>();
            var ownership=new LinkedHashMap<ChunkAccess,List<BlockPos>>();
            for(var entry:writes.entrySet()) {
                if(entry.getValue().existed)continue;
                var pos=entry.getKey();
                if(entry.getValue().connection&&connection(level.getBlockState(pos))) {
                    var chunk=level.getChunk(pos);
                    if(chunk instanceof ImposterProtoChunk wrapper)chunk=wrapper.getWrapped();
                    ownership.computeIfAbsent(chunk,ignored->new ArrayList<>()).add(pos);
                    if(chunk instanceof ProtoChunk)chunk.markPosForPostprocessing(pos);
                    else {
                        // FULL chunks no longer consume native generation offsets. Persist only
                        // our newly authored ownership and use the existing ready-only queue.
                        fullChunkConnections.add(pos);
                    }
                    continue;
                }
                if(!entry.getValue().chest)continue;
                if(!chest(level.getBlockState(pos)))continue;
                if(!(level.getBlockEntity(pos) instanceof ChestBlockEntity tile))
                    throw new IllegalStateException("New native structure chest has no actual block entity at "+pos);
                tile.getPersistentData().putString(ORIGIN,origin);
                tile.getPersistentData().putBoolean(PENDING,true);
                tile.setChanged();
                positions.add(pos);
            }
            ownership.forEach((chunk,ownedPositions)->remember(chunk,ownedPositions,origin));
            if(!positions.isEmpty()||!fullChunkConnections.isEmpty()||!borderWrites.isEmpty())level.getLevel().getServer().execute(()->{
                var work=WORK.computeIfAbsent(level.getLevel(),ignored->new Work());
                work.positions.addAll(positions);work.fullChunkConnections.addAll(fullChunkConnections);
                // A later clip may replace air with a wall (or clear a wall). Revisit only
                // same-start newborn neighbors; unmarked pre-existing fences are excluded.
                for(var changed:borderWrites) {
                    enqueueOwned(level.getLevel(),work,changed,origin);
                    for(var side:SIDES)enqueueOwned(level.getLevel(),work,changed.relative(side),origin);
                }
            });
        }
        @Override public void close(){if(!closed){closed=true;if(prior==null)CURRENT.remove();else CURRENT.set(prior);}}
    }

    private static boolean chest(BlockState state){return state.is(Blocks.CHEST)||state.is(Blocks.TRAPPED_CHEST);}
    private static boolean pane(BlockState state){return state.is(Blocks.GLASS_PANE)||state.getBlock() instanceof StainedGlassPaneBlock;}
    /** 原生 FenceBlock 保留木/下界砖/真实子类自己的连接判定；金属围栏走其既有 updateShape。 */
    private static boolean connection(BlockState state){return pane(state)||state.getBlock() instanceof FenceBlock||state.getBlock() instanceof MetalFenceBlock;}
    /** All changed border neighbors matter during placement; normal writes only invalidate
     * ownership when replacing a connective block. No attachment is allocated on reads. */
    public static boolean trackedWrite(BlockState previous,BlockState next){return previous!=null&&previous!=next&&(CURRENT.get()!=null||previous.getBlock()!=next.getBlock()&&connection(previous));}

    private static ChunkAccess actual(ChunkAccess chunk){return chunk instanceof ImposterProtoChunk wrapper?wrapper.getWrapped():chunk;}
    private static boolean owned(ChunkAccess chunk,BlockPos pos,BlockState state,String origin) {
        var data=actual(chunk).getExistingDataOrNull(CONNECTIONS);
        if(data==null)return false;
        var marker=data.getCompound(Long.toString(pos.asLong()));
        return (!marker.getString("origin").isEmpty())&&(origin==null||origin.equals(marker.getString("origin")))
                &&BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals(marker.getString("block"));
    }
    private static void remember(ChunkAccess chunk,List<BlockPos> positions,String origin) {
        chunk=actual(chunk);
        synchronized(chunk) {
            var previous=chunk.getExistingDataOrNull(CONNECTIONS);
            var data=previous==null?new CompoundTag():previous.copy();
            for(var pos:positions) {
                var marker=new CompoundTag();marker.putString("origin",origin);
                marker.putString("block",BuiltInRegistries.BLOCK.getKey(chunk.getBlockState(pos).getBlock()).toString());
                data.put(Long.toString(pos.asLong()),marker);
            }
            chunk.setData(CONNECTIONS,data);
        }
    }
    private static void forget(ChunkAccess chunk,BlockPos pos) {
        chunk=actual(chunk);
        synchronized(chunk) {
            var previous=chunk.getExistingDataOrNull(CONNECTIONS);
            var key=Long.toString(pos.asLong());
            if(previous==null||!previous.contains(key))return;
            var data=previous.copy();data.remove(key);
            if(data.isEmpty())chunk.removeData(CONNECTIONS);else chunk.setData(CONNECTIONS,data);
        }
    }
    private static void enqueueOwned(ServerLevel level,Work work,BlockPos pos,String origin) {
        var chunk=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if(chunk!=null&&owned(chunk,pos,chunk.getBlockState(pos),origin))work.fullChunkConnections.add(pos.immutable());
    }

    /** Chunk Load can fire before FULL; defer all reads until a later server tick. Only
     * this loaded chunk's pending new-generation markers are considered, never old data migration. */
    @SubscribeEvent public static void loaded(ChunkEvent.Load event) {
        if(!(event.getLevel() instanceof ServerLevel level))return;
        long key=event.getChunk().getPos().toLong();
        level.getServer().execute(()->WORK.computeIfAbsent(level,ignored->new Work()).loadedChunks.add(key));
    }
    @SubscribeEvent public static void unloaded(ChunkEvent.Unload event) {
        if(!(event.getLevel() instanceof ServerLevel level))return;
        var chunk=event.getChunk().getPos();
        level.getServer().execute(()->{
            var work=WORK.get(level);if(work==null)return;
            work.loadedChunks.remove(chunk.toLong());
            work.positions.removeIf(pos->pos.getX()>>4==chunk.x&&pos.getZ()>>4==chunk.z);
            work.fullChunkConnections.removeIf(pos->pos.getX()>>4==chunk.x&&pos.getZ()>>4==chunk.z);
        });
    }
    @SubscribeEvent public static void tick(ServerTickEvent.Post event) {
        for(var entry:WORK.entrySet()) {
            var level=entry.getKey();if(level.getServer()!=event.getServer())continue;
            var work=entry.getValue();
            for(int i=0,n=Math.min(4,work.loadedChunks.size());i<n;i++) {
                var iterator=work.loadedChunks.iterator();long key=iterator.next();iterator.remove();
                var chunk=level.getChunkSource().getChunkNow(ChunkPos.getX(key),ChunkPos.getZ(key));
                if(chunk==null){work.loadedChunks.add(key);continue;}
                for(var tile:chunk.getBlockEntities().values())
                    if(tile instanceof ChestBlockEntity&&tile.getPersistentData().getBoolean(PENDING))
                        work.positions.add(tile.getBlockPos().immutable());
                var connections=chunk.getExistingDataOrNull(CONNECTIONS);
                if(connections!=null)for(var keyString:connections.getAllKeys()) {
                    var pos=BlockPos.of(Long.parseLong(keyString));
                    if(owned(chunk,pos,chunk.getBlockState(pos),null))work.fullChunkConnections.add(pos);
                }
            }
            for(int i=0,n=Math.min(64,work.positions.size());i<n;i++) {
                var iterator=work.positions.iterator();var pos=iterator.next();iterator.remove();
                if(!finish(level,pos))work.positions.add(pos);
            }
            for(int i=0,n=Math.min(64,work.fullChunkConnections.size());i<n;i++) {
                var iterator=work.fullChunkConnections.iterator();var pos=iterator.next();iterator.remove();
                var chunks=ready(level,pos);
                if(chunks==null){work.fullChunkConnections.add(pos);continue;}
                var state=state(chunks,pos);
                var chunk=chunks.get(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));
                if(connection(state)&&owned(chunk,pos,state,null))write(level,pos,Block.updateFromNeighbourShapes(state,level,pos));
            }
        }
    }

    /** Only ready LevelChunks are queried. Radius2 includes both chests' four-neighbour
     * checks, so a missing neighbour cannot accidentally validate a triple as a pair. */
    private static Map<Long,LevelChunk> ready(ServerLevel level,BlockPos position) {
        var chunks=new HashMap<Long,LevelChunk>();
        for(int x=(position.getX()-2)>>4;x<=(position.getX()+2)>>4;x++)
            for(int z=(position.getZ()-2)>>4;z<=(position.getZ()+2)>>4;z++) {
                var chunk=level.getChunkSource().getChunkNow(x,z);
                if(chunk==null)return null;
                chunks.put(ChunkPos.asLong(x,z),chunk);
            }
        return chunks;
    }
    private static BlockState state(Map<Long,LevelChunk> chunks,BlockPos pos){return chunks.get(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4)).getBlockState(pos);}
    private static ChestBlockEntity tile(Map<Long,LevelChunk> chunks,BlockPos pos){var tile=chunks.get(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4)).getBlockEntity(pos);return tile instanceof ChestBlockEntity chest?chest:null;}
    private static List<BlockPos> adjacent(Map<Long,LevelChunk> chunks,BlockPos pos,Block block) {
        var found=new ArrayList<BlockPos>();for(var side:SIDES){var other=pos.relative(side);if(state(chunks,other).is(block))found.add(other);}return found;
    }

    private static boolean finish(ServerLevel level,BlockPos pos) {
        var local=level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4);
        if(local==null)return false;
        if(!(local.getBlockEntity(pos) instanceof ChestBlockEntity first)||!chest(local.getBlockState(pos))
                ||!first.getPersistentData().getBoolean(PENDING))return true;
        var chunks=ready(level,pos);if(chunks==null)return false;
        var original=state(chunks,pos);
        String origin=first.getPersistentData().getString(ORIGIN);
        if(origin.isEmpty())return true;
        var neighbors=adjacent(chunks,pos,original.getBlock());
        if(neighbors.size()!=1){single(level,pos,original,first);return true;}
        var partnerPos=neighbors.getFirst();
        var partner=tile(chunks,partnerPos);
        var partnerState=state(chunks,partnerPos);
        if(partner==null||!partner.getPersistentData().getString(ORIGIN).equals(origin)
                ||adjacent(chunks,partnerPos,original.getBlock()).size()!=1) {
            single(level,pos,original,first);return true;
        }
        // Canonical position order makes the common facing independent of chunk-load/task order.
        BlockPos a=pos.compareTo(partnerPos)<=0?pos:partnerPos,b=a.equals(pos)?partnerPos:pos;
        var aState=a.equals(pos)?original:partnerState;
        var bState=a.equals(pos)?partnerState:original;
        // Use final placed (already rotated/mirrored) facings. Keep the existing common
        // facing whenever valid; a source front-to-back pair needs a legal perpendicular facing.
        Direction toward=Direction.fromDelta(b.getX()-a.getX(),0,b.getZ()-a.getZ());
        Direction facing=aState.getValue(ChestBlock.FACING);
        if(facing.getAxis()==toward.getAxis()) {
            var otherFacing=bState.getValue(ChestBlock.FACING);
            facing=otherFacing.getAxis()!=toward.getAxis()?otherFacing:toward.getClockWise();
        }
        ChestType type=toward==facing.getClockWise()?ChestType.LEFT:ChestType.RIGHT;
        write(level,a,aState.setValue(ChestBlock.FACING,facing).setValue(ChestBlock.TYPE,type));
        write(level,b,bState.setValue(ChestBlock.FACING,facing).setValue(ChestBlock.TYPE,type.getOpposite()));
        done(first);done(partner);
        return true;
    }
    private static void single(ServerLevel level,BlockPos pos,BlockState state,ChestBlockEntity tile){write(level,pos,state.setValue(ChestBlock.TYPE,ChestType.SINGLE));done(tile);}
    private static void write(ServerLevel level,BlockPos pos,BlockState state) {
        // Same block identity retains its actual 27-slot BE/loot/lock. Do not send
        // neighbour shape updates capable of attaching or modifying an unmarked old chest.
        if(!level.getBlockState(pos).equals(state))level.setBlock(pos,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
    }
    private static void done(ChestBlockEntity tile){tile.getPersistentData().remove(PENDING);tile.setChanged();}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level)WORK.remove(level);}
    @SubscribeEvent public static void stopped(ServerStoppedEvent event){WORK.keySet().removeIf(level->level.getServer()==event.getServer());}
    private StructureChestPairs(){}
}
