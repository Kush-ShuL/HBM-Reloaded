package com.hbm.world.structure.dependencies.materials;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** Original deprecated BlockPillar: fixed upper/lower icon, no axis, machine, BE or placement orientation. */
public final class FusionHeaterBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public FusionHeaterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends FusionHeaterBlock> codec(){return simpleCodec(properties->new FusionHeaterBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
}
