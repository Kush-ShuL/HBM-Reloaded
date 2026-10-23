package com.hbm.client.particle;

import com.hbm.particle.LegacyParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** 1.7.10 EntityAuraFX (bla): original tile, gray/cyan color, UV epsilon and final-tick ordering. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class LegacyAuraParticles {
    private LegacyAuraParticles() { }
    @SubscribeEvent public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LegacyParticles.AURA.get(), sprites ->
                (type, level, x, y, z, vx, vy, vz) -> new Aura(level, x, y, z, vx, vy, vz, sprites, false));
        event.registerSpriteSet(LegacyParticles.SCHRAB_AURA.get(), sprites ->
                (type, level, x, y, z, vx, vy, vz) -> new Aura(level, x, y, z, vx, vy, vz, sprites, true));
    }

    private static final class Aura extends SuspendedTownParticle {
        Aura(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites, boolean cyan) {
            super(level, x, y, z, vx, vy, vz);
            pickSprite(sprites);
            if (cyan) setColor(0, 1, 1);
        }
        @Override protected float getU1() { return sprite.getU(.0624375F / .0625F); }
        @Override protected float getV1() { return sprite.getV(.0624375F / .0625F); }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            move(xd, yd, zd);
            xd *= .99D; yd *= .99D; zd *= .99D;
            if (lifetime-- <= 0) remove();
        }
    }
}
