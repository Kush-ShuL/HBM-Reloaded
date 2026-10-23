package com.hbm.client.armor.diesel;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Source vanilla1.7 EntitySmokeFX plus its bnuuy smokeParticleScale=.5 assignment; sole existing smoke material/queue. */
public final class DieselSmokeParticle extends Particle {
    private static final ParticleRenderType SMOKE = MukeRenderTypes.cloudTexture(TextureAtlas.LOCATION_PARTICLES);
    private final float shade;
    public DieselSmokeParticle(ClientLevel level, double x, double y, double z, double mx, double mz) {
        super(level, x, y, z);
        double vx = (float) (Math.random() * 2 - 1) * .4F, vy = (float) (Math.random() * 2 - 1) * .4F, vz = (float) (Math.random() * 2 - 1) * .4F;
        float magnitude = (float) (Math.random() + Math.random() + 1) * .15F;
        float length = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        xd = vx / length * magnitude * .4000000059604645 * .10000000149011612 + mx;
        yd = (vy / length * magnitude * .4000000059604645 + .10000000149011612) * .10000000149011612;
        zd = vz / length * magnitude * .4000000059604645 * .10000000149011612 + mz;
        shade = (float) (Math.random() * .30000001192092896);
        lifetime = (int) (8D / (Math.random() * .8D + .2D));
        hasPhysics = true;
    }
    @Override public void move(double dx, double dy, double dz) {
        // Retain old smoke sliding along ceilings; modern Particle's permanent stoppedByCollision is not the source behavior.
        Vec3 actual = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), getBoundingBox(), level, List.of());
        onGround = dy != actual.y && dy < 0;
        setPos(x + actual.x, y + actual.y, z + actual.z);
        if (dx != actual.x) xd = 0; if (dy != actual.y) yd = 0; if (dz != actual.z) zd = 0;
    }
    @Override public void tick() {
        xo = x; yo = y; zo = z;
        if (age++ >= lifetime) remove();
        yd += .004; move(xd, yd, zd);
        if (y == yo) { xd *= 1.1; zd *= 1.1; }
        xd *= .9599999785423279; yd *= .9599999785423279; zd *= .9599999785423279;
        if (onGround) { xd *= .699999988079071; zd *= .699999988079071; }
    }
    @Override public ParticleRenderType getRenderType() { return MukeRenderTypes.ORDERED; }
    @Override public void render(VertexConsumer ignored, Camera camera, float partial) {
        if (MukeRenderTypes.defer(this, partial)) return;
        var out = MukeRenderTypes.begin(SMOKE);
        var eye = camera.getPosition(); int frame = Math.max(0, 7 - age * 8 / lifetime);
        var sprite = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES))
            .getSprite(ResourceLocation.withDefaultNamespace("generic_" + frame));
        float scale = .1F * .5F * Mth.clamp((age + partial) / lifetime * 32F, 0, 1);
        float px = (float) (Mth.lerp(partial, xo, x) - eye.x), py = (float) (Mth.lerp(partial, yo, y) - eye.y), pz = (float) (Mth.lerp(partial, zo, z) - eye.z);
        var corners = new org.joml.Vector3f[] {new org.joml.Vector3f(-1, -1, 0), new org.joml.Vector3f(-1, 1, 0), new org.joml.Vector3f(1, 1, 0), new org.joml.Vector3f(1, -1, 0)};
        float[] us = {sprite.getU1(), sprite.getU1(), sprite.getU0(), sprite.getU0()}, vs = {sprite.getV1(), sprite.getV0(), sprite.getV0(), sprite.getV1()};
        for (int i = 0; i < 4; i++) {
            var vertex = corners[i].rotate(camera.rotation()).mul(scale).add(px, py, pz);
            out.addVertex(vertex.x, vertex.y, vertex.z).setUv(us[i], vs[i]).setColor(shade, shade, shade, 1).setLight(getLightColor(partial));
        }
    }
}
