package com.hbm.machine.oilcatalyticcracker;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** True five XR cuboids and final core position; no filled bounding-box shortcut or invented GUI. */
public final class CatalyticCrackerBlock extends DummyableBlock {
 private static final int[][] DIMENSIONS={{0,0,3,3,2,3},{8,-1,3,-1,2,0},{13,0,0,3,2,1},{14,-13,-1,2,1,0},{3,-1,2,3,-1,3}};
 public CatalyticCrackerBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends CatalyticCrackerBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return DIMENSIONS[0].clone();}@Override public int offset(){return 3;}
 public static Set<BlockPos> cells(BlockPos core,Direction facing){var cells=new LinkedHashSet<BlockPos>();for(var dimensions:DIMENSIONS){var d=rotate(dimensions,facing);for(var p:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))cells.add(p.immutable());}return cells;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;for(var p:cells(coreForPlacement(context),context.getHorizontalDirection().getOpposite()))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());safely(()->{for(var p:cells(core,facing)){if(p.equals(core))continue;Direction away=p.getY()<core.getY()?Direction.DOWN:p.getY()>core.getY()?Direction.UP:p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});for(var port:CatalyticCrackerEntity.ports(core,facing))setPort(level,port.position(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CatalyticCrackerEntity(pos,state):isPort(state)?new CatalyticCrackerPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CatalyticCrackerContent.ENTITY.get(),CatalyticCrackerEntity::tick):null;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!player.isShiftKeyDown()&&stack.getItem() instanceof FluidIdentifier identifier){if(level.isClientSide)return ItemInteractionResult.SUCCESS;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof CatalyticCrackerEntity machine))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;var type=identifier.getType(level,core,stack);machine.tanks[0].setTankType(type);machine.setChanged();player.displayClientMessage(Component.translatable("gui.hbm.catalytic_cracker.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);return ItemInteractionResult.CONSUME;}return super.useItemOn(stack,state,level,pos,player,hand,hit);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return level.isClientSide||player.isShiftKeyDown()?InteractionResult.SUCCESS:InteractionResult.PASS;}
}
