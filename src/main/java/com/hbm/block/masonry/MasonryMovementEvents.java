package com.hbm.block.masonry;

import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original ModEventHandler.START step receiver, once per tick at the player's foot cell. */
@EventBusSubscriber(modid="hbm")
public final class MasonryMovementEvents {
    @SubscribeEvent public static void tick(PlayerTickEvent.Pre event) {
        var player=event.getEntity();
        if (!player.level().isClientSide || player.getAbilities().flying || player.zza==0 && player.xxa==0) return;
        var state=player.level().getBlockState(BlockPos.containing(player.getX(),player.getY()-.01,player.getZ()));
        if (state.getBlock() instanceof AsphaltBlock || state.getBlock() instanceof LegacyMultiSlabBlock slab && slab.materialName(state.getValue(LegacyMultiSlabBlock.MATERIAL)).equals("asphalt")) player.setDeltaMovement(player.getDeltaMovement().multiply(1.5,1,1.5));
    }
    private MasonryMovementEvents() { }
}
