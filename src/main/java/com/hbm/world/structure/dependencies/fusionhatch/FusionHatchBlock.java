package com.hbm.world.structure.dependencies.fusionhatch;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** True original deprecated FusionHatch: full stone-sounding iron cube, source texture faces and one final yaw-facing placement; no BE/GUI invented. */
public final class FusionHatchBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public FusionHatchBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends FusionHatchBlock> codec(){return simpleCodec(properties->new FusionHatchBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return fromLegacy(new int[]{2,5,3,4}[yaw]);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 // Source FusionHatch is not INBTBlockTransformable and is not a vanilla rotatable block.
 // Base Block.rotate intentionally leaves raw legacy_meta unchanged for native template rotation.
}
