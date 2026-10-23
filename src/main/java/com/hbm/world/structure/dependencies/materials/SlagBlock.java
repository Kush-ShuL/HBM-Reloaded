package com.hbm.world.structure.dependencies.materials;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** Original BlockSlag -> BlockBeaconable: metadata1 broken icon, ordinary self drop, genuine beacon base through native tag. */
public final class SlagBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public SlagBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(2F,2F).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends SlagBlock> codec(){return simpleCodec(properties->new SlagBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
}
