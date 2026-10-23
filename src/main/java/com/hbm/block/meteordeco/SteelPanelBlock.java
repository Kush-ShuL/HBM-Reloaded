package com.hbm.block.meteordeco;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.hbm.api.tool.ToolableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
/** Source DecoBlock's real steel wall and three-piece steel corner, including screwdriver direction cycles. */
public final class SteelPanelBlock extends Block implements ToolableBlock,SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final boolean corner;
 public SteelPanelBlock(boolean corner){super(Properties.of().strength(corner?15:5,9).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.corner=corner;registerDefaultState(stateDefinition.any().setValue(META,3).setValue(WATERLOGGED,false));}@Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,WATERLOGGED);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}@Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getHorizontalDirection().get3DDataValue()).setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);}
 public static double[][] boxes(boolean corner,int meta){if(!corner)return switch(meta){case 2->new double[][]{{0,0,.875,1,1,1}};case 3->new double[][]{{0,0,0,1,1,.125}};case 4->new double[][]{{.875,0,0,1,1,1}};case 5->new double[][]{{0,0,0,.125,1,1}};default->new double[][]{{0,0,0,1,1,1}};};return switch(meta){case 2->new double[][]{{.25,0,.875,1,1,1},{0,0,.75,.25,1,1},{0,0,0,.125,1,.75}};case 3->new double[][]{{0,0,0,.75,1,.125},{.75,0,0,1,1,.25},{.875,0,.25,1,1,1}};case 4->new double[][]{{.875,0,0,1,1,.75},{.75,0,.75,1,1,1},{0,0,.875,.75,1,1}};case 5->new double[][]{{0,0,.25,.125,1,1},{0,0,0,.25,1,.25},{.25,0,0,1,1,.125}};default->new double[0][];};}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){var shape=Shapes.empty();for(var b:boxes(corner,state.getValue(META)))shape=Shapes.or(shape,Shapes.box(b[0],b[1],b[2],b[3],b[4],b[5]));return shape;}@Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return corner?Shapes.block():getCollisionShape(state,level,pos,context);}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;int meta=level.getBlockState(pos).getValue(META);int next=player.isShiftKeyDown()?switch(meta){case 3->5;case 4->3;case 2->4;case 5->2;default->meta;}:switch(meta){case 3->4;case 4->2;case 2->5;case 5->3;default->meta;};if(!level.isClientSide)level.setBlock(pos,level.getBlockState(pos).setValue(META,next),UPDATE_ALL);return true;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}@Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);if(meta<2||meta>5||mirror==Mirror.NONE)return state;if(!corner)return state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue());int next=mirror==Mirror.LEFT_RIGHT?switch(meta){case 2->5;case 3->4;case 4->3;default->2;}:switch(meta){case 2->4;case 3->5;case 4->2;default->3;};return state.setValue(META,next);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of(new ItemStack(this));}

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor world,BlockPos pos,BlockPos neighborPos){
        if(state.getValue(WATERLOGGED))world.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(world));
        return super.updateShape(state,direction,neighbor,world,pos,neighborPos);
    }
}
