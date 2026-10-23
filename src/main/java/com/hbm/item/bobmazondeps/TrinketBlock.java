package com.hbm.item.bobmazondeps;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

/** Exactly the shared harvest/placement path in the two source BlockContainers; shape/transform/use remain distinct. */
public abstract class TrinketBlock extends BaseEntityBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 protected TrinketBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
 protected abstract boolean snowglobe();
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,Mth.floor((context.getRotation()+180)*16F/360F+.5D)&15);}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){int meta=BobmazonDependenciesContent.metadata(stack);if(level.getBlockEntity(pos)instanceof SnowglobeEntity tile)tile.setType(meta);else if(level.getBlockEntity(pos)instanceof PlushieEntity tile)tile.setType(meta);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
 @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var entity=level.getBlockEntity(pos);int type=entity instanceof SnowglobeEntity tile?tile.type.ordinal():entity instanceof PlushieEntity tile?tile.type.ordinal():0;return BobmazonDependenciesContent.stack(snowglobe(),type);}
 @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player){if(!level.isClientSide&&!player.getAbilities().instabuild&&(level.getBlockEntity(pos)instanceof SnowglobeEntity||level.getBlockEntity(pos)instanceof PlushieEntity)){var stack=getCloneItemStack(level,pos,state);var item=new ItemEntity(level,pos.getX()+.5,pos.getY(),pos.getZ()+.5,stack);item.setDeltaMovement(0,0,0);level.addFreshEntity(item);}return super.playerWillDestroy(level,pos,state,player);}
 @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity entity,ItemStack tool){player.awardStat(Stats.BLOCK_MINED.get(this));player.causeFoodExhaustion(.025F);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of();}
}
