package com.hbm.worldgen.redroom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** BlockStone semantics; its source four side keyholes use the requested deepslate host. */
public final class StrangeStoneBlock extends Block {
    public StrangeStoneBlock(){super(Properties.of().strength(1.5F,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(Blocks.DEEPSLATE);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return RedRoomGenerator.open(level,pos,player,hit.getDirection(),false);}
}
