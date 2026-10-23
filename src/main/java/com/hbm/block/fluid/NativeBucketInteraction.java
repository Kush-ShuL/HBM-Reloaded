package com.hbm.block.fluid;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Source BucketHandler pickup, without exposing a transportable NeoForge fluid. */
public final class NativeBucketInteraction {
    private NativeBucketInteraction() {}

    public static void pickup(PlayerInteractEvent.RightClickItem event) {
        var player = event.getEntity();
        var held = player.getItemInHand(event.getHand());
        if (!held.is(Items.BUCKET)) return;
        var level = player.level();
        var eye = player.getEyePosition();
        var end = eye.add(player.getViewVector(1F).scale(player.blockInteractionRange()));
        var hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
        if (hit.getType() != HitResult.Type.BLOCK) return;
        var pos = hit.getBlockPos();
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof LegacyFluidBlock fluid) || fluid.spec().bucket() == null) return;
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos.relative(hit.getDirection()), hit.getDirection(), held)) return;
        var filled = fluid.pickupNative(level, pos, state);
        if (filled.isEmpty()) return;
        player.awardStat(Stats.ITEM_USED.get(Items.BUCKET));
        player.playSound(SoundEvents.BUCKET_FILL, 1F, 1F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(held, player, filled));
        if (player instanceof ServerPlayer serverPlayer) CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, filled);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
