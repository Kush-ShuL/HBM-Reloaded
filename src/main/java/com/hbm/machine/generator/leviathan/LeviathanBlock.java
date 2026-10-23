package com.hbm.machine.generator.leviathan;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Original4-boxMachineChungus, specialfrontport and physicalcompressorlever. NoGUIexistsinsource. */
public final class LeviathanBlock extends DummyableBlock {
 public static final int[][] BOXES={{3,0,0,3,2,2},{4,-4,0,3,1,1},{3,0,6,-1,1,1},{2,0,10,-7,1,1}};
 public LeviathanBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return BOXES[0].clone();}@Override public int offset(){return 3;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var result=super.getStateForPlacement(context);if(result==null)return null;var core=coreForPlacement(context);var facing=placementFacing(result);for(int i:new int[]{2,3}){var d=rotate(BOXES[i],facing);for(var pos:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}var special=core.relative(facing,4).above(2);if(!context.getLevel().isInWorldBounds(special)||!context.getLevel().hasChunkAt(special)||!context.getLevel().getBlockState(special).canBeReplaced())return null;return result;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());safely(()->{for(int i=1;i<BOXES.length;i++){var d=rotate(BOXES[i],facing);for(var location:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3]))){var part=location.immutable();var away=part.getY()>core.getY()?Direction.UP:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}level.setBlock(core.relative(facing,4).above(2),defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()),UPDATE_ALL);var actual=level.getBlockState(core);for(var port:LeviathanEntity.fluidPortPositions(core,actual))setPort(level,port,true);setPort(level,LeviathanEntity.powerPortPosition(core,actual),true);});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new LeviathanEntity(pos,state):isPort(state)?new LeviathanProxy(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,LeviathanContent.ENTITY.get(),LeviathanEntity::tick):null;}
 private InteractionResult activate(Level level,BlockPos pos,Player player){if(player.isShiftKeyDown())return InteractionResult.PASS;var core=findCore(level,pos);if(core==null)return InteractionResult.SUCCESS;if(!(level.getBlockEntity(core) instanceof LeviathanEntity machine))return InteractionResult.PASS;var dir=facing(machine.getBlockState());var side=dir.getCounterClockWise();var a=core.relative(dir).relative(side,2);var b=core.relative(dir,2).relative(side,2);if((pos.getX()==a.getX()||pos.getX()==b.getX())&&(pos.getZ()==a.getZ()||pos.getZ()==b.getZ())&&pos.getY()<core.getY()+2){if(!level.isClientSide){if(!machine.operational){level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("block.chungusLever"),SoundSource.BLOCKS,1.5F,1);machine.onLeverPull();}else player.displayClientMessage(Component.translatable("message.hbm.leviathan.running").withStyle(ChatFormatting.RED),false);}return InteractionResult.SUCCESS;}return InteractionResult.PASS;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}@Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(activate(level,pos,player).consumesAction())return ItemInteractionResult.SUCCESS;return super.useItemOn(stack,state,level,pos,player,hand,hit);}
}
