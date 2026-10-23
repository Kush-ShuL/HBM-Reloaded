package com.hbm.machine.turret.maxwell;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

/** Actual TurretMaxwell extends TurretBaseNT:2x2 footprint, half-height cells and inventory/power proxies. */
public final class MaxwellTurretBlock extends DummyableBlock {
    public MaxwellTurretBlock() { super(Properties.of().strength(5,36).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends MaxwellTurretBlock> codec() { return MapCodec.unit(this); }
    @Override public int[] dimensions() { return new int[]{0,0,1,0,1,0}; }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return Block.box(0,0,0,16,8,16); }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return getShape(state,level,pos,context); }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return DummyableBlock.isCore(state)?new MaxwellTurretEntity(pos,state):new MaxwellTurretProxy(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) {
        return DummyableBlock.isCore(state)?(world,pos,s,tile)->{if(tile instanceof MaxwellTurretEntity turret){if(world instanceof ServerLevel server)turret.tickServer(server);else turret.tickClient();}}:null;
    }
}
