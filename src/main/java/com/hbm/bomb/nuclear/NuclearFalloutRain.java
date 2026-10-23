package com.hbm.bomb.nuclear;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.explosion.LegacyExplosionMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Collection;
import java.util.LinkedHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Original delayed radial chunk sweep, ordered fallout substitutions, collapse and crater biomes. */
public final class NuclearFalloutRain extends NuclearChunkloadingEntity implements NuclearWork,com.hbm.entity.LegacyTrackingRange {
 private static final EntityDataAccessor<Integer> SCALE=SynchedEntityData.defineId(NuclearFalloutRain.class,EntityDataSerializers.INT);
 private boolean firstTick=true;
 private int tickDelay=NuclearConfig.FALLOUT_DELAY.get();
 private final List<Long> chunks=new ArrayList<>(),outerChunks=new ArrayList<>();
 private final LinkedHashSet<Long> gatheredInner=new LinkedHashSet<>(),gatheredOuter=new LinkedHashSet<>();
 private int gatherDistance,gatherAngle;
 private boolean gatheringOuter=true;
 private java.util.UUID sourceBlast;
 private int blastRadius,scanInner,scanOuter;
 private boolean waitingForBlast;
 private final NuclearChunkAccess chunkAccess=new NuclearChunkAccess();
 private FalloutWork work;
 private static final class FalloutWork {
  final long packed;final boolean edge;
  final LinkedHashMap<Integer,Holder<Biome>> biomes=new LinkedHashMap<>();
  int column,y,depth;boolean started,legacyBiomes;LevelChunk chunk;
  FalloutWork(long packed,boolean edge){this.packed=packed;this.edge=edge;}
 }
 @Override public int legacyTrackingRange(){return 1000;}
 public NuclearFalloutRain(EntityType<? extends NuclearFalloutRain> type,Level level){super(type,level);this.noCulling=true;}
 @Override public void remove(RemovalReason reason){chunkAccess.close();super.remove(reason);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(SCALE,0);}
 public void setScale(int value){entityData.set(SCALE,value);}
 public int getScale(){int scale=entityData.get(SCALE);return scale==0?1:scale;}
 public void followBlast(NuclearExplosionMK5 blast){sourceBlast=blast.getUUID();blastRadius=blast.length;}
 private boolean waitingForSourceBlast(){
  if(sourceBlast==null||!(level() instanceof ServerLevel server))return false;
  var source=server.getEntity(sourceBlast);
  return source instanceof NuclearExplosionMK5&&!source.isRemoved();
 }
 @Override public boolean hasNuclearWork(){
  if(waitingForSourceBlast())return false;
  if(firstTick)return true;
  if(tickDelay>0)return false;
  if(!waitingForBlast||sourceBlast==null)return true;
  var source=level() instanceof ServerLevel server?server.getEntity(sourceBlast):null;
  return source==null||source.isRemoved()||source instanceof NuclearExplosionMK5 blast&&blast.isTracingComplete();
 }
 @Override public void tick(){
  if(!(level() instanceof ServerLevel server))return;
  // Older saves already contain linked rain spawned ahead of the source blast.
  // Wait before gathering, requesting chunks or taking any nuclear tick budget;
  // keep the saved queue, active column and original fallout delay unchanged.
  if(waitingForSourceBlast())return;
  //Waiting for the source's delay is not terrain work and must advance even if a
  //large blast consumed this tick's wall-clock budget before this entity ticks.
  if(!firstTick&&tickDelay>0){tickDelay--;return;}
  long deadline=NuclearTickBudget.deadline(level(),this);
  if(System.nanoTime()>=deadline)return;
  if(firstTick){
   if(!chunks.isEmpty()||!outerChunks.isEmpty())firstTick=false;
   else if(gatherChunks(deadline))firstTick=false;
   else return;
  }
  if(tickDelay>0){tickDelay--;return;}
  while(System.nanoTime()<deadline){
   if(work==null){
    if(chunks.isEmpty()&&outerChunks.isEmpty()){discard();return;}
    work=nextReady(server,deadline);
    if(work==null)return;
   }
   if(!processChunk(server,deadline))break;
   (work.edge?outerChunks:chunks).removeLast();work=null;
  }
  tickDelay=Math.max(0,NuclearConfig.FALLOUT_DELAY.get()-1);
 }
 private FalloutWork nextReady(ServerLevel server,long deadline){
  for(int outer=0;outer<2;outer++){
   boolean edge=outer!=0;var queue=edge?outerChunks:chunks;
   if(queue.isEmpty())continue;
   int start=Math.floorMod(edge?scanOuter:scanInner,queue.size());
   for(int checked=0;checked<queue.size();checked++){
    int index=Math.floorMod(queue.size()-1-start-checked,queue.size());long packed=queue.get(index);
    if(System.nanoTime()>=deadline){if(edge)scanOuter=start+checked;else scanInner=start+checked;return null;}
    if(!ready(server,packed))continue;
    //Keep removal O(1); a deferred center must not prevent safe outer terrain from progressing.
    if(index!=queue.size()-1){long last=queue.getLast();queue.set(index,last);queue.set(queue.size()-1,packed);}
    waitingForBlast=false;
    if(edge)scanOuter=0;else scanInner=0;
    return new FalloutWork(packed,edge);
   }
   if(edge)scanOuter=0;else scanInner=0;
  }
  waitingForBlast=true;return null;
 }
 private boolean ready(ServerLevel server,long packed){
  if(sourceBlast==null)return true;
  int cx=ChunkPos.getX(packed),cz=ChunkPos.getZ(packed);
  double dx=Math.max(Math.max((cx<<4)-getX(),getX()-((cx<<4)+15)),0),dz=Math.max(Math.max((cz<<4)-getZ(),getZ()-((cz<<4)+15)),0);
  //No original ray can reach a chunk wholly beyond its maximum horizontal length.
  if(dx*dx+dz*dz>(blastRadius+1D)*(blastRadius+1D))return true;
  var source=server.getEntity(sourceBlast);
  return source==null||source.isRemoved()||source instanceof NuclearExplosionMK5 blast&&blast.isTerrainComplete(cx,cz);
 }
 private boolean gatherChunks(long deadline){
  int range=getScale(),angles=20*range/32,operations=0;
  while(gatheringOuter&&gatherAngle<=angles){
   if((operations++&127)==0&&System.nanoTime()>=deadline)return false;
   var vector=LegacyVector.createVectorHelper(range,0,0);vector.rotateAroundY((float)(gatherAngle*Math.PI/180D/(angles/360D)));
   gatheredOuter.add(ChunkPos.asLong((int)(getX()+vector.xCoord)>>4,(int)(getZ()+vector.zCoord)>>4));gatherAngle++;
  }
  if(gatheringOuter){gatheringOuter=false;gatherAngle=0;}
  while(gatherDistance<=range){
   while(gatherAngle<=angles){
    if((operations++&127)==0&&System.nanoTime()>=deadline)return false;
    var vector=LegacyVector.createVectorHelper(gatherDistance,0,0);vector.rotateAroundY((float)(gatherAngle*Math.PI/180D/(angles/360D)));
    long coord=ChunkPos.asLong((int)(getX()+vector.xCoord)>>4,(int)(getZ()+vector.zCoord)>>4);
    if(!gatheredOuter.contains(coord))gatheredInner.add(coord);gatherAngle++;
   }
   gatherDistance+=8;gatherAngle=0;
  }
  chunks.addAll(gatheredInner);outerChunks.addAll(gatheredOuter);Collections.reverse(chunks);Collections.reverse(outerChunks);
  gatheredInner.clear();gatheredOuter.clear();return true;
 }
 private boolean processChunk(ServerLevel server,long deadline){
  int cx=ChunkPos.getX(work.packed),cz=ChunkPos.getZ(work.packed);
  // Source notifications inspect neighbouring states. Prepare their one-chunk
  // halo through the existing asynchronous access before retaining UPDATE_ALL;
  // a border notification must never synchronously generate a cold neighbour.
  var ready=chunkAccess.getWithNeighbors(server,cx,cz);
  if(ready==null)return false;
  work.chunk=ready;
  if(work.legacyBiomes){
   work.biomes.clear();
   for(int column=0;column<work.column;column++){
    int x=(cx<<4)+(column>>4),z=(cz<<4)+(column&15);double distance=Math.hypot(x-getX(),z-getZ());
    if(work.edge&&distance>getScale())continue;
    var biome=NuclearCraterBiomes.change(server,distance*100/getScale(),getScale(),server.getBiome(new BlockPos(x,0,z)));
    if(biome!=null)work.biomes.put((x&15)|((z&15)<<4),biome);
   }
   work.legacyBiomes=false;
  }
  while(work.column<256){
   if(System.nanoTime()>=deadline)return false;
   int x=(cx<<4)+(work.column>>4),z=(cz<<4)+(work.column&15);
   double distance=Math.hypot(x-getX(),z-getZ());
   if(work.edge&&distance>getScale()){work.column++;continue;}
   double percent=distance*100/getScale();
   if(!work.started){
    // Everything above WORLD_SURFACE was skipped as air by the original loop.
    work.y=work.chunk.getHeight(Heightmap.Types.WORLD_SURFACE,x&15,z&15);work.depth=0;work.started=true;
   }
   if(!stomp(server,x,z,percent,deadline))return false;
   int cell=(x&15)|((z&15)<<4);
   var previous=work.biomes.getOrDefault(cell,server.getBiome(new BlockPos(x,0,z)));
   var biome=NuclearCraterBiomes.change(server,percent,getScale(),previous);if(biome!=null)work.biomes.put(cell,biome);
   work.started=false;work.column++;
  }
  if(!work.biomes.isEmpty()){
   for(var change:work.biomes.entrySet())NuclearCraterBiomes.column(work.chunk,(cx<<4)+(change.getKey()&15),(cz<<4)+(change.getKey()>>4),change.getValue());
   NuclearCraterBiomes.sync(server,work.chunk);
  }
  return true;
 }
 private boolean stomp(ServerLevel world,int x,int z,double distance,long deadline){
  while(work.y>=world.getMinBuildHeight()&&work.depth<3){
   if(System.nanoTime()>=deadline)return false;
   int y=work.y--;var pos=new BlockPos(x,y,z);var original=world.getBlockState(pos);
   if(LegacyMaterials.of(original).name().equals("air")||original.is(NuclearTerrain.FALLOUT.get()))continue;
   if(FalloutRules.matches(original,"volcano_core",-1)){world.setBlock(pos,FalloutRules.state("volcano_rad_core",FalloutRules.metadata(original)),3);continue;}
   var above=pos.above();var top=world.getBlockState(above);int metadata=FalloutRules.metadata(original);
   if(work.depth==0&&(top.is(Blocks.AIR)||(LegacyMaterials.of(top).isReplaceable()&&!LegacyMaterials.of(top).isLiquid()))){double d=distance/100D,chance=.1-Math.pow(d-.7,2);var layer=NuclearTerrain.FALLOUT.get().defaultBlockState();if(chance>=random.nextDouble()&&layer.canSurvive(world,above))world.setBlock(above,layer,3);}
   if(distance<65&&original.isFlammable(world,pos,Direction.UP)&&random.nextInt(5)==0&&world.isEmptyBlock(above))world.setBlock(above,Blocks.FIRE.defaultBlockState(),3);
   boolean evaluated=false;for(var entry:FalloutRules.entries)if(entry.evaluate(world,pos,original,metadata,distance)){if(entry.isSolid())work.depth++;evaluated=true;break;}
   float hardness=original.getDestroySpeed(world,pos);
   if(y>world.getMinBuildHeight()&&distance<65&&hardness<=Blocks.STONE_BRICKS.getExplosionResistance()&&hardness>=0&&!(original.getBlock() instanceof MultiBlockHost||original.getBlock() instanceof com.hbm.multiblock.DummyableBlock)&&world.getBlockState(pos.below()).is(Blocks.AIR))for(int i=0;i<=work.depth;i++){
    var at=pos.above(i);var falling=world.getBlockState(at);hardness=falling.getDestroySpeed(world,at);if(hardness<=Blocks.STONE_BRICKS.getExplosionResistance()&&hardness>=0){var entity=new NuclearFallingBlock(world,x+.5D,y+.5D+i,z+.5D,falling);entity.canDrop=false;world.addFreshEntity(entity);}
   }
   if(!evaluated&&original.isSolidRender(world,pos)&&LegacyMaterials.of(original).blocksMovement())work.depth++;
  }
  return true;
 }

 /** Original BlockDummyable implementations expose their host identity through this shared marker. */
 public interface MultiBlockHost{}
 @Override protected void addAdditionalSaveData(CompoundTag tag){
  saveTicket(tag);tag.putInt("scale",getScale());tag.putIntArray("chunks",write(chunks));tag.putIntArray("outerChunks",write(outerChunks));
  if(sourceBlast!=null){tag.putUUID("sourceBlast",sourceBlast);tag.putInt("blastRadius",blastRadius);}
  tag.putInt("tickDelay",tickDelay);tag.putBoolean("gathering",firstTick);tag.putBoolean("gatheringOuter",gatheringOuter);tag.putInt("gatherDistance",gatherDistance);tag.putInt("gatherAngle",gatherAngle);
  tag.putIntArray("gatheredInner",write(gatheredInner));tag.putIntArray("gatheredOuter",write(gatheredOuter));
  if(work!=null){
   var saved=new CompoundTag();saved.putLong("chunk",work.packed);saved.putBoolean("edge",work.edge);saved.putInt("column",work.column);saved.putInt("y",work.y);saved.putInt("depth",work.depth);saved.putBoolean("started",work.started);saved.putInt("biomeColumns",work.legacyBiomes?16:256);
   var biomes=new ListTag();work.biomes.forEach((cell,biome)->{var entry=new CompoundTag();entry.putInt("cell",cell);entry.putString("biome",biome.unwrapKey().orElseThrow().location().toString());biomes.add(entry);});saved.put("biomes",biomes);tag.put("work",saved);
  }
 }
 @Override protected void readAdditionalSaveData(CompoundTag tag){
  loadTicket(tag);setScale(tag.getInt("scale"));read(tag.getIntArray("chunks"),chunks);read(tag.getIntArray("outerChunks"),outerChunks);
  if(tag.hasUUID("sourceBlast")){sourceBlast=tag.getUUID("sourceBlast");blastRadius=tag.getInt("blastRadius");}
  if(tag.contains("gathering")){firstTick=tag.getBoolean("gathering");tickDelay=Math.max(0,tag.getInt("tickDelay"));gatheringOuter=tag.getBoolean("gatheringOuter");gatherDistance=tag.getInt("gatherDistance");gatherAngle=tag.getInt("gatherAngle");read(tag.getIntArray("gatheredInner"),gatheredInner);read(tag.getIntArray("gatheredOuter"),gatheredOuter);}
  if(tag.contains("work")){
   var saved=tag.getCompound("work");work=new FalloutWork(saved.getLong("chunk"),saved.getBoolean("edge"));work.column=saved.getInt("column");work.y=saved.getInt("y");work.depth=saved.getInt("depth");work.started=saved.getBoolean("started");work.legacyBiomes=saved.getInt("biomeColumns")!=256;
   var biomes=saved.getList("biomes",Tag.TAG_COMPOUND);for(int i=0;i<biomes.size();i++){var entry=biomes.getCompound(i);var biome=level().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(ResourceKey.create(Registries.BIOME,ResourceLocation.parse(entry.getString("biome"))));work.biomes.put(entry.getInt("cell"),biome);}
  }
 }
 private static int[] write(Collection<Long> list){int[] result=new int[list.size()*2];int i=0;for(long value:list){result[i++]=ChunkPos.getX(value);result[i++]=ChunkPos.getZ(value);}return result;}
 private static void read(int[] data,Collection<Long> list){for(int i=0;i+1<data.length;i+=2)list.add(ChunkPos.asLong(data[i],data[i+1]));}
}
