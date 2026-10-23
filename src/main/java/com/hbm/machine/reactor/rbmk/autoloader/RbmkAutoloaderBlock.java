package com.hbm.machine.reactor.rbmk.autoloader;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
/** Authored nine-cell autoloader, narrow0..4 post and full4..9 upper housing. */
public final class RbmkAutoloaderBlock extends DummyableBlock {
    public RbmkAutoloaderBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(50,36).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends RbmkAutoloaderBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{8,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkAutoloaderEntity(pos,state):new DummyInventoryProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkAutoloaderContent.ENTITY.get(),RbmkAutoloaderEntity::tick):null;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){var core=findCore(level,pos);return core==null||pos.getY()-core.getY()<4?box(6,0,6,10,16,10):Shapes.block();}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
}
