package com.hbm.world.structure.dependencies.industrial44;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** DecoPoleTop: nonopaque ISBRH appearance, original full-cube physical collision. */
public final class PoleTopBlock extends Block {
 public PoleTopBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,9).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
}
