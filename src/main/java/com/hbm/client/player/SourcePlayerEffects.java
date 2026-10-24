package com.hbm.client.player;

import com.hbm.armor.HazmatRegistry;
import com.hbm.particle.LegacyParticles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original ModEventHandler.onPlayerTick client-side author aura, using the existing 1.7 particle core. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class SourcePlayerEffects {
    private static final RandomSource RANDOM = RandomSource.create();

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (!player.level().isClientSide || player.isInvisible() || player.isShiftKeyDown()
                || !player.getUUID().equals(HazmatRegistry.SPECIAL_UUID)) return;
        var offset = new Vec3(3 * RANDOM.nextDouble(), 0, 0)
                .zRot((float) (RANDOM.nextDouble() * Math.PI))
                .yRot((float) (RANDOM.nextDouble() * Math.PI * 2));
        player.level().addParticle(LegacyParticles.AURA.get(), player.getX() + offset.x,
                player.getY() + 1 + offset.y, player.getZ() + offset.z, 0, 0, 0);
    }

    private SourcePlayerEffects() { }
}
