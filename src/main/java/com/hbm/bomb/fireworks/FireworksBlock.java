package com.hbm.bomb.fireworks;

import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original charge/color/name-tag interaction, not a menu/HE-powered firework launcher. */
public final class FireworksBlock extends BaseEntityBlock {
    private static final MapCodec<FireworksBlock> CODEC=simpleCodec(ignored->new FireworksBlock());
    public FireworksBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(0F,3F).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FireworksBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world,BlockState state,BlockEntityType<T> type){return world.isClientSide?null:createTickerHelper(type,FireworksContent.TILE.get(),FireworksBlockEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){
        if(world.isClientSide)return InteractionResult.SUCCESS;
        if(!(world.getBlockEntity(pos) instanceof FireworksBlockEntity tile))return InteractionResult.PASS;
        var held=player.getMainHandItem();
        if(!held.isEmpty()){
            if(held.is(Items.GUNPOWDER)){tile.charges+=held.getCount()*3;tile.setChanged();held.setCount(0);return InteractionResult.CONSUME;}
            if(held.is(HbmContent.item("sulfur"))){tile.charges+=held.getCount();tile.setChanged();held.setCount(0);return InteractionResult.CONSUME;}
            if(held.getItem() instanceof DyeItem dye){tile.color=dye.getDyeColor().getFireworkColor();tile.setChanged();held.shrink(1);return InteractionResult.CONSUME;}
            if(held.is(Items.NAME_TAG)){tile.message=held.getHoverName().getString();tile.setChanged();held.shrink(1);return InteractionResult.CONSUME;}
        }
        player.sendSystemMessage(Component.translatable("block.hbm.fireworks").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable("tile.fireworks.charges",tile.charges).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable("tile.fireworks.color",Integer.toHexString(tile.color)).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable("tile.fireworks.message",tile.message).withStyle(ChatFormatting.YELLOW));
        return InteractionResult.CONSUME;
    }
}
