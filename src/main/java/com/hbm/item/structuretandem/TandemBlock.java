package com.hbm.item.structuretandem;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.constructionwand.ConstructionWandContent;
import com.hbm.item.structurewand.StructureBlacklist;
import com.hbm.item.structurewand.StructureWandContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Source editor cube and six real directions, represented by a native property rather than a save/meta importer. */
public final class TandemBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=DirectionalBlock.FACING;
    public TandemBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(0,0).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.DOWN));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new TandemEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,TandemContent.ENTITY.get(),TandemEntity::tick);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var player=context.getPlayer();var pos=context.getClickedPos();Direction direction=Direction.DOWN;
        if(player!=null){
            boolean close=Math.abs((float)player.getX()-pos.getX())<2&&Math.abs((float)player.getZ()-pos.getZ())<2;
            double eye=player.getY()+1.82;
            if(close&&eye-pos.getY()>2)direction=Direction.UP;
            else if(close&&pos.getY()-eye>0)direction=Direction.DOWN;
            else direction=switch(Mth.floor(player.getYRot()*4F/360F+.5)&3){case 0->Direction.NORTH;case 1->Direction.EAST;case 2->Direction.SOUTH;default->Direction.WEST;};
        }
        return defaultBlockState().setValue(FACING,direction);
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.setValue(FACING,mirror.mirror(state.getValue(FACING)));}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!(level.getBlockEntity(pos) instanceof TandemEntity tile))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var source=player.getMainHandItem();
        // Source paper branch precedes the sneak check, has no item consumption, and copies only editor settings.
        if(source.is(Items.PAPER)){
            if(!level.isClientSide){if(!source.has(DataComponents.CUSTOM_DATA))source.set(DataComponents.CUSTOM_DATA,CustomData.of(tile.settings()));else tile.edit(source.get(DataComponents.CUSTOM_DATA).copyTag());}
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if(player.isShiftKeyDown())return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(source.getItem() instanceof BlockItem item){
            Block block=item.getBlock();if(block==ConstructionWandContent.AIR_MARKER.get())block=Blocks.AIR;
            if(!StructureBlacklist.isAuthoring(block,false)){
                if(!level.isClientSide){
                    BlockState replacement=block.defaultBlockState();
                    if(source.has(HbmHazards.LEGACY_METADATA.get()))replacement=com.hbm.world.structure.LegacyStructureStates.resolve(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString(),source.get(HbmHazards.LEGACY_METADATA.get()),0);
                    var intrinsic=source.get(DataComponents.BLOCK_STATE);if(intrinsic!=null)replacement=intrinsic.apply(replacement);
                    tile.replaceWith(replacement);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if(source.is(StructureWandContent.WAND.get()))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(player.isShiftKeyDown()||player.getMainHandItem().is(StructureWandContent.WAND.get()))return InteractionResult.PASS;
        if(level instanceof ServerLevel server&&level.getBlockEntity(pos) instanceof TandemEntity tile)TandemPackets.open(server,player,tile);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
