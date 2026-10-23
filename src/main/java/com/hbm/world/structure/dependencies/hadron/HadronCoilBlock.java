package com.hbm.world.structure.dependencies.hadron;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** Real X5846 BlockHadronCoil; all nine materials connect to every actual coil variant. */
public final class HadronCoilBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public final int factor;
 public HadronCoilBlock(int factor){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.METAL).requiresCorrectToolForDrops());this.factor=factor;registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends HadronCoilBlock> codec(){return simpleCodec(properties->new HadronCoilBlock(factor));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 public boolean canConnect(BlockState other){return other.getBlock() instanceof HadronCoilBlock;}
}
