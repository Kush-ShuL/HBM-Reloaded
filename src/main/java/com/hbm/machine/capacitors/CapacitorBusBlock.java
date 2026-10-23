package com.hbm.machine.capacitors;
import api.hbm.energymk2.IEnergyConnectorBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
/** Original no-TE visual-only bus; no native/standard energy storage or capability is claimed. */
public final class CapacitorBusBlock extends Block implements IEnergyConnectorBlock {
 public CapacitorBusBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(CapacitorBlock.META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(CapacitorBlock.META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext c){return defaultBlockState().setValue(CapacitorBlock.META,c.getPlayer()==null?0:placement(c.getClickedPos(),c.getPlayer()));}
 private static int placement(BlockPos pos,LivingEntity entity){if(Math.abs((float)entity.getX()-pos.getX())<2&&Math.abs((float)entity.getZ()-pos.getZ())<2){double eye=entity.getY()+1.82;if(eye-pos.getY()>2)return 1;if(pos.getY()-eye>0)return 0;}return switch(Mth.floor(entity.getYRot()*4F/360F+.5)&3){case 0->2;case 1->5;case 2->3;default->4;};}
 @Override public boolean canConnect(BlockGetter world,BlockPos pos,Direction side){int meta=world.getBlockState(pos).getValue(CapacitorBlock.META);return meta<6&&side==Direction.from3DDataValue(meta);}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(CapacitorBlock.META);return meta<6?state.setValue(CapacitorBlock.META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(CapacitorBlock.META);return meta<6?state.setValue(CapacitorBlock.META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
}
