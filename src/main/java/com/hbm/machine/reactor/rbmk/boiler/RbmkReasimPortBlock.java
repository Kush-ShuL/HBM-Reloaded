package com.hbm.machine.reactor.rbmk.boiler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Real source inlet/outlet block containers, full static cubes without a GUI. */
public final class RbmkReasimPortBlock extends BaseEntityBlock {
    public final boolean output;public RbmkReasimPortBlock(boolean output){super(Properties.of().mapColor(MapColor.METAL).strength(50,36).sound(SoundType.STONE).requiresCorrectToolForDrops());this.output=output;}
    @Override protected MapCodec<? extends RbmkReasimPortBlock> codec(){return MapCodec.unit(this);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RbmkReasimPort(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,RbmkBoilerContent.REASIM_PORT.get(),RbmkReasimPort::tick);}
}
