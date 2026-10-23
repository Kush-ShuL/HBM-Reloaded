package com.hbm.block.meteorstatue;

import com.hbm.explosion.ExplosionFleijaExempt;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** X5846 DecoBlockAlt's sole registered statue variant; its illegal old registry name is not reused. */
public final class MeteorStatueBlock extends BaseEntityBlock implements ExplosionFleijaExempt {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final VoxelShape BASE=box(0,0,0,16,8,16);
 public MeteorStatueBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY).sound(SoundType.STONE).noOcclusion().lightLevel(state->15).isValidSpawn((s,w,p,t)->false));}
 private MeteorStatueBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends MeteorStatueBlock> codec(){return simpleCodec(MeteorStatueBlock::new);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,new int[]{2,5,3,4}[Mth.floor(context.getRotation()*4F/360F+.5D)&3]);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return BASE;}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return BASE;}
 @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
 @Override protected float getShadeBrightness(BlockState state,BlockGetter level,BlockPos pos){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MeteorStatueBlockEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,MeteorStatueContent.ENTITY.get(),MeteorStatueBlockEntity::tick);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(!player.isShiftKeyDown()){ItemStack held=player.getMainHandItem();if(!held.isEmpty()){boolean cracked=held.is(HbmContent.item("key_red_cracked"));if(cracked||held.is(HbmContent.item("key_red"))){if(cracked)held.shrink(1);level.destroyBlock(pos,true);return InteractionResult.CONSUME;}}}return InteractionResult.PASS;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
}
