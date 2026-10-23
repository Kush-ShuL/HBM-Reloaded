package com.hbm.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original table has a temporary player container, no tile inventory or ticker. */
public final class ArmorTableBlock extends Block {
    public ArmorTableBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(player.isShiftKeyDown())return InteractionResult.PASS;
        player.openMenu(new SimpleMenuProvider((id,inventory,owner)->new ArmorTableMenu(id,inventory),Component.translatable("container.armorTable")));
        return InteractionResult.CONSUME;
    }
}
