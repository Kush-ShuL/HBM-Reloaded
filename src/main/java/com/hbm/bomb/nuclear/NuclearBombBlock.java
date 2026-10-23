package com.hbm.bomb.nuclear;

import com.hbm.interfaces.IBomb;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Source static nuclear assemblies, inventory loss on detonation and redstone/IBomb entry paths. */
public final class NuclearBombBlock extends BaseEntityBlock implements IBomb {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final Random dropRandom=new Random();
 private final String kind;
 public NuclearBombBlock(String kind){this(kind,Properties.of().mapColor(MapColor.METAL).strength(5F,120F).sound(SoundType.STONE).noOcclusion().isValidSpawn((state,world,pos,type)->false));}
 private NuclearBombBlock(String kind,Properties properties){super(properties);this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,0));}
 public String kind(){return kind;}
 @Override protected MapCodec<? extends NuclearBombBlock> codec(){return simpleCodec(properties->new NuclearBombBlock(kind,properties));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,new int[]{5,3,4,2}[Mth.floor(context.getRotation()*4F/360F+.5D)&3]);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new NuclearBombBlockEntity(pos,state);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(!level.isClientSide&&player instanceof net.minecraft.server.level.ServerPlayer server&&level.getBlockEntity(pos) instanceof NuclearBombBlockEntity bomb)server.openMenu(bomb);return InteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos other,boolean moving){if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());}
 @Override public BombReturnCode explode(Level level,int x,int y,int z){
  if(level.isClientSide)return BombReturnCode.UNDEFINED;var pos=new BlockPos(x,y,z);if(!(level.getBlockEntity(pos) instanceof NuclearBombBlockEntity entity))return BombReturnCode.ERROR_MISSING_COMPONENT;var inventory=entity.assembly();boolean filled=inventory.isFilled();if(!inventory.isReady()&&!filled)return BombReturnCode.ERROR_MISSING_COMPONENT;
  int radius=NuclearConfig.radius(kind);
  if(kind.equals("tsar")&&!filled)radius=NuclearConfig.radius("man");
  // X5846 Mike passes manRadius when unfilled, but igniteTestBomb ignores that argument and always uses mikeRadius.
  inventory.clearContent();level.removeBlock(pos,false);
  if(kind.equals("fleija")||kind.equals("prototype")||kind.equals("solinium")){
   var explosion=NuclearExplosionMK3.statFacFleija(level,x+.5D,y+.5D,z+.5D,radius);if(kind.equals("solinium"))explosion.makeSol();
   if(!explosion.isRemoved()){level.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,1F,level.random.nextFloat()*.1F+.9F);level.addFreshEntity(explosion);NuclearFleijaCloud cloud=kind.equals("solinium")?new NuclearSoliniumCloud(level,radius):new NuclearFleijaCloud(NuclearContent.FLEIJA_CLOUD.get(),level,radius);cloud.setPos(x,y,z);level.addFreshEntity(cloud);}return BombReturnCode.DETONATED;
  }
  level.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,1F,level.random.nextFloat()*.1F+.9F);level.addFreshEntity(kind.equals("n2")?NuclearExplosionMK5.statFacNoRad(level,radius,x+.5D,y+.5D,z+.5D):NuclearExplosionMK5.statFac(level,radius,x+.5D,y+.5D,z+.5D));
  if(kind.equals("boy")){var torex=new NuclearTorex(NuclearContent.TOREX.get(),level);torex.setPos(x+.5D,y+1D,z+.5D);torex.setScale(1.5F);level.addFreshEntity(torex);}else NuclearTorex.statFacStandard(level,x+.5D,y+.5D,z+.5D,radius);
  return BombReturnCode.DETONATED;
 }
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
  if(!state.is(replacement.getBlock())&&level.getBlockEntity(pos) instanceof NuclearBombBlockEntity entity){var inventory=entity.assembly();for(int slot=0;slot<inventory.getContainerSize();slot++){var stack=inventory.getItem(slot);if(stack.isEmpty())continue;float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(dropRandom.nextInt(21)+10,stack.getCount());var item=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));item.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(item);}}level.updateNeighbourForOutputSignal(pos,this);}
  super.onRemove(state,level,pos,replacement,moving);
 }
}
