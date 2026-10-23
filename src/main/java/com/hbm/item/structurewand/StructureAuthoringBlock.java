package com.hbm.item.structurewand;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

public final class StructureAuthoringBlock extends BaseEntityBlock {
    private static final MapCodec<StructureAuthoringBlock> CODEC=RecordCodecBuilder.mapCodec(builder->builder.group(propertiesCodec(),Codec.BOOL.fieldOf("loading").forGetter(block->block.loading)).apply(builder,StructureAuthoringBlock::new));
    public final boolean loading;
    public StructureAuthoringBlock(boolean loading){this(Properties.of().mapColor(MapColor.METAL).strength(0,0),loading);}
    private StructureAuthoringBlock(Properties properties,boolean loading){super(properties);this.loading=loading;}
    @Override protected MapCodec<? extends StructureAuthoringBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new StructureAuthoringEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,StructureWandContent.ENTITY.get(),StructureAuthoringEntity::tick);}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(player.isShiftKeyDown())return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var source=player.getMainHandItem();
        if(source.getItem() instanceof BlockItem item&&!StructureBlacklist.isAuthoring(item.getBlock(),true)&&level.getBlockEntity(pos) instanceof StructureAuthoringEntity tile){if(!level.isClientSide)tile.toggleBlock(item.getBlock(),source);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level instanceof ServerLevel server&&level.getBlockEntity(pos) instanceof StructureAuthoringEntity tile)StructureWandPackets.open(server,player,tile);return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(asItem()));}
    @Override public ItemStack getCloneItemStack(BlockState state,net.minecraft.world.phys.HitResult hit,LevelReader level,BlockPos pos,Player player){return new ItemStack(asItem());}
}
