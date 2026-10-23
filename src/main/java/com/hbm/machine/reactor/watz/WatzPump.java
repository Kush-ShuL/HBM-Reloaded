package com.hbm.machine.reactor.watz;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original inert2-high pump. Its upper dummy cell is the source UP-side support. */
public final class WatzPump extends DummyableBlock {
    public WatzPump(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,0,0,0,0,0};}
    @Override protected net.minecraft.world.phys.shapes.VoxelShape getBlockSupportShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos){return state.getValue(LEGACY_META)==1?net.minecraft.world.level.block.Block.box(0,15,0,16,16,16):net.minecraft.world.phys.shapes.Shapes.empty();}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new WatzPumpEntity(pos,state):null;}
}
