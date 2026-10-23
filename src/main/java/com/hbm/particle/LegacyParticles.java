package com.hbm.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, "hbm");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AURA = PARTICLES.register("legacy_aura", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SCHRAB_AURA = PARTICLES.register("legacy_schrab_aura", () -> new SimpleParticleType(false));
    private LegacyParticles() { }
    public static void register(IEventBus bus) { PARTICLES.register(bus); }
}
