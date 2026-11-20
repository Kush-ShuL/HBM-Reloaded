package com.hbm.machine.reactorcontroller;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

/** Retired reactor remote, retained as the inert directional block from upstream. */
public final class ReactorControllerBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public ReactorControllerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getHorizontalDirection().getOpposite().get3DDataValue());}
}
