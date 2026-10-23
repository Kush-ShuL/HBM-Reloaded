package com.hbm.explosion;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.IdentityHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;

/** Original GSP resistance rays and original float-length endpoint replay.
 * Immutable terrain reads, parked cold rays and deduplicated radial write batches
 * change scheduling, never the ray set, resistance arithmetic or final voxel union. */
public class ExplosionNukeRayBatched implements IExplosionRay {
    private static final int CURSORS=16,BAND_WIDTH=8;
    private static final double[] LIQUID_LOSS=new double[0];
    private final int posX,posY,posZ,strength,length,speed,gspNumMax;
    private final Level world;
    private final OriginalSpiralOrder rayOrder;
    private final NuclearChunkAccess chunkAccess=new NuclearChunkAccess();
    private final BlockPos.MutableBlockPos cursor=new BlockPos.MutableBlockPos();
    private final Long2ObjectOpenHashMap<PalettedContainer<BlockState>> originalSections=new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<UniformSection> uniformSections=new Long2ObjectOpenHashMap<>();
    private final LongOpenHashSet originalAir=new LongOpenHashSet();
    private final Long2ObjectOpenHashMap<AirSpan> airSpans=new Long2ObjectOpenHashMap<>();
    private final IdentityHashMap<BlockState,double[]> blockLosses=new IdentityHashMap<>();
    private final Int2ObjectOpenHashMap<double[]> resistanceLosses=new Int2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<BitSet> seenSections=new Long2ObjectOpenHashMap<>();
    // Successful quiet deletes that can still acquire a later source tip flag.
    private final Long2ObjectOpenHashMap<BitSet> quietSections=new Long2ObjectOpenHashMap<>();
    private final Long2IntOpenHashMap pendingByChunk=new Long2IntOpenHashMap();
    private final ArrayList<Band> bands=new ArrayList<>();
    private final Trace[] traces=new Trace[CURSORS];
    private int issuedRays,completedRays,nextCursor;
    private int gspNum=1;
    public boolean isAusf3Complete;
    private record AirSpan(int minY,int maxY){}
    private record UniformSection(BlockState state){}
    private static final UniformSection MIXED_SECTION=new UniformSection(null);
    private static final class UniformProbe implements java.util.function.Consumer<BlockState>,java.util.function.Predicate<BlockState>{
        BlockState first;boolean mixed;
        UniformProbe(BlockState first){this.first=first;}
        @Override public boolean test(BlockState state){return state!=first;}
        @Override public void accept(BlockState state){if(first==null)first=state;else if(first!=state)mixed=true;}
    }
    private static final class Band {
        final Long2ObjectLinkedOpenHashMap<WriteChunk> chunks=new Long2ObjectLinkedOpenHashMap<>();
    }
    private static final class WriteChunk {
        final long key;final int x,z;
        final Int2ObjectOpenHashMap<WriteSection> sections=new Int2ObjectOpenHashMap<>();
        WriteChunk(long key){this.key=key;x=ChunkPos.getX(key);z=ChunkPos.getZ(key);}
    }
    private static final class WriteSection {
        final int y,linearFlip;
        final BitSet pending=new BitSet(4096);
        BitSet ends;
        Int2ObjectOpenHashMap<BlockState> lateTips;
        int cursor;
        WriteSection(int y,int linearFlip){this.y=y;this.linearFlip=linearFlip;}
        void markTip(int bit){if(ends==null)ends=new BitSet(4096);ends.set(bit);}
    }
    private static final class Trace {
        final LongArrayList chunks=new LongArrayList(32);
        final OriginalRayReplay replay=new OriginalRayReplay();
        final UniformRayTrace.Result uniformResult=new UniformRayTrace.Result();
        boolean active,replaying,hasTip;
        double dx,dy,dz;
        int step;
        float resistance,x,y,z;
        long lastChunk=Long.MIN_VALUE,replayChunk=Long.MIN_VALUE,section=Long.MIN_VALUE;
        boolean allowed;
        long replaySection=Long.MIN_VALUE;
        boolean replayAir;
        int replayFlip;
        PalettedContainer<BlockState> replayPalette;
        BitSet replaySeen;
        PalettedContainer<BlockState> palette;
        BlockState uniform;
        void start(double latitude,double longitude,int strength){
            active=true;replaying=false;hasTip=false;chunks.clear();
            dx=Math.sin(latitude)*Math.cos(longitude);dy=Math.cos(latitude);dz=Math.sin(latitude)*Math.sin(longitude);
            step=replay.step=0;resistance=strength;lastChunk=replayChunk=section=Long.MIN_VALUE;palette=null;uniform=null;clearReplayCache();
        }
        void clearReplayCache(){replaySection=Long.MIN_VALUE;replayPalette=null;replaySeen=null;replayAir=false;}
        void replay(int originX,int originY,int originZ){
            replaying=true;clearReplayCache();replay.reset(originX,originY,originZ,x,y,z);
        }
    }

    public ExplosionNukeRayBatched(Level world,int x,int y,int z,int strength,int speed,int length){
        this.world=world;posX=x;posY=y;posZ=z;this.strength=strength;this.speed=speed;this.length=length;
        gspNumMax=(int)(2.5*Math.PI*Math.pow(strength,2));
        rayOrder=new OriginalSpiralOrder(Math.max(1,gspNumMax));
        for(int i=0;i<traces.length;i++)traces[i]=new Trace();
        if(gspNumMax==0)isAusf3Complete=true;
    }

    public void collectTip(int count){collectTip(count,Long.MAX_VALUE);}
    private void collectTip(int count,long deadline){
        if(isAusf3Complete||!rayOrder.prepare(deadline))return;
        int target=(int)Math.min(gspNumMax,(long)completedRays+Math.max(0,count));
        while(completedRays<target&&System.nanoTime()<deadline){
            boolean progress=false;
            for(int checked=0;checked<traces.length&&completedRays<target;checked++){
                if(System.nanoTime()>=deadline)return;
                Trace ray=traces[nextCursor];nextCursor=(nextCursor+1)%traces.length;
                if(!ray.active){
                    if(issuedRays>=gspNumMax)continue;
                    ray.start(rayOrder.latitude(),rayOrder.longitude(),strength);rayOrder.advance();issuedRays++;progress=true;
                }
                int oldStep=ray.step,oldReplay=ray.replay.step;boolean replaying=ray.replaying;
                if(advance(ray,deadline)){
                    ray.active=false;completedRays++;gspNum=completedRays+1;progress=true;
                    if(completedRays==gspNumMax){
                        isAusf3Complete=true;originalSections.clear();uniformSections.clear();originalAir.clear();airSpans.clear();seenSections.clear();quietSections.clear();
                        blockLosses.clear();resistanceLosses.clear();
                        for(Trace other:traces){other.palette=null;other.uniform=null;other.clearReplayCache();}
                    }
                }else if(oldStep!=ray.step||oldReplay!=ray.replay.step||replaying!=ray.replaying)progress=true;
            }
            // Every live cursor is waiting on real asynchronous chunk work.
            // Do not spin or let one cold ray block the other directions.
            if(!progress)return;
        }
    }

    private boolean advance(Trace ray,long deadline){
        int operations=0,limit=Math.min(length,strength);
        while(!ray.replaying){
            if((operations++&63)==0&&System.nanoTime()>=deadline)return false;
            if(ray.step>=limit){if(!ray.hasTip)return true;ray.replay(posX,posY,posZ);break;}
            int i=ray.step;
            float x=(float)(posX+ray.dx*i),y=(float)(posY+ray.dy*i),z=(float)(posZ+ray.dz*i);
            int bx=(int)Math.floor(x),by=(int)Math.floor(y),bz=(int)Math.floor(z);
            if(world.isOutsideBuildHeight(by)){
                if(by>=world.getMaxBuildHeight()&&ray.dy>=0||by<world.getMinBuildHeight()&&ray.dy<=0)ray.step=limit;
                else ray.step++;
                continue;
            }
            long section=SectionPos.asLong(bx>>4,by>>4,bz>>4);
            if(ray.section!=section){
                var snapshot=original(bx,by,bz);
                if(snapshot==null)return false;
                ray.section=section;ray.palette=snapshot;ray.uniform=null;
                if(!originalAir.contains(section)){
                    var uniform=uniformSections.get(section);
                    if(uniform==null){
                        // Only actual values in the immutable source palette can
                        // prove uniformity; mixed sections retain the old loop.
                        var probe=new UniformProbe(snapshot.get(0,0,0));
                        // maybeHas examines palette entries only. A false result
                        // proves every represented state is the first state. A
                        // true result still needs getAll to exclude unused IDs.
                        if(snapshot.maybeHas(probe))snapshot.getAll(probe);
                        uniform=probe.mixed||probe.first==null?MIXED_SECTION:new UniformSection(probe.first);
                        uniformSections.put(section,uniform);
                    }
                    ray.uniform=uniform.state();
                }
            }
            if(originalAir.contains(section)){
                ray.step=skipAir(ray.step,limit,ray.dx,ray.dy,ray.dz,bx,by,bz,true);continue;
            }
            if(ray.uniform!=null){
                int end=UniformRayTrace.sectionEnd(ray.step,limit,posX,posY,posZ,ray.dx,ray.dy,ray.dz,bx,by,bz);
                if(ray.uniform.isAir()){ray.step=end;continue;}
                var result=ray.uniformResult;
                boolean finished=UniformRayTrace.advance(result,ray.step,end,limit,ray.resistance,lossesFor(ray.uniform),posX,posY,posZ,ray.dx,ray.dy,ray.dz,deadline);
                ray.step=result.nextStep;ray.resistance=result.resistance;
                if(result.lastPositive>=0){
                    ray.hasTip=true;ray.x=result.x;ray.y=result.y;ray.z=result.z;
                    if(result.chunk!=ray.lastChunk){ray.chunks.add(result.chunk);ray.lastChunk=result.chunk;}
                }
                if(!finished)return false;
                continue;
            }
            BlockState state=ray.palette.get(bx&15,by&15,bz&15);
            if(!state.isAir())ray.resistance-=resistanceLoss(state,i);
            if(ray.resistance>0&&!state.isAir()){
                ray.hasTip=true;ray.x=x;ray.y=y;ray.z=z;
                long chunk=ChunkPos.asLong(bx>>4,bz>>4);
                if(chunk!=ray.lastChunk){ray.chunks.add(chunk);ray.lastChunk=chunk;}
            }
            ray.step++;
            if(ray.resistance<=0)ray.step=limit;
        }
        while(ray.replay.step<ray.replay.distance){
            if((operations++&63)==0&&System.nanoTime()>=deadline)return false;
            ray.replay.next();
            int x=ray.replay.x,y=ray.replay.y,z=ray.replay.z;
            if(world.isOutsideBuildHeight(y))continue;
            long chunk=ChunkPos.asLong(x>>4,z>>4);
            if(chunk!=ray.replayChunk){ray.replayChunk=chunk;ray.allowed=ray.chunks.contains(chunk);}
            if(!ray.allowed)continue;
            long section=SectionPos.asLong(x>>4,y>>4,z>>4);
            if(ray.replaySection!=section){
                ray.replaySection=section;ray.replayAir=originalAir.contains(section);
                ray.replayPalette=ray.replayAir?null:originalSections.get(section);
                ray.replaySeen=seenSections.get(section);
                ray.replayFlip=MortonSectionOrder.linearFlip(posX,posY,posZ,x>>4,y>>4,z>>4);
                if(!ray.replayAir&&ray.replayPalette==null)throw new IllegalStateException("Original replay lost its source chunk snapshot");
            }
            if(ray.replayAir){
                ray.replay.step=skipAir(ray.replay.step,(int)Math.ceil(ray.replay.distance),ray.replay.dx,ray.replay.dy,ray.replay.dz,x,y,z,false);continue;
            }
            if(!ray.replayPalette.get(x&15,y&15,z&15).isAir())record(ray,x,y,z,ray.replay.isTip());
        }
        return true;
    }

    private PalettedContainer<BlockState> original(int x,int y,int z){
        long key=SectionPos.asLong(x>>4,y>>4,z>>4);
        var existing=originalSections.get(key);if(existing!=null)return existing;
        LevelChunk chunk=world instanceof ServerLevel server?chunkAccess.get(server,x>>4,z>>4):world.getChunk(x>>4,z>>4);
        if(chunk==null)return null;
        for(int i=0;i<chunk.getSections().length;i++){
            long section=SectionPos.asLong(x>>4,chunk.getSectionYFromSectionIndex(i),z>>4);
            if(!originalSections.containsKey(section)){
                var source=chunk.getSection(i);originalSections.put(section,source.getStates().copy());
                if(source.hasOnlyAir())originalAir.add(section);
            }
        }
        return originalSections.get(key);
    }

    private double resistanceLoss(BlockState state,int step){
        double[] losses=lossesFor(state);
        return losses.length==0?0:losses[step];
    }

    private double[] lossesFor(BlockState state){
        double[] losses=blockLosses.get(state);
        if(losses==null){
            if(com.hbm.block.fluid.LegacyMaterials.of(state).isLiquid())losses=LIQUID_LOSS;
            else{
                float resistance=masqueradeResistance(state);int key=Float.floatToRawIntBits(resistance);losses=resistanceLosses.get(key);
                if(losses==null){
                    losses=new double[Math.max(1,Math.min(strength,Math.max(1,length)))];
                    for(int i=0;i<losses.length;i++){double factor=(100-((double)i/strength)*100)*.07D;losses[i]=Math.pow(resistance,7.5D-factor);}
                    resistanceLosses.put(key,losses);
                }
            }
            blockLosses.put(state,losses);
        }
        return losses;
    }

    private int skipAir(int next,int limit,double dx,double dy,double dz,int x,int y,int z,boolean floats){
        long key=SectionPos.asLong(x>>4,y>>4,z>>4);var span=airSpans.get(key);
        if(span==null){
            int min=y>>4,max=min;
            while(min>world.getMinSection()&&originalAir.contains(SectionPos.asLong(x>>4,min-1,z>>4)))min--;
            while(max+1<world.getMaxSection()&&originalAir.contains(SectionPos.asLong(x>>4,max+1,z>>4)))max++;
            span=new AirSpan(min<<4,(max+1)<<4);
            for(int section=min;section<=max;section++)airSpans.put(SectionPos.asLong(x>>4,section,z>>4),span);
        }
        return OriginalRayReplay.skipAir(next,limit,posX,posY,posZ,dx,dy,dz,x>>4,z>>4,span.minY,span.maxY,floats);
    }

    private void record(Trace ray,int x,int y,int z,boolean tip){
        long section=ray.replaySection;int flip=ray.replayFlip;
        int bit=MortonSectionOrder.encode(((y&15)<<8)|((z&15)<<4)|(x&15),flip);
        BitSet seen=ray.replaySeen;
        if(seen==null){
            // Another cursor may have created this section while this cursor was
            // paused over air. Adopt the canonical mutable bitmap, never replace it.
            seen=seenSections.get(section);
            if(seen==null){seen=new BitSet(4096);seenSections.put(section,seen);}
            ray.replaySeen=seen;
        }
        if(seen.get(bit)&&!tip)return;
        double dx=x-posX,dy=y-posY,dz=z-posZ;
        int band=(int)(Math.sqrt(dx*dx+dy*dy+dz*dz)/BAND_WIDTH);
        while(bands.size()<=band)bands.add(new Band());
        Band bucket=bands.get(band);long key=ChunkPos.asLong(x>>4,z>>4);
        WriteChunk chunk=bucket.chunks.get(key);WriteSection pending=chunk==null?null:chunk.sections.get(y>>4);
        if(seen.get(bit)){
            if(tip&&pending!=null&&pending.pending.get(bit))pending.markTip(bit);
            else if(tip){
                BitSet quiet=quietSections.get(section);
                if(quiet!=null&&quiet.get(bit)){
                    var snapshot=ray.replayPalette;
                    if(snapshot==null)throw new IllegalStateException("Late nuclear tip lost its source snapshot");
                    if(chunk==null){chunk=new WriteChunk(key);bucket.chunks.put(key,chunk);}
                    if(pending==null){pending=new WriteSection(y>>4,flip);chunk.sections.put(y>>4,pending);}
                    if(pending.lateTips==null)pending.lateTips=new Int2ObjectOpenHashMap<>();
                    pending.lateTips.put(bit,snapshot.get(x&15,y&15,z&15));
                    pending.pending.set(bit);pending.cursor=Math.min(pending.cursor,bit);
                    pendingByChunk.addTo(key,1);
                    quiet.clear(bit);
                    if(quiet.isEmpty())quietSections.remove(section);
                }
            }
            return;
        }
        seen.set(bit);
        if(chunk==null){chunk=new WriteChunk(key);bucket.chunks.put(key,chunk);}
        if(pending==null){pending=new WriteSection(y>>4,flip);chunk.sections.put(y>>4,pending);}
        pending.pending.set(bit);if(tip)pending.markTip(bit);pending.cursor=Math.min(pending.cursor,bit);
        pendingByChunk.addTo(key,1);
    }

    public void processChunk(){flush(Long.MAX_VALUE);}
    private void flush(long deadline){
        // Density of the evenly stratified source rays grows with processed count.
        // Publish only the corresponding inner sphere; distant individual rays stay
        // coalesced in bitmaps rather than carving visible isolated wedges.
        double front=length*Math.sqrt(completedRays/(double)Math.max(1,gspNumMax));
        // Publish the small center band as soon as an actual source ray has
        // finished. Requiring a full density band first hides every terrain
        // update of a large cold blast until thousands of rays have completed.
        int readyBand=completedRays>0?Math.max(0,(int)(front/BAND_WIDTH)-1):-1;
        int maximum=isAusf3Complete?bands.size()-1:Math.min(bands.size()-1,readyBand);
        for(int band=0;band<=maximum&&System.nanoTime()<deadline;band++){
            Band bucket=bands.get(band);
            while(!bucket.chunks.isEmpty()&&System.nanoTime()<deadline){
                WriteChunk selected=null;LevelChunk loaded=null;int scanned=0;
                var cold=new LongArrayList(64);
                // Prefer already resident chunks. An unresolved request cannot pin
                // the entire destruction queue behind one cold chunk.
                for(WriteChunk work:bucket.chunks.values()){
                    loaded=world instanceof ServerLevel server?chunkAccess.peekReady(server,work.x,work.z):world.getChunk(work.x,work.z);
                    if(loaded!=null){selected=work;break;}
                    cold.add(work.key);
                    if(++scanned>=64||System.nanoTime()>=deadline)break;
                }
                if(selected==null){
                    scanned=0;
                    for(int i=0;i<cold.size();i++){
                        WriteChunk work=bucket.chunks.get(cold.getLong(i));
                        loaded=world instanceof ServerLevel server?chunkAccess.get(server,work.x,work.z):world.getChunk(work.x,work.z);
                        if(loaded!=null){selected=work;break;}
                        if(++scanned>=16||System.nanoTime()>=deadline)break;
                    }
                }
                // The next slice must also see ready work beyond this cold prefix.
                for(int i=0;i<cold.size();i++)bucket.chunks.getAndMoveToLast(cold.getLong(i));
                if(selected==null)break;
                // Readiness probes do not hold a work ticket or refresh LRU. Touch
                // the selected chunk and its asynchronous neighbour halo before any
                // quiet delete, source tip or late-tip callback can query adjacent terrain.
                if(world instanceof ServerLevel server){
                    loaded=chunkAccess.getWithNeighbors(server,selected.x,selected.z);
                    if(loaded==null){bucket.chunks.getAndMoveToLast(selected.key);continue;}
                }
                if(!write(selected,loaded,deadline))return;
                bucket.chunks.remove(selected.key);
            }
        }
    }

    private boolean write(WriteChunk chunk,LevelChunk live,long deadline){
        int operations=0;
        while(!chunk.sections.isEmpty()){
            var section=nearestSection(chunk);
            long sectionKey=SectionPos.asLong(chunk.x,section.y,chunk.z);
            BitSet quiet=quietSections.get(sectionKey);
            int bit;
            while((bit=section.pending.nextSetBit(section.cursor))>=0){
                if((operations++&31)==0&&System.nanoTime()>=deadline)return false;
                int linear=MortonSectionOrder.linear(bit,section.linearFlip);
                int x=(chunk.x<<4)|(linear&15),y=(section.y<<4)|(linear>>8),z=(chunk.z<<4)|((linear>>4)&15);
                cursor.set(x,y,z);
                BlockState late=section.lateTips==null?null:section.lateTips.get(bit);
                if(late!=null){
                    notifyLateTip(cursor,late);
                    section.lateTips.remove(bit);
                }else if(!live.getBlockState(cursor).isAir()){
                    if(section.ends!=null&&section.ends.get(bit))handleTip(x,y,z);
                    else if(world.setBlock(cursor,Blocks.AIR.defaultBlockState(),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE)&&!isAusf3Complete){
                        if(quiet==null){quiet=new BitSet(4096);quietSections.put(sectionKey,quiet);}
                        quiet.set(bit);
                    }
                }
                section.pending.clear(bit);if(section.ends!=null)section.ends.clear(bit);section.cursor=bit+1;
                int left=pendingByChunk.addTo(chunk.key,-1)-1;if(left==0)pendingByChunk.remove(chunk.key);
            }
            chunk.sections.remove(section.y);
        }
        return true;
    }

    /** Replay only the neighbor/shape phases suppressed by the earlier quiet delete. */
    private void notifyLateTip(BlockPos pos,BlockState original){
        pos=pos.immutable();
        BlockState air=Blocks.AIR.defaultBlockState();
        if(world.getBlockState(pos)!=air)return;
        world.blockUpdated(pos,original.getBlock());
        if(world.getBlockState(pos)!=air)return;
        int flags=Block.UPDATE_ALL&~(Block.UPDATE_NEIGHBORS|Block.UPDATE_SUPPRESS_DROPS);
        int recursion=Block.UPDATE_LIMIT-1;
        original.updateIndirectNeighbourShapes(world,pos,flags,recursion);
        if(world.getBlockState(pos)!=air)return;
        air.updateNeighbourShapes(world,pos,flags,recursion);
        if(world.getBlockState(pos)==air)air.updateIndirectNeighbourShapes(world,pos,flags,recursion);
    }

    private WriteSection nearestSection(WriteChunk chunk){
        WriteSection closest=null;long best=Long.MAX_VALUE;
        for(var section:chunk.sections.values()){
            long base=(long)section.y<<4;
            long distance=Math.max(0,Math.max(base-posY,posY-(base+15)));
            if(distance<best||distance==best&&(closest==null||section.y<closest.y)){closest=section;best=distance;}
        }
        return closest;
    }

    public static float masqueradeResistance(BlockState state){
        if(state.is(Blocks.SANDSTONE))return Blocks.STONE.getExplosionResistance();
        if(state.is(Blocks.OBSIDIAN))return Blocks.STONE.getExplosionResistance()*3;
        return state.getBlock().getExplosionResistance();
    }
    protected void handleTip(int x,int y,int z){world.setBlock(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState(),3);}
    @Override public boolean isComplete(){return isAusf3Complete&&pendingByChunk.isEmpty();}
    public boolean isTerrainComplete(int x,int z){return isAusf3Complete&&!pendingByChunk.containsKey(ChunkPos.asLong(x,z));}
    @Override public void cacheChunksTick(int time){cacheUntil(System.nanoTime()+time*1_000_000L);}
    public void cacheUntil(long deadline){collectTip(Integer.MAX_VALUE,deadline);}
    @Override public void destructionTick(int time){destroyUntil(System.nanoTime()+time*1_000_000L);}
    public void destroyUntil(long deadline){flush(deadline);}
    @Override public void cancel(){
        isAusf3Complete=true;chunkAccess.close();originalSections.clear();uniformSections.clear();originalAir.clear();airSpans.clear();
        seenSections.clear();quietSections.clear();bands.clear();pendingByChunk.clear();blockLosses.clear();resistanceLosses.clear();
        for(Trace ray:traces){ray.active=false;ray.palette=null;ray.uniform=null;ray.chunks.clear();ray.clearReplayCache();}
    }
    public void suspend(){chunkAccess.close();}
}
