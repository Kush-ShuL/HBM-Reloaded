package com.hbm.client.radiation;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.radiation.RadiationFogEffects;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** ParticleRadiationFog's actual fixed-position, seeded25-billboard visual. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RadiationFogClient {
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(RadiationFogEffects.FOG.get(),
                (type, level, x, y, z, vx, vy, vz) -> new Fog(level, x, y, z));
    }

    private static final class Fog extends SingleQuadParticle {
        private static final ParticleRenderType TYPE = MukeRenderTypes.cloudTexture(
                ResourceLocation.parse("hbm:textures/particle/fog.png"), true);

        Fog(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
            lifetime = 100 + random.nextInt(40);
            quadSize = 7.5F;
            rCol = .85F; gCol = .9F; bCol = .5F;
        }

        @Override public void tick() {
            xo = x; yo = y; zo = z;
            // Source's first update promotes its100..139 constructor lifetime to400.
            if (lifetime < 400) lifetime = 400;
            if (++age >= lifetime) remove();
            // Source does not call move; changing velocity never moves this fog.
            xd *= .9599999785423279D; yd *= .9599999785423279D; zd *= .9599999785423279D;
            if (onGround) { xd *= .699999988079071D; zd *= .699999988079071D; }
        }

        @Override public void render(VertexConsumer out, Camera camera, float partial) {
            if (MukeRenderTypes.defer(this, partial)) return;
            out = MukeRenderTypes.begin(TYPE);
            alpha = (float) Math.sin(age * Math.PI / 400F) * .125F;
            var random = new Random(50);
            var cam = camera.getPosition();
            float translatedX = 0, translatedY = 0, translatedZ = 0;
            for (int i = 0; i < 25; i++) {
                translatedX += (float) ((random.nextGaussian() - 1D) * 2.5D);
                translatedY += (float) ((random.nextGaussian() - 1D) * .15D);
                translatedZ += (float) ((random.nextGaussian() - 1D) * 2.5D);
                quadSize = (float) (random.nextDouble() * 7.5F);
                // Source GL translations accumulate across all25 quads, not independently.
                float px = (float) (Mth.lerp(partial, xo, x) - cam.x + random.nextGaussian() * .5);
                float py = (float) (Mth.lerp(partial, yo, y) - cam.y + random.nextGaussian() * .5);
                float pz = (float) (Mth.lerp(partial, zo, z) - cam.z + random.nextGaussian() * .5);
                renderRotatedQuad(out, camera.rotation(), px + translatedX, py + translatedY,
                        pz + translatedZ, partial);
            }
        }

        @Override protected float getU0() { return 0; }
        @Override protected float getU1() { return 1; }
        @Override protected float getV0() { return 0; }
        @Override protected float getV1() { return 1; }
        @Override protected int getLightColor(float partial) { return 240; }
        @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(float partial) {
            return net.minecraft.world.phys.AABB.INFINITE;
        }
        @Override public ParticleRenderType getRenderType() { return MukeRenderTypes.ORDERED; }
    }

    private RadiationFogClient() {}
}
