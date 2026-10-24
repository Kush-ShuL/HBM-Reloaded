package com.hbm.config;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** The original 528-mode right-click consequence for Applied Energistics blocks. */
@EventBusSubscriber(modid = "hbm")
public final class ExplosiveEnergisticsEvents {
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        if (level.isClientSide || !GeneralModes.explosiveEnergistics()) return;
        var pos = event.getPos();
        // The existing optional certus-quartz integration also uses modern AE2's actual namespace.
        var blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock());
        if (!blockId.getNamespace().equals("ae2")) return;

        level.destroyBlock(pos, false);
        var explosion = new ExplosionVNT(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 5, null);
        explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1, 20).setupPiercing(5, .2F));
        explosion.setPlayerProcessor(new PlayerProcessorStandard());
        explosion.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        explosion.explode();
        event.setCanceled(true);
    }

    private ExplosiveEnergisticsEvents() {}
}
