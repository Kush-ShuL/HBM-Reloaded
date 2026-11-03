package com.hbm.world.structure.dependencies.materials;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
/** Slag's broken appearance shares the ordinary item, loot and beacon behavior. */
public final class SlagBlock extends Block {
 public static final BooleanProperty BROKEN=BooleanProperty.create("broken");
 public SlagBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(2F,2F).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(BROKEN,false));}
 @Override protected MapCodec<? extends SlagBlock> codec(){return simpleCodec(properties->new SlagBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(BROKEN);}
 /** Decode original source structures; metadata one was the sole broken texture. */
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(BROKEN,metadata==1);}
}
