package com.hbm.bomb.nuclear;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.entity.LegacyFallingBlockEntity;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.HbmExposure;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.BasicBlockItem;
import com.hbm.particle.LegacyParticles;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Real X5846 fallout terrain, including its mushroom growth and falling dead leaves. */
public final class NuclearTerrain {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final Map<String,DeferredBlock<? extends Block>> ALL=new LinkedHashMap<>();
 private static final List<Supplier<? extends Item>> CREATIVE=new ArrayList<>();
 public static final DeferredBlock<Earth> EARTH=block("waste_earth",()->new Earth(false),"WasteEarth",true);
 public static final DeferredBlock<Earth> MYCELIUM=block("waste_mycelium",()->new Earth(true),"WasteEarth",true);
 public static final DeferredBlock<Trinitite> TRINITITE=block("waste_trinitite",()->new Trinitite("waste_trinitite"),"BlockTrinitite",true);
 public static final DeferredBlock<Trinitite> RED_TRINITITE=block("waste_trinitite_red",()->new Trinitite("waste_trinitite_red"),"BlockTrinitite",true);
 public static final DeferredBlock<DeadLeaves> LEAVES=block("waste_leaves",DeadLeaves::new,"WasteLeaves",true);
 public static final DeferredBlock<Layer> LEAF_LAYER=block("leaves_layer",()->new Layer(false),"BlockLayering",true);
 public static final DeferredBlock<Layer> FALLOUT=block("fallout",()->new Layer(true),"BlockFallout",true);
 public static final DeferredBlock<Mush> MUSH=block("mush",Mush::new,"BlockMush",true);
 public static final DeferredBlock<HugeMush> CAP=block("mush_block",HugeMush::new,"BlockMushHuge",false);
 public static final DeferredBlock<HugeMush> STEM=block("mush_block_stem",HugeMush::new,"BlockMushHuge",false);
 public static final DeferredBlock<SlakedBedrock> BEDROCK=block("sellafield_bedrock",SlakedBedrock::new,"BlockSellafieldSlaked",true);
 private static <B extends Block> DeferredBlock<B> block(String id,Supplier<B> factory,String original,boolean creative){
  var block=BLOCKS.register(id,factory);ALL.put(id,block);
  // ModItems.fallout is the dust. Its original ItemBlock shares the old textual name in a separate registry.
  var item=ITEMS.register(id.equals("fallout")?"fallout_block":id,()->new BasicBlockItem(block.get(),new Item.Properties(),original,id));if(creative)CREATIVE.add(item);return block;
 }
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static Map<String,DeferredBlock<? extends Block>> blocks(){return java.util.Collections.unmodifiableMap(ALL);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?CREATIVE.stream().map(item->new ItemStack(item.get())).toList():List.of();}
 private static boolean silk(LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);return tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),tool)>0;}
 private static void radiation(Entity entity,int ticks,int amplifier,boolean curable){if(entity instanceof LivingEntity living){var effect=new MobEffectInstance(ExposureEffects.RADIATION,ticks,amplifier);if(!curable)effect.getCures().clear();living.addEffect(effect);}}
 public static boolean layerSupport(BlockState below,LevelReader level,BlockPos pos,Block block){
  if(below.is(Blocks.ICE)||below.is(Blocks.PACKED_ICE))return false;
  if(!below.isAir()&&(below.is(BlockTags.LEAVES)||LegacyMaterials.of(below).name().equals("leaves")))return true;
  if(below.is(block)&&(com.hbm.explosion.LegacyExplosionMath.metadata(below)&7)==7)return true;
  return below.isSolidRender(level,pos)&&LegacyMaterials.of(below).blocksMovement();
 }
 /** Future reactor debris implementations implement the original BlockLayering support exception. */
 public interface DebrisSupport{}
 /** The source's unbreakable slaked variant is a distinct block, including its 16 irradiation shades. */
 public static final class SlakedBedrock extends Block {
  public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
  SlakedBedrock(){super(Properties.of().mapColor(MapColor.STONE).strength(-1F,3_600_000F).sound(SoundType.STONE).isValidSpawn((state,level,pos,type)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
  @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
 }
 public static final class Earth extends Block {
  final boolean mycelium;
  Earth(boolean mycelium){super(Properties.of().mapColor(MapColor.DIRT).strength(.6F).sound(SoundType.GRASS).randomTicks().lightLevel(state->mycelium?15:0));this.mycelium=mycelium;}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(silk(context)?this:Blocks.DIRT));}
  @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){if(mycelium)radiation(entity,600,3,true);}
  @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(mycelium)level.addParticle(LegacyParticles.AURA.get(),pos.getX()+random.nextFloat(),pos.getY()+1.1F,pos.getZ()+random.nextFloat(),0,0,0);}
  @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
   if(mycelium&&NuclearConfig.MYCELIUM.get())for(int x=-1;x<2;x++)for(int y=-1;y<2;y++)for(int z=-1;z<2;z++){var at=pos.offset(x,y,z);var base=level.getBlockState(at);if(!level.getBlockState(at.above()).isSolidRender(level,at.above())&&(base.is(Blocks.DIRT)||base.is(Blocks.GRASS_BLOCK)||base.is(Blocks.MYCELIUM)||base.is(EARTH.get())))level.setBlock(at,MYCELIUM.get().defaultBlockState(),3);}
   var above=pos.above();var top=level.getBlockState(above);
   if(NuclearConfig.CLEANUP_DEAD_DIRT.get()||(level.getMaxLocalRawBrightness(above)<4&&top.getLightBlock(level,above)>2))level.setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
   if(top.getBlock() instanceof MushroomBlock)level.setBlock(above,MUSH.get().defaultBlockState(),3);
  }
  @Override public TriState canSustainPlant(BlockState state,BlockGetter world,BlockPos pos,Direction face,BlockState plant){return plant.getBlock() instanceof MushroomBlock||plant.getBlock() instanceof Mush?TriState.TRUE:TriState.FALSE;}
 }
 public static final class Trinitite extends LegacyOreBlock {
  Trinitite(String id){super(new OreSpec(id,"BlockTrinitite","sand","sand",.5F,1.5F,0,false,true,"trinitite",null,0,1,0,"NONE",0,List.of(),"COMMON",true,0,false,false));}
  @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){radiation(entity,100,2,true);}
  @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(random.nextInt(5)==0)level.addParticle(LegacyParticles.AURA.get(),pos.getX()+random.nextFloat(),pos.getY()+1.1F,pos.getZ()+random.nextFloat(),0,0,0);}
 }
 public static final class DeadLeaves extends Block {
  DeadLeaves(){super(Properties.of().mapColor(MapColor.PLANT).strength(.1F).sound(SoundType.GRASS).randomTicks().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false).isValidSpawn((s,w,p,t)->false));}
  @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
  @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
  @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
  @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){if(random.nextInt(30)==0){level.removeBlock(pos,false);if(level.isEmptyBlock(pos.below())){var falling=new LegacyFallingBlockEntity(level,pos,LEAF_LAYER.get().defaultBlockState());falling.time=2;falling.dropItem=false;level.addFreshEntity(falling);}}}
  @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(random.nextInt(7)==0&&level.isEmptyBlock(pos.below()))com.hbm.client.bomb.nuclear.NuclearDeadLeaf.spawn(level,pos.getX()+random.nextDouble(),pos.getY()-.05,pos.getZ()+random.nextDouble());}
 }
 public static final class Layer extends Block {
  public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
  final boolean fallout;
  Layer(boolean fallout){super(Properties.of().mapColor(MapColor.SAND).strength(.1F).sound(fallout?SoundType.GRAVEL:SoundType.GRASS).noOcclusion().replaceable().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));this.fallout=fallout;registerDefaultState(stateDefinition.any().setValue(META,0));}
  @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
  @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
  @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
  @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
  @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return box(0,0,0,16,fallout?2:2*((state.getValue(META)&7)+1),16);}
  @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){int height=fallout?2:2*(state.getValue(META)&7);return height==0?Shapes.empty():box(0,0,0,16,height,16);}
  @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var below=level.getBlockState(pos.below());return !fallout&&below.getBlock() instanceof DebrisSupport||layerSupport(below,level,pos.below(),this);}
  @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos other,boolean moving){if(!canSurvive(state,level,pos))level.removeBlock(pos,false);}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return fallout?List.of(new ItemStack(HbmContent.item("fallout"))):List.of();}
  @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){if(fallout&&!level.isClientSide&&!(entity instanceof Player p&&p.isCreative()))radiation(entity,12000,0,false);}
  @Override protected void attack(BlockState state,Level level,BlockPos pos,Player player){if(fallout)HbmExposure.addContamination(player,1F,200,false);}
 }
 public static final class Mush extends Block implements BonemealableBlock {
  Mush(){super(Properties.of().mapColor(MapColor.PLANT).strength(0).sound(SoundType.GRASS).lightLevel(s->7).noCollission().noOcclusion().randomTicks());}
  @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return box(4.8,0,4.8,11.2,6.4,11.2);}
  boolean canGrowHere(LevelReader level,BlockPos pos){var below=level.getBlockState(pos.below());for(String id:List.of("waste_earth","waste_mycelium","waste_trinitite","waste_trinitite_red","block_waste","block_waste_painted","block_waste_vitrified"))if(FalloutRules.matches(below,id,-1))return true;return false;}
  @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){if(level.isOutsideBuildHeight(pos))return false;var below=level.getBlockState(pos.below());var decision=below.canSustainPlant(level,pos.below(),Direction.UP,state);return (decision.isDefault()?below.isFaceSturdy(level,pos.below(),Direction.UP):decision.isTrue())||canGrowHere(level,pos);}
  void check(Level level,BlockPos pos){if(!canSurvive(defaultBlockState(),level,pos)){dropResources(defaultBlockState(),level,pos);level.removeBlock(pos,false);}}
  @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos other,boolean moving){check(level,pos);}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
  @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
   check(level,pos);int x=pos.getX(),y=pos.getY(),z=pos.getZ();
   if(NuclearConfig.MYCELIUM.get()&&level.getBlockState(pos.below()).is(EARTH.get())&&random.nextInt(5)==0)level.setBlock(pos.below(),MYCELIUM.get().defaultBlockState(),3);
   if(random.nextInt(25)!=0)return;int remaining=3;
   // Keep the source's explicit x/z/y axis order in mushroom population sampling and wandering.
   for(int ix=x-4;ix<=x+4;ix++)for(int iy=y-4;iy<=y+4;iy++)for(int iz=z-1;iz<=z+1;iz++)if(level.getBlockState(new BlockPos(ix,iz,iy)).is(this)&&--remaining<=0)return;
   int ix=x+random.nextInt(5)-2,iy=z+random.nextInt(2)-random.nextInt(2),iz=y+random.nextInt(5)-2;
   for(int i=0;i<4;i++){var at=new BlockPos(ix,iy,iz);if(level.isEmptyBlock(at)&&canGrowHere(level,at)){x=ix;z=iy;y=iz;}ix=x+random.nextInt(5)-2;iy=z+random.nextInt(2)-random.nextInt(2);iz=y+random.nextInt(5)-2;}
   var at=new BlockPos(ix,iy,iz);if(level.isEmptyBlock(at)&&canGrowHere(level,at))level.setBlock(at,defaultBlockState(),2);
  }
  @Override public boolean isValidBonemealTarget(LevelReader level,BlockPos pos,BlockState state){return canSurvive(state,level,pos);}
  @Override public boolean isBonemealSuccess(Level level,RandomSource random,BlockPos pos,BlockState state){return random.nextFloat()<.4D;}
  @Override public void performBonemeal(ServerLevel level,RandomSource random,BlockPos pos,BlockState state){level.removeBlock(pos,false);int[] ys={0,3,5,6,7,8,9,10},radii={1,1,2,4,4,4,3,1};for(int i=0;i<ys.length;i++)for(int x=-radii[i];x<=radii[i];x++)for(int z=-radii[i];z<=radii[i];z++)level.setBlock(pos.offset(x,ys[i],z),CAP.get().defaultBlockState(),3);for(int y=0;y<8;y++)level.setBlock(pos.above(y),STEM.get().defaultBlockState(),3);}
 }
 public static final class HugeMush extends Block {
  HugeMush(){super(Properties.of().mapColor(MapColor.PLANT).strength(.2F).sound(SoundType.GRASS).lightLevel(s->15).forceSolidOff());}
  @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){if(silk(context))return List.of(new ItemStack(this));int amount=Math.max(0,context.getLevel().random.nextInt(10)-7);return amount==0?List.of():List.of(new ItemStack(MUSH.get(),amount));}
  @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(MUSH.get());}
 }
 private NuclearTerrain(){}
}
