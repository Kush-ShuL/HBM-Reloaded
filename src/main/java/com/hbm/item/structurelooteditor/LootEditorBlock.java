package com.hbm.item.structurelooteditor;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.hazard.HbmHazards;
import com.hbm.interfaces.IBomb;
import com.hbm.item.lock.*;
import com.hbm.world.structure.LegacyStructureStates;
import com.hbm.world.structure.loot.StructureLootContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Genuine tool-driven Loot authoring cube; not a fabricated GUI or loot generator. */
public final class LootEditorBlock extends BaseEntityBlock implements ToolableBlock,IBomb {
    public static final IntegerProperty ROTATION=IntegerProperty.create("rotation",0,3);
    public LootEditorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(0,0).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(ROTATION,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(ROTATION);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new LootEditorEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,LootEditorContent.ENTITY.get(),LootEditorEntity::tick);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(ROTATION,new int[]{3,2,0,1}[yaw]);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){if(level.getBlockEntity(pos) instanceof LootEditorEntity tile){tile.placedRotation=player.getYRot();tile.setChanged();}}
    // Source has INBTTileEntityTransformable, not INBTBlockTransformable: texture orientation is not structurally rotated.
    private static BlockState selected(ItemStack stack){
        if(!(stack.getItem() instanceof BlockItem item))return null;
        var block=item.getBlock();var state=block.defaultBlockState();
        if(stack.has(HbmHazards.LEGACY_METADATA.get()))state=LegacyStructureStates.resolve(BuiltInRegistries.BLOCK.getKey(block).toString(),stack.get(HbmHazards.LEGACY_METADATA.get()),0);
        var intrinsic=stack.get(DataComponents.BLOCK_STATE);return intrinsic==null?state:intrinsic.apply(state);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(hand!=InteractionHand.MAIN_HAND||!(level.getBlockEntity(pos) instanceof LootEditorEntity tile))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(!player.isShiftKeyDown()){
            var replacement=selected(stack);
            if(replacement!=null&&LootEditorEntity.lootable(pos,replacement)){
                if(level instanceof net.minecraft.server.level.ServerLevel server){tile.replacement=replacement;var pools=tile.poolNames(server);if(!pools.contains(tile.pool))tile.pool=pools.getFirst();tile.changed();}
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if(stack.getItem() instanceof PadlockItem lock){if(!level.isClientSide){tile.lockMod=(float)lock.lockMod;tile.lockCode=KeyPinItem.getPins(stack);tile.changed();}}
        // Original returns false after copying a lock. Padlock.useOn sees this is not a lockable host and consumes nothing.
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return InteractionResult.PASS;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){
        if(!(level.getBlockEntity(pos) instanceof LootEditorEntity tile)||tool!=ToolType.SCREWDRIVER&&tool!=ToolType.HAND_DRILL&&tool!=ToolType.DEFUSER)return false;
        if(level.isClientSide)return true;
        switch(tool){
            case SCREWDRIVER->{if(player.isShiftKeyDown()){tile.minimum--;if(tile.minimum<0)tile.minimum=0;}else{tile.minimum++;tile.maximum=Math.max(tile.minimum,tile.maximum);}}
            case HAND_DRILL->{if(player.isShiftKeyDown()){tile.maximum--;if(tile.maximum<0)tile.maximum=0;tile.minimum=Math.min(tile.minimum,tile.maximum);}else tile.maximum++;}
            case DEFUSER->{var names=tile.poolNames((net.minecraft.server.level.ServerLevel)level);int index=names.indexOf(tile.pool)+(player.isShiftKeyDown()?-1:1);tile.pool=names.get(Mth.clamp(index,0,names.size()-1));}
            default->{}
        }
        tile.changed();return true;
    }
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(!(level.getBlockEntity(new BlockPos(x,y,z)) instanceof LootEditorEntity tile))return null;
        if(!level.isClientSide){tile.trigger=true;tile.setChanged();LootEditorPlacement.enlist(tile);}return BombReturnCode.TRIGGERED;
    }
}
