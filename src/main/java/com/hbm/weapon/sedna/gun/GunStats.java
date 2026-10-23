package com.hbm.weapon.sedna.gun;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Source statBullets increments once per fired round, not once per shotgun pellet. */
public final class GunStats {
    private static final DeferredRegister<ResourceLocation> STATS=DeferredRegister.create(Registries.CUSTOM_STAT,"hbm");
    public static final DeferredHolder<ResourceLocation,ResourceLocation> BULLETS=STATS.register("ntm_bullets",()->ResourceLocation.parse("hbm:ntm_bullets"));
    public static void register(IEventBus bus){STATS.register(bus);}
    public static void bulletFired(Player player){player.awardStat(Stats.CUSTOM.get(BULLETS.get()),1);}
    private GunStats(){}
}
