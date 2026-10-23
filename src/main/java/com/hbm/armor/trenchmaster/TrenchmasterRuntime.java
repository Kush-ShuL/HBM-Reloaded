package com.hbm.armor.trenchmaster;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original transient HbmPlayerProps.plinkCooldown only; effects/attack dispatch remain the sole existing FSB paths. */
public final class TrenchmasterRuntime {
    private static final Map<Player, Integer> PLINK_COOLDOWN = Collections.synchronizedMap(new WeakHashMap<>());

    public static void plink(Player player, float pitch) {
        synchronized (PLINK_COOLDOWN) {
            if (PLINK_COOLDOWN.getOrDefault(player, 0) > 0) return;
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, .5F, pitch);
            PLINK_COOLDOWN.put(player, 10);
        }
    }

    static void tick(PlayerTickEvent.Post event) {
        // Source handlePlinking decrements every living tick, including after the armor is removed.
        // Its cooldown was never saved/cloned; weak player identity retains that actual lifetime.
        synchronized (PLINK_COOLDOWN) {
            Integer ticks = PLINK_COOLDOWN.get(event.getEntity());
            if (ticks == null) return;
            if (ticks <= 1) PLINK_COOLDOWN.remove(event.getEntity());
            else PLINK_COOLDOWN.put(event.getEntity(), ticks - 1);
        }
    }
    private TrenchmasterRuntime() {}
}
