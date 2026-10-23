package com.hbm.bomb.nuclear;

import com.hbm.exposure.ExposureEffects;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original Balefire propagation; generation age only changes when the fire spreads. */
public final class NuclearBalefireBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public NuclearBalefireBlock(){super(Properties.of().mapColor(MapColor.FIRE).strength(0).lightLevel(s->15).noCollission().noOcclusion().replaceable().randomTicks().noLootTable());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
 private static boolean catches(LevelReader level,BlockPos pos,Direction face){return level.getBlockState(pos).isFlammable(level,pos,face);}
 private static boolean neighborBurns(LevelReader level,BlockPos pos){return catches(level,pos.east(),Direction.WEST)||catches(level,pos.west(),Direction.EAST)||catches(level,pos.below(),Direction.UP)||catches(level,pos.above(),Direction.DOWN)||catches(level,pos.north(),Direction.SOUTH)||catches(level,pos.south(),Direction.NORTH);}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP)||neighborBurns(level,pos);}
 @Override protected void onPlace(BlockState state,Level world,BlockPos pos,BlockState previous,boolean moving){if(previous.is(this))return;if(world instanceof ServerLevel&&(world.dimension()==Level.OVERWORLD||world.dimension()==Level.NETHER)){var portal=PortalShape.findEmptyPortalShape(world,pos,Direction.Axis.X);if(portal.isPresent()){portal.get().createPortalBlocks();return;}}if(!state.canSurvive(world,pos))world.removeBlock(pos,false);else world.scheduleTick(pos,this,30+world.random.nextInt(10));}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos other,boolean moving){if(!state.canSurvive(level,pos))level.removeBlock(pos,false);}
 @Override protected void randomTick(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){tick(state,world,pos,random);}
 @Override protected void tick(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){
  if(!world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK))return;
  if(!state.canSurvive(world,pos))world.removeBlock(pos,false);
  int meta=world.getBlockState(pos).hasProperty(META)?world.getBlockState(pos).getValue(META):0;
  if(meta<15)world.scheduleTick(pos,this,30+random.nextInt(10));
  if(!neighborBurns(world,pos)&&!world.getBlockState(pos.below()).isFaceSturdy(world,pos.below(),Direction.UP)){world.removeBlock(pos,false);return;}
  if(meta>=15)return;
  catchFire(world,pos.east(),500,random,meta,Direction.WEST);catchFire(world,pos.west(),500,random,meta,Direction.EAST);catchFire(world,pos.below(),300,random,meta,Direction.UP);catchFire(world,pos.above(),300,random,meta,Direction.DOWN);catchFire(world,pos.north(),500,random,meta,Direction.SOUTH);catchFire(world,pos.south(),500,random,meta,Direction.NORTH);
  for(int x=pos.getX()-3;x<=pos.getX()+3;x++)for(int z=pos.getZ()-3;z<=pos.getZ()+3;z++)for(int y=pos.getY()-1;y<=pos.getY()+4;y++){
   if(x==pos.getX()&&y==pos.getY()&&z==pos.getZ())continue;var at=new BlockPos(x,y,z);int limit=100;if(y>pos.getY()+1)limit+=(y-(pos.getY()+1))*100;var there=world.getBlockState(at);
   if(there.is(this)&&there.getValue(META)>meta+1){world.setBlock(at,defaultBlockState().setValue(META,meta+1),3);continue;}
   int chance=neighborChance(world,at);if(chance>0){int adjusted=(chance+40+world.getDifficulty().getId()*7)/(meta+30);if(adjusted>0&&random.nextInt(limit)<=adjusted)world.setBlock(at,defaultBlockState().setValue(META,meta+1),3);}
  }
 }
 private void catchFire(ServerLevel world,BlockPos pos,int chance,RandomSource random,int age,Direction face){var state=world.getBlockState(pos);if(random.nextInt(chance)<state.getFlammability(world,pos,face)){boolean tnt=state.is(Blocks.TNT);world.setBlock(pos,defaultBlockState().setValue(META,age+1),3);if(tnt)TntBlock.explode(world,pos);}}
 private static int neighborChance(Level level,BlockPos pos){if(!level.isEmptyBlock(pos))return 0;int chance=0;for(Direction dir:new Direction[]{Direction.EAST,Direction.WEST,Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH}){var at=pos.relative(dir);chance=Math.max(chance,level.getBlockState(at).getFireSpreadSpeed(level,at,dir.getOpposite()));}return chance;}
 @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){entity.igniteForSeconds(10);if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,100,9));}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
 @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
  if(random.nextInt(24)==0)level.playLocalSound(pos.getX()+.5D,pos.getY()+.5D,pos.getZ()+.5D,SoundEvents.FIRE_AMBIENT,SoundSource.BLOCKS,1F+random.nextFloat(),random.nextFloat()*.7F+.3F,false);
  if(level.getBlockState(pos.below()).isSolidRender(level,pos.below())||catches(level,pos.below(),Direction.UP)){for(int i=0;i<3;i++)smoke(pos.getX()+random.nextDouble(),pos.getY()+random.nextDouble()*.5D+.5D,pos.getZ()+random.nextDouble());}
  else{
   if(catches(level,pos.west(),Direction.EAST))for(int i=0;i<2;i++)smoke(pos.getX()+random.nextDouble()*.1D,pos.getY()+random.nextDouble(),pos.getZ()+random.nextDouble());
   if(catches(level,pos.east(),Direction.WEST))for(int i=0;i<2;i++)smoke(pos.getX()+1-random.nextDouble()*.1D,pos.getY()+random.nextDouble(),pos.getZ()+random.nextDouble());
   if(catches(level,pos.north(),Direction.SOUTH))for(int i=0;i<2;i++)smoke(pos.getX()+random.nextDouble(),pos.getY()+random.nextDouble(),pos.getZ()+random.nextDouble()*.1D);
   if(catches(level,pos.south(),Direction.NORTH))for(int i=0;i<2;i++)smoke(pos.getX()+random.nextDouble(),pos.getY()+random.nextDouble(),pos.getZ()+1-random.nextDouble()*.1D);
   if(catches(level,pos.above(),Direction.DOWN))for(int i=0;i<2;i++)smoke(pos.getX()+random.nextDouble(),pos.getY()+1-random.nextDouble()*.1D,pos.getZ()+random.nextDouble());
  }
 }
 private static void smoke(double x,double y,double z){com.hbm.client.bomb.nuclear.LegacyExplosionEffects.particle("largesmoke",x,y,z,0,0,0);}
}
