package com.hbm.machine.capacitors;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
/** Real MachineCapacitor: source power constructor argument is unused; no BE, storage, GUI or discharge is invented. */
public final class CapacitorBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final String metal;
 public CapacitorBlock(String metal){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));this.metal=metal;registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(META);}
 public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,meta&15);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext c){return fromLegacy(c.getClickedFace().get3DDataValue());}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta<6?fromLegacy(rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta<6?fromLegacy(mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
 @Override protected VoxelShape getOcclusionShape(BlockState s,BlockGetter w,BlockPos p){return Shapes.empty();}@Override protected int getLightBlock(BlockState s,BlockGetter w,BlockPos p){return 0;}@Override protected boolean propagatesSkylightDown(BlockState s,BlockGetter w,BlockPos p){return true;}
}
