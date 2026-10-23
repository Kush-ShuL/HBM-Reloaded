package com.hbm.entity.mob.duck;

import com.hbm.registry.HbmSounds;
import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** The existing duck's once-per-player login/button consumer, not another entity or spawn backend. */
public final class DuckCall {
    private static final String HAS_DUCKED = "hasDucked";
    private static boolean registered;

    public static void register(IEventBus bus, ModContainer container) {
        if (registered) return;
        registered = true;
        DuckCallConfig.register(container);
        DuckCallPayloads.register(bus);
        NeoForge.EVENT_BUS.addListener(DuckCall::login);
    }

    private static boolean hasDucked(ServerPlayer player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getBoolean(HAS_DUCKED);
    }

    private static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && DuckCallConfig.enabled() && !hasDucked(player)) {
            PacketDistributor.sendToPlayer(player, new DuckCallPayloads.Reminder());
        }
    }

    static void summon(ServerPlayer player) {
        if (!DuckCallConfig.enabled() || hasDucked(player)) return;
        var level = player.serverLevel();
        var duck = Objects.requireNonNull(DuckContent.DUCK.get().create(level), "Registered duck factory returned no duck");
        duck.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        duck.setDeltaMovement(player.getLookAngle());
        level.addFreshEntity(duck);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("entity.ducc"),
                player.getSoundSource(), 1F, 1F);
        // The original marks the player after the spawn call, even if an entity-join listener rejects the spawn.
        var persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putBoolean(HAS_DUCKED, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
    }
    private DuckCall() {}
}
