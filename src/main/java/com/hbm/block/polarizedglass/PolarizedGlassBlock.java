package com.hbm.block.polarizedglass;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source BlockNTMGlassCT(0,...), not the source luminous layer1/light5 family. */
public final class PolarizedGlassBlock extends HalfTransparentBlock {
    public PolarizedGlassBlock(){super(Properties.of().mapColor(MapColor.NONE).strength(.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((s,l,p,t)->false).isViewBlocking((s,l,p)->false).isSuffocating((s,l,p)->false));}
    @Override protected MapCodec<? extends HalfTransparentBlock> codec(){return MapCodec.unit(this);}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
}
