package com.hbm.machine.centrifuge;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;

/** Source four-block column with full-width foot and 3/4-width body. */
public final class CentrifugeBlock extends DummyableBlock {
    private static final MapCodec<CentrifugeBlock> CODEC=simpleCodec(ignored->new CentrifugeBlock());
    public CentrifugeBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{3,0,0,0,0,0}; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return isCore(state)?new CentrifugeBlockEntity(pos,state):isPort(state)?new CentrifugePowerProxy(pos,state):null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) {
        if(type==CentrifugeContent.PROXY.get()) return createTickerHelper(type,CentrifugeContent.PROXY.get(),CentrifugePowerProxy::tick);
        return createTickerHelper(type,CentrifugeContent.ENTITY.get(),CentrifugeBlockEntity::tick);
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        BlockPos core=findCore(level,pos); if(core==null) return Shapes.empty();
        return core.equals(pos)?box(0,0,0,16,15.984,16):box(2,0,2,14,16,14);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        BlockPos core=findCore(level,pos); if(core==null) return Shapes.empty();
        return core.equals(pos)?Shapes.block():box(2,0,2,14,16,14);
    }
}
