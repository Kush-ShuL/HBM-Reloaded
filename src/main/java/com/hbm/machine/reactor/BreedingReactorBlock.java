package com.hbm.machine.reactor;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original three-cell breeder, inventory proxies on both upper cells. */
public final class BreedingReactorBlock extends DummyableBlock {
    private static final MapCodec<BreedingReactorBlock> CODEC=simpleCodec(ignored->new BreedingReactorBlock());
    public BreedingReactorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{2,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new BreedingReactorBlockEntity(pos,state):new ReactorInventoryPort(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ReactorContent.BREEDING_ENTITY.get(),BreedingReactorBlockEntity::tick):null;}
}
