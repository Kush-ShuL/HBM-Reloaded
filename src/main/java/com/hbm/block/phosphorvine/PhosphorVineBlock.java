package com.hbm.block.phosphorvine;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.IShearable;

/** Actual BlockHangingVine, not vanilla VineBlock: no growth, wall attachments or native climbing. */
public final class PhosphorVineBlock extends Block implements IShearable {
    public PhosphorVineBlock(){super(Properties.of().mapColor(MapColor.PLANT).strength(.5F,.5F).sound(SoundType.GRASS).noCollission().noOcclusion().requiresCorrectToolForDrops().ignitedByLava().pushReaction(PushReaction.DESTROY));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var abovePos=pos.above();var above=level.getBlockState(abovePos);return above.is(this)||above.isFaceSturdy(level,abovePos,Direction.UP);}
    @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){entity.setDeltaMovement(entity.getDeltaMovement().scale(.5D));entity.fallDistance=0F;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){super.neighborChanged(state,level,pos,neighbor,from,moving);if(level instanceof ServerLevel server)PhosphorVinesContent.checkSupport(server,pos);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){PhosphorVinesContent.checkSupport(level,pos);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&level instanceof ServerLevel server)PhosphorVinesContent.removed(server,pos);super.onRemove(state,level,pos,replacement,moving);}
    @Override public boolean isShearable(Player player,ItemStack tool,Level level,BlockPos pos){return true;}
    @Override public List<ItemStack> onSheared(Player player,ItemStack tool,Level level,BlockPos pos){return List.of(new ItemStack(this));}
}
