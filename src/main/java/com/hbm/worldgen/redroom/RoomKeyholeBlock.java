package com.hbm.worldgen.redroom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Source accepts a horizontal click even on a side other than its marked face. */
public final class RoomKeyholeBlock extends RoomBrickBlock {
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(RedRooms.BRICK.get());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return RedRoomGenerator.open(level,pos,player,hit.getDirection(),true);}
}
