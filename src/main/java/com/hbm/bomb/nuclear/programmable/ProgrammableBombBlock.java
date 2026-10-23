package com.hbm.bomb.nuclear.programmable;

import com.hbm.interfaces.IBomb;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Original non-normal full collision cube and source per-class metadata orientation/drop paths. */
public abstract class ProgrammableBombBlock extends BaseEntityBlock implements IBomb {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final Random CUSTOM_DROP_RANDOM=new Random();
 protected ProgrammableBombBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
 protected static Properties sourceProperties(){return Properties.of().mapColor(MapColor.METAL).strength(5,120).sound(SoundType.STONE).noOcclusion().isValidSpawn((s,w,p,t)->false);}
 protected abstract boolean timed();
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){int quadrant=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,(timed()?new int[]{2,5,3,4}:new int[]{5,3,4,2})[quadrant]);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(world.isClientSide)return InteractionResult.SUCCESS;if(world.getBlockEntity(pos) instanceof ProgrammableBombInventory bomb)if(player instanceof net.minecraft.server.level.ServerPlayer server)server.openMenu(bomb,bomb.getBlockPos());return InteractionResult.CONSUME;}
 @Override protected void neighborChanged(BlockState state,Level world,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(world.hasNeighborSignal(pos))explode(world,pos.getX(),pos.getY(),pos.getZ());}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
 @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState replacement,boolean moving){if(!state.is(replacement.getBlock())&&world.getBlockEntity(pos) instanceof ProgrammableBombInventory bomb){for(int slot=0;slot<bomb.getContainerSize();slot++){ItemStack stack=bomb.getItem(slot);if(stack.isEmpty())continue;float x=(timed()?world.random.nextFloat():CUSTOM_DROP_RANDOM.nextFloat())*.8F+.1F,y=(timed()?world.random.nextFloat():CUSTOM_DROP_RANDOM.nextFloat())*.8F+.1F,z=(timed()?world.random.nextFloat():CUSTOM_DROP_RANDOM.nextFloat())*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(stack.getCount(),(timed()?world.random.nextInt(21):CUSTOM_DROP_RANDOM.nextInt(21))+10);ItemEntity item=new ItemEntity(world,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));item.setDeltaMovement((float)(timed()?world.random.nextGaussian():CUSTOM_DROP_RANDOM.nextGaussian())*.05F,(float)(timed()?world.random.nextGaussian():CUSTOM_DROP_RANDOM.nextGaussian())*.05F+.2F,(float)(timed()?world.random.nextGaussian():CUSTOM_DROP_RANDOM.nextGaussian())*.05F);world.addFreshEntity(item);}}world.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,world,pos,replacement,moving);}
}
