package com.hbm.machine.turret.greg;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
/** Actual TurretArty source dimensions[1,0,2,1,2,1],offset1,full source collision; no imaginary small-turret port outlines. */
public final class GregTurretBlock extends DummyableBlock {
    public final GregTurretKind kind;public GregTurretBlock(GregTurretKind kind){super(Properties.of().strength(5,360).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.kind=kind;}
    @Override protected MapCodec<? extends GregTurretBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{1,0,2,1,2,1};}
    @Override public int offset(){return 1;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,16,16);}@Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return DummyableBlock.isCore(state)?new GregTurretEntity(kind,pos,state):new GregTurretProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return DummyableBlock.isCore(state)?(world,pos,s,tile)->{if(tile instanceof GregTurretEntity turret){if(world instanceof ServerLevel server)turret.tickServer(server);else turret.tickClient();}}:null;}
}
