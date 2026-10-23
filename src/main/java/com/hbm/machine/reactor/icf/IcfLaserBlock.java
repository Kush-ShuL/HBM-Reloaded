package com.hbm.machine.reactor.icf;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.BlockHitResult;
public final class IcfLaserBlock extends BaseEntityBlock{
 public IcfLaserBlock(){super(Properties.of().strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(IcfStructBlock.META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new IcfLaserBlock());}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(IcfStructBlock.META);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(IcfStructBlock.META,context.getHorizontalDirection().getOpposite().get3DDataValue());}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new IcfLaserEntity(pos,state);}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,IcfReactorContent.LASER_ENTITY.get(),IcfLaserEntity::tick);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos) instanceof IcfLaserEntity laser&&!laser.assembled)IcfLaserAssembly.assemble(level,pos,(ServerPlayer)player);return InteractionResult.CONSUME;}
}
