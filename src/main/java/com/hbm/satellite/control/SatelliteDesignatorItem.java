package com.hbm.satellite.control;
import com.hbm.satellite.*;import com.hbm.satellite.item.SatelliteChipItem;import net.minecraft.world.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.*;
/** Exact source300block Library.rayTrace + hit-side adjacent coordinate for true satellite onCoordAction. */
public final class SatelliteDesignatorItem extends SatelliteChipItem {
 @Override public InteractionResultHolder<ItemStack> use(Level world,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(world instanceof net.minecraft.server.level.ServerLevel level){var sat=SatelliteSavedData.getData(level).getSatFromFreq(getFreq(stack));if(sat!=null){var start=player.getEyePosition(1F);var end=start.add(player.getViewVector(1F).scale(300));var hit=world.clip(new ClipContext(start,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,player));var position=hit.getBlockPos().relative(hit.getDirection());sat.onCoordAction(level,player,position.getX(),position.getY(),position.getZ());}}return InteractionResultHolder.sidedSuccess(stack,world.isClientSide);}
}
