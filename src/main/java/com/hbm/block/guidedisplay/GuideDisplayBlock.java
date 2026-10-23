package com.hbm.block.guidedisplay;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original full iron guide cube, including its unusual unrotated top and yaw-to-side mapping. */
public final class GuideDisplayBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public GuideDisplayBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int index=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,switch(index){case 0->2;case 1->5;case 2->3;default->4;});}
    private InteractionResult activate(Level level,Player player){if(level.isClientSide&&!player.isShiftKeyDown()){GuideDisplayContent.openWiki.run();return InteractionResult.SUCCESS;}return InteractionResult.PASS;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,net.minecraft.core.BlockPos pos,Player player,BlockHitResult hit){return activate(level,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,net.minecraft.core.BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,player)==InteractionResult.SUCCESS?ItemInteractionResult.SUCCESS:ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
}
