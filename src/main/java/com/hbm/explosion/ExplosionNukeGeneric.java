package com.hbm.explosion;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.damage.HbmDamageSources;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.registry.HbmContent;
import java.util.Random;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

/** Original shared nuclear entity damage and both independent waste conversion variants. */
public final class ExplosionNukeGeneric {
 private static final Random random=new Random();
 private static final int QUIET_UPDATE=Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE;
 private ExplosionNukeGeneric(){}
 public static void dealDamage(Level world,double x,double y,double z,double radius){dealDamage(world,x,y,z,radius,250F);}
 public static void dealDamage(Level world,double x,double y,double z,double radius,float maxDamage){
  BlastWorld visibility=null;
  Vec3 origin=new Vec3(x,y,z);
  net.minecraft.world.damagesource.DamageSource source=null;
  for(Entity entity:world.getEntities((Entity)null,new AABB(x,y,z,x,y,z).inflate(radius))){
   double dist=(double)(float)Math.sqrt(entity.distanceToSqr(x,y,z));
   if(dist>radius||entity instanceof Ocelot||entity instanceof ExplosionKnockbackExempt||entity instanceof ExplosionNuclearExempt||entity instanceof ServerPlayer player&&player.isCreative())continue;
   if(visibility==null)visibility=new BlastWorld(world);
   if(isBlastObstructed(visibility,origin,new Vec3(entity.getX(),entity.getEyeY(),entity.getZ())))continue;
   boolean knockback=true;float amount=(float)(maxDamage*(radius-dist)/radius);
   if(source==null)source=HbmDamageSources.source(world,HbmDamageSources.nuclearBlast);
   if(entity instanceof LivingEntity living&&entity.isAlive()){knockback=LegacyEntityDamage.attackEntityFromNT(living,source,amount,true,true,0,100F,0);if(!living.isAlive())ExplosionDeathEffects.decide(living,source);}else entity.hurt(source,amount);
   entity.igniteForSeconds(5);
   if(knockback){double kx=entity.getX()-x,ky=entity.getEyeY()-y,kz=entity.getZ()-z,len=(double)(float)Math.sqrt(kx*kx+ky*ky+kz*kz);Vec3 vector=len<1E-4D?Vec3.ZERO:new Vec3(kx/len,ky/len,kz/len);entity.setDeltaMovement(entity.getDeltaMovement().add(vector.scale(.2D)));}
  }
 }
 /** Library.isObstructed used 1.7.10's initial block plus at most 201 DDA
  * advances (while(counter-- >= 0), starting at 200). Modern Level.clip has
  * no such bound and reads a fluid palette even with Fluid.NONE. A large blast
  * otherwise repeats thousands of useless air/fluid lookups per entity/tick. */
 private static boolean isBlastObstructed(BlastWorld world,Vec3 from,Vec3 to){
  if(!Double.isFinite(from.x)||!Double.isFinite(from.y)||!Double.isFinite(from.z)
    ||!Double.isFinite(to.x)||!Double.isFinite(to.y)||!Double.isFinite(to.z))return false;
  var query=new BlastVisibility(world,from,to);
  return BlockGetter.traverseBlocks(from,to,query,(ray,pos)->ray.test(pos),ray->false);
 }
 /** One synchronous damage sweep shares real chunk handles between its rays.
  * Only handles are cached: every voxel still reads the live mutable palette,
  * so earlier entity damage callbacks/terrain changes remain visible. This
  * scope never survives the call or skips original DDA samples/shape queries. */
 private static final class BlastWorld {
  final Level world;
  final Long2ObjectOpenHashMap<LevelChunk> chunks=new Long2ObjectOpenHashMap<>();
  BlastWorld(Level world){this.world=world;}
  LevelChunk chunk(int x,int z){
   long key=ChunkPos.asLong(x,z);var chunk=chunks.get(key);
   if(chunk==null){chunk=world.getChunk(x,z);chunks.put(key,chunk);}
   return chunk;
  }
 }
 private static final class BlastVisibility {
  private final BlastWorld access;
  private final Level world;
  private final ClipContext context;
  private LevelChunk chunk;
  private LevelChunkSection section;
  private int samples,chunkX,chunkZ,sectionIndex;
  private BlastVisibility(BlastWorld access,Vec3 from,Vec3 to){
   this.access=access;this.world=access.world;context=new ClipContext(from,to,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,CollisionContext.empty());
  }
  private Boolean test(BlockPos pos){
   if(samples++>=202)return false;
   if(world.isOutsideBuildHeight(pos))return null;
   int x=pos.getX()>>4,z=pos.getZ()>>4;
   if(chunk==null||chunkX!=x||chunkZ!=z){chunk=access.chunk(x,z);chunkX=x;chunkZ=z;section=null;}
   int index=world.getSectionIndex(pos.getY());
   if(section==null||sectionIndex!=index){section=chunk.getSection(index);sectionIndex=index;}
   // Keep every original DDA step and its 202-sample bound, but an entirely
   // empty live section needs no palette lookup for each repeated air voxel.
   if(section.hasOnlyAir())return null;
   BlockState state=section.getBlockState(pos.getX()&15,pos.getY()&15,pos.getZ()&15);
   if(state.isAir())return null;
   Block block=state.getBlock();
   // Vanilla water/lava have an empty outline and Fluid.NONE never hits them.
   // Ordinary Block itself always returns the unit cube. Keep the actual shape
   // query for every subclass, including connected and context-dependent blocks.
   if(block==Blocks.WATER||block==Blocks.LAVA)return null;
   var shape=block.getClass()==Block.class?Shapes.block():context.getBlockShape(state,world,pos);
   if(shape.isEmpty())return null;
   var hit=world.clipWithInteractionOverride(context.getFrom(),context.getTo(),pos,shape,state);
   return hit!=null&&hit.getType()!=HitResult.Type.MISS?Boolean.TRUE:null;
  }
 }
 /** The source transmutation variant: its coal probability and surviving glass/leaves differ from NoSchrab. */
 public static void waste(Level world,int x,int y,int z,int radius){
  int r2=radius*radius,half=r2/2;
  for(int xx=-radius;xx<radius;xx++){int a=xx*xx;for(int yy=-radius;yy<radius;yy++){int b=a+yy*yy;
   for(int zz=-radius;zz<radius;zz++)if(b+zz*zz<half+world.random.nextInt(half/5)){
    var pos=new BlockPos(x+xx,y+yy,z+zz);if(!world.getBlockState(pos).is(Blocks.AIR))wasteDest(world,pos);
   }
  }}
 }
 public static void wasteDest(Level world,int x,int y,int z){wasteDest(world,new BlockPos(x,y,z));}
 public static void wasteDest(Level world,BlockPos pos){
  if(world.isClientSide)return;
  var state=world.getBlockState(pos);var key=BuiltInRegistries.BLOCK.getKey(state.getBlock());boolean vanilla=key.getNamespace().equals("minecraft");BlockState replacement=null;int flags=3;
  if(vanilla&&state.is(BlockTags.DOORS)){replacement=Blocks.AIR.defaultBlockState();flags=2;}
  else if(state.is(Blocks.GRASS_BLOCK))replacement=original("waste_earth");
  else if(state.is(Blocks.MYCELIUM))replacement=original("waste_mycelium");
  else if(state.is(Blocks.SAND)||state.is(Blocks.RED_SAND)){if(random.nextInt(20)==1)replacement=original(state.is(Blocks.SAND)?"waste_trinitite":"waste_trinitite_red");}
  else if(state.is(Blocks.CLAY))replacement=Blocks.TERRACOTTA.defaultBlockState();
  else if(state.is(Blocks.MOSSY_COBBLESTONE))replacement=Blocks.COAL_ORE.defaultBlockState();
  else if(state.is(Blocks.COAL_ORE)||state.is(Blocks.DEEPSLATE_COAL_ORE)){
   int roll=random.nextInt(10);boolean deep=state.is(Blocks.DEEPSLATE_COAL_ORE);
   if(roll==1||roll==2||roll==3)replacement=(deep?Blocks.DEEPSLATE_DIAMOND_ORE:Blocks.DIAMOND_ORE).defaultBlockState();
   if(roll==9)replacement=(deep?Blocks.DEEPSLATE_EMERALD_ORE:Blocks.EMERALD_ORE).defaultBlockState();
  }
  else if(vanilla&&state.is(BlockTags.LOGS)||state.is(Blocks.MUSHROOM_STEM))replacement=original("waste_log");
  else if(state.is(Blocks.BROWN_MUSHROOM_BLOCK)||state.is(Blocks.RED_MUSHROOM_BLOCK)){replacement=Blocks.AIR.defaultBlockState();flags=2;}
  else if(LegacyMaterials.of(state).name().equals("wood")&&state.isSolidRender(world,pos)&&!state.is(HbmContent.block("waste_log")))replacement=original("waste_planks");
  else for(String source:new String[]{"ore_uranium","ore_nether_uranium","ore_gneiss_uranium"})if(com.hbm.bomb.nuclear.FalloutRules.matches(state,source,-1)){
   boolean schrab=random.nextInt(com.hbm.bomb.nuclear.NuclearConfig.schrabOreChance())==1;
   String product=source.replace("uranium",schrab?"schrabidium":"uranium_scorched");
   replacement=com.hbm.registry.HbmDeepslateOres.forHost(state,product).defaultBlockState();break;
  }
  if(replacement!=null)world.setBlock(pos,replacement,flags==2?QUIET_UPDATE:flags);
 }
 public static void wasteNoSchrab(Level world,int x,int y,int z,int radius){int r2=radius*radius,half=r2/2;for(int xx=-radius;xx<radius;xx++){int a=xx*xx;for(int yy=-radius;yy<radius;yy++){int b=a+yy*yy;for(int zz=-radius;zz<radius;zz++)if(b+zz*zz<half+world.random.nextInt(half/5)){var pos=new BlockPos(x+xx,y+yy,z+zz);if(!world.getBlockState(pos).is(Blocks.AIR))wasteDestNoSchrab(world,pos);}}}}
 public static void wasteDestNoSchrab(Level world,int x,int y,int z){wasteDestNoSchrab(world,new BlockPos(x,y,z));}
 public static void wasteDestNoSchrab(Level world,BlockPos pos){
  if(world.isClientSide)return;var state=world.getBlockState(pos);var key=BuiltInRegistries.BLOCK.getKey(state.getBlock());String id=key.getPath();boolean vanilla=key.getNamespace().equals("minecraft");BlockState replacement=null;int flags=3;
  if(state.is(Blocks.GLASS)||vanilla&&(id.endsWith("_stained_glass")||state.is(BlockTags.DOORS)||state.is(BlockTags.LEAVES)))replacement=Blocks.AIR.defaultBlockState();
  else if(state.is(Blocks.GRASS_BLOCK))replacement=original("waste_earth");
  else if(state.is(Blocks.MYCELIUM))replacement=original("waste_mycelium");
  else if(state.is(Blocks.SAND)||state.is(Blocks.RED_SAND)){if(random.nextInt(20)==1)replacement=original(state.is(Blocks.SAND)?"waste_trinitite":"waste_trinitite_red");}
  else if(state.is(Blocks.CLAY))replacement=Blocks.TERRACOTTA.defaultBlockState();
  else if(state.is(Blocks.MOSSY_COBBLESTONE))replacement=Blocks.COAL_ORE.defaultBlockState();
  else if(state.is(Blocks.COAL_ORE)||state.is(Blocks.DEEPSLATE_COAL_ORE)){int roll=random.nextInt(30);if(roll==1||roll==2||roll==3)replacement=state.is(Blocks.DEEPSLATE_COAL_ORE)?Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState():Blocks.DIAMOND_ORE.defaultBlockState();if(roll==29)replacement=state.is(Blocks.DEEPSLATE_COAL_ORE)?Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState():Blocks.EMERALD_ORE.defaultBlockState();}
  else if(vanilla&&state.is(BlockTags.LOGS)||state.is(Blocks.MUSHROOM_STEM))replacement=original("waste_log");
  else if(vanilla&&state.is(BlockTags.PLANKS))replacement=original("waste_planks");
  else if(state.is(Blocks.BROWN_MUSHROOM_BLOCK)||state.is(Blocks.RED_MUSHROOM_BLOCK)){replacement=Blocks.AIR.defaultBlockState();flags=2;}
  if(replacement!=null)world.setBlock(pos,replacement,flags==2?QUIET_UPDATE:flags);
 }
 public static void empBlast(Level world,int x,int y,int z,int strength){ExplosionEmp.empBlast(world,x,y,z,strength);}
 public static void emp(Level world,int x,int y,int z){ExplosionEmp.emp(world,x,y,z);}
 public static void vapor(Level world,int x,int y,int z,int strength){int radius=strength*2,r2=radius*radius,half=r2/2;for(int xx=-radius;xx<radius;xx++){int a=xx*xx;for(int yy=-radius;yy<radius;yy++){int b=a+yy*yy;for(int zz=-radius;zz<radius;zz++)if(b+zz*zz<half)vaporDest(world,x+xx,y+yy,z+zz);}}}
 public static int destruction(Level world,int x,int y,int z){
  if(world.isClientSide)return 0;var pos=new BlockPos(x,y,z);var state=world.getBlockState(pos);float resistance=state.getBlock().getExplosionResistance();
  if(resistance>=200F){
   int protection=(int)(resistance/300F);
   if(com.hbm.bomb.nuclear.FalloutRules.matches(state,"brick_concrete",-1)){if(random.nextInt(8)==0){world.setBlock(pos,Blocks.GRAVEL.defaultBlockState(),3);return 0;}}
   else if(com.hbm.bomb.nuclear.FalloutRules.matches(state,"brick_light",-1)){int roll=random.nextInt(3);if(roll==0){world.setBlock(pos,original("waste_planks"),3);return 0;}if(roll==1){world.setBlock(pos,original("block_scrap"),3);return 0;}}
   else if(com.hbm.bomb.nuclear.FalloutRules.matches(state,"brick_obsidian",-1)){if(random.nextInt(20)==0)world.setBlock(pos,Blocks.OBSIDIAN.defaultBlockState(),3);}
   else if(state.is(Blocks.OBSIDIAN)){world.setBlock(pos,original("gravel_obsidian"),3);return 0;}
   else if(random.nextInt(protection+3)==0)world.setBlock(pos,original("block_scrap"),3);
   return protection;
  }
  world.setBlock(pos,Blocks.AIR.defaultBlockState(),QUIET_UPDATE);return 0;
 }
 public static int vaporDest(Level world,int x,int y,int z){
  if(world.isClientSide)return 0;var pos=new BlockPos(x,y,z);var state=world.getBlockState(pos);float resistance=state.getBlock().getExplosionResistance();
  if(resistance<.5F||state.is(Blocks.COBWEB)||com.hbm.bomb.nuclear.FalloutRules.matches(state,"red_cable",-1)||state.getBlock() instanceof net.minecraft.world.level.block.LiquidBlock){world.setBlock(pos,Blocks.AIR.defaultBlockState(),QUIET_UPDATE);return 0;}
  if(resistance<=3F&&!state.isSolidRender(world,pos)&&!state.is(Blocks.CHEST)&&!state.is(Blocks.FARMLAND)){world.setBlock(pos,Blocks.AIR.defaultBlockState(),QUIET_UPDATE);return 0;}
  if(state.isFlammable(world,pos,net.minecraft.core.Direction.UP)&&world.getBlockState(pos.above()).is(Blocks.AIR))world.setBlock(pos.above(),Blocks.FIRE.defaultBlockState(),QUIET_UPDATE);
  return (int)(resistance/300F);
 }
 public static void solinium(Level world,int x,int y,int z){
  if(world.isClientSide)return;var pos=new BlockPos(x,y,z);var state=world.getBlockState(pos);
  if(state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.MYCELIUM)||com.hbm.bomb.nuclear.FalloutRules.matches(state,"waste_earth",-1)||com.hbm.bomb.nuclear.FalloutRules.matches(state,"waste_mycelium",-1)){world.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);return;}
  String material=LegacyMaterials.of(state).name();
  if(material.equals("cactus")||material.equals("coral")||material.equals("leaves")||material.equals("plants")||material.equals("sponge")||material.equals("vine")||material.equals("gourd")||material.equals("wood"))world.removeBlock(pos,false);
 }
 private static BlockState original(String symbol){return HbmContent.block(symbol).defaultBlockState();}
}
