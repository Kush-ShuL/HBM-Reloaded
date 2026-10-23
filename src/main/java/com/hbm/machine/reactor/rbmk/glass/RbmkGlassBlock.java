package com.hbm.machine.reactor.rbmk.glass;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual BlockNTMGlassCT(0,name,Material.glass), doesDrop=false; ordinary harvest has no drop. */
public final class RbmkGlassBlock extends HalfTransparentBlock {
    private static final MapCodec<RbmkGlassBlock> CODEC = simpleCodec(RbmkGlassBlock::new);
    public RbmkGlassBlock() { this(Properties.of().mapColor(MapColor.NONE).strength(.3F).sound(SoundType.GLASS).noOcclusion()
            .isViewBlocking((state, world, pos) -> false).isSuffocating((state, world, pos) -> false).isValidSpawn((state, world, pos, type) -> false)); }
    public RbmkGlassBlock(Properties properties) { super(properties); }
    @Override public MapCodec<? extends HalfTransparentBlock> codec() { return CODEC; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    @Override protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) { return 1F; }
}
