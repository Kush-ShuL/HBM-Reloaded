package com.hbm.machine.coolingtower;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source MachineCondenser: ordinary full iron-material block, real passive converterBE, no GUI. */
public final class CondenserBlock extends BaseEntityBlock {
 public CondenserBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends CondenserBlock> codec(){return simpleCodec(p->new CondenserBlock());}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CoolingTowerBlockEntity(pos,state);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,CoolingTowerContent.ENTITIES.get(CoolingTowerKind.CONDENSER).get(),CoolingTowerBlockEntity::tick);}
}
