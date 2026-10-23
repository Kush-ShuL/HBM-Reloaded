package com.hbm.item.recipedependencies;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** Actual BlockNTMGlassCT(1,rock,true): source translucent CT, rock tool/sound, ordinary and silk self drop0. */
public final class ReinforcedLaminateBlock extends HalfTransparentBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public ReinforcedLaminateBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(15,180).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isViewBlocking((s,w,p)->false).isSuffocating((s,w,p)->false).isValidSpawn((s,w,p,t)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends HalfTransparentBlock> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
}
