package com.hbm.client.render.mixin;

import java.util.Map;
import java.util.Queue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Only admitted live particles; the pending-add queue remains owned by the native engine. */
@Mixin(ParticleEngine.class)
public interface SmokeParticleEngineAccess {
    @Accessor("particles")
    Map<ParticleRenderType, Queue<Particle>> hbm$smokeParticles();
}
