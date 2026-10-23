package com.hbm.machine.purex;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Original5x5x5 dummy structure, offset2, sixteen bottom ports and twenty outward connections; other parts have no BE. */
public final class PurexBlock extends DummyableBlock {
    private static final MapCodec<PurexBlock> CODEC = simpleCodec(ignored -> new PurexBlock());
    public PurexBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{4,0,2,2,2,2}; }
    @Override public int offset() { return 2; }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if (!level.isClientSide) for (var port : PurexBlockEntity.portPositions(pos.relative(placementFacing(state),-offset()))) setPort(level,port,true);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return isCore(state) ? new PurexBlockEntity(pos,state) : isPort(state) ? new PurexPort(pos,state) : null;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) {
        return isCore(state) ? createTickerHelper(type,PurexContent.ENTITY.get(),PurexBlockEntity::tick) : null;
    }
}
