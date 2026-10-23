package com.hbm.world.structure.logic;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source invisible runtime hides rendering/opacity only; its full collision is deliberately retained. */
public final class InvisibleLogicBlock extends LogicBlock {
    public InvisibleLogicBlock() { this(Properties.of().mapColor(MapColor.STONE).strength(0,0).requiresCorrectToolForDrops().noOcclusion()); }
    private InvisibleLogicBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends InvisibleLogicBlock> codec() { return simpleCodec(InvisibleLogicBlock::new); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
}
