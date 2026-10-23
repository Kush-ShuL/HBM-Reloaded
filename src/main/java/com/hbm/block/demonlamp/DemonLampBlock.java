package com.hbm.block.demonlamp;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

/** X5846 DemonLamp: iron full-cube collision, non-opaque OBJ, and clicked-side raw metadata. */
public final class DemonLampBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public DemonLampBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3,3).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion().lightLevel(state->15));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getClickedFace().get3DDataValue());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DemonLampEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world,BlockState state,BlockEntityType<T> type){return world.isClientSide?null:createTickerHelper(type,DemonLampContent.ENTITY.get(),DemonLampEntity::tick);}
    @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
}
