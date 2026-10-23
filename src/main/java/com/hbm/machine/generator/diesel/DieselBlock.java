package com.hbm.machine.generator.diesel;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
public final class DieselBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;private static final MapCodec<DieselBlock> CODEC=simpleCodec(ignored->new DieselBlock());
    public DieselBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}@Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DieselBlockEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,DieselContent.ENTITY.get(),DieselBlockEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof DieselBlockEntity engine){server.openMenu(engine,pos);return InteractionResult.CONSUME;}return InteractionResult.PASS;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&level.getBlockEntity(pos) instanceof DieselBlockEntity engine){Containers.dropContents(level,pos,engine);level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,replacement,moving);}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(level.getBlockEntity(pos) instanceof DieselBlockEntity engine&&engine.isOn&&engine.hasAcceptableFuel()&&engine.tank.getFill()>0){Direction direction=state.getValue(FACING),rot=direction.getCounterClockWise();level.addParticle(ParticleTypes.SMOKE,pos.getX()+.5-direction.getStepX()*.6+rot.getStepX()*.1875,pos.getY()+.3125,pos.getZ()+.5-direction.getStepZ()*.6+rot.getStepZ()*.1875,0,0,0);}}
}
