package com.hbm.bomb.nuclear;

import com.hbm.LegacyStartup;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorCross;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.interfaces.IBomb;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Four true source duds: defusing returns their source materials; IBomb detonates their distinct mechanisms. */
public final class NuclearDudBlock extends BaseEntityBlock implements IBomb {
 public enum Type{BALEFIRE,CONVENTIONAL,NUKE,SALTED}
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public NuclearDudBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(-1F,3600F).noOcclusion().isValidSpawn((state,world,pos,type)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends NuclearDudBlock> codec(){return simpleCodec(properties->new NuclearDudBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public static Type type(BlockState state){return Type.values()[Math.abs(state.getValue(META)%4)];}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new NuclearDudBlockEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,NuclearSpecialContent.DUD_ENTITY.get(),(world,pos,block,entity)->entity.serverTick());}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){
  if(world.isClientSide)return InteractionResult.SUCCESS;
  if(!player.getMainHandItem().isEmpty()&&player.getMainHandItem().getItem()==HbmContent.item("defuser")){
   switch(type(state)){
    case BALEFIRE->drop(world,pos,"egg_balefire_shard",1);
    case CONVENTIONAL->drop(world,pos,"ball_tnt",16);
    case NUKE->{drop(world,pos,"ball_tnt",8);drop(world,pos,"billet_plutonium",4);}
    case SALTED->{drop(world,pos,"ball_tnt",8);drop(world,pos,"billet_plutonium",2);drop(world,pos,"ingot_cobalt",12);}
   }
   world.destroyBlock(pos,false);return InteractionResult.SUCCESS;
  }
  return InteractionResult.PASS;
 }
 private static void drop(Level world,BlockPos pos,String symbol,int count){world.addFreshEntity(new ItemEntity(world,pos.getX()+.5D,pos.getY()+.5D,pos.getZ()+.5D,new ItemStack(HbmContent.item(symbol),count)));}
 @Override public BombReturnCode explode(Level world,int x,int y,int z){
  if(!world.isClientSide){var pos=new BlockPos(x,y,z);var type=type(world.getBlockState(pos));world.removeBlock(pos,false);
   switch(type){
    case BALEFIRE->{var blast=new NuclearBalefireExplosion(NuclearSpecialContent.BALEFIRE_EXPLOSION.get(),world);blast.setPos(x,y,z);blast.destructionRange=(int)(NuclearConfig.radius("fatman")*1.25);world.addFreshEntity(blast);spawnMush(world,x,y,z,true);}
    case CONVENTIONAL->{var blast=new ExplosionVNT(world,x+.5D,y+.5D,z+.5D,35F);blast.setBlockAllocator(new BlockAllocatorStandard(24));blast.setBlockProcessor(new BlockProcessorStandard().setNoDrop());blast.setEntityProcessor(new EntityProcessorCross(5D).withRangeMod(1.5F));blast.setPlayerProcessor(new PlayerProcessorStandard());blast.explode();LargeConventionalEffects.large(world,x+.5D,y+.5D,z+.5D);}
    case NUKE->{world.addFreshEntity(NuclearExplosionMK5.statFac(world,35,x+.5D,y+.5D,z+.5D));spawnMush(world,x,y,z,LegacyStartup.polaroidId==11||world.random.nextInt(100)==0);}
    case SALTED->{world.addFreshEntity(NuclearExplosionMK5.statFac(world,25,x+.5D,y+.5D,z+.5D).moreFallout(25));spawnMush(world,x,y,z,LegacyStartup.polaroidId==11||world.random.nextInt(100)==0);}
   }
  }
  return BombReturnCode.DETONATED;
 }
 public static void spawnMush(Level level,int x,int y,int z,boolean balefire){level.playSound(null,x+.5D,y+.5D,z+.5D,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15F,1F);NuclearMukeEffects.send(level,x+.5D,y+.5D,z+.5D,"muke",balefire);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of(stack(state));}
 private ItemStack stack(BlockState state){return NuclearSpecialContent.dudStack(type(state));}
 @Override public ItemStack getCloneItemStack(LevelReader world,BlockPos pos,BlockState state){return stack(state);}
}
