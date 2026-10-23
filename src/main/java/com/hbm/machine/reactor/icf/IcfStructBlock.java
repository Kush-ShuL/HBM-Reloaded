package com.hbm.machine.reactor.icf;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
public final class IcfStructBlock extends BaseEntityBlock{
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,5);
 public IcfStructBlock(){super(Properties.of().strength(5,10).sound(SoundType.STONE).lightLevel(state->15).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new IcfStructBlock());}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}@Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().getOpposite().get3DDataValue());}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new IcfStructEntity(pos,state);}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,IcfReactorContent.STRUCT_ENTITY.get(),IcfStructEntity::tick);}
}
