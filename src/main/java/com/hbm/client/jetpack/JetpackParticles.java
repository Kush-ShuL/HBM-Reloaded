package com.hbm.client.jetpack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

/** Original ClientProxy jetpack handler: paired nozzles, source mode0/1 thrust, ground dust and quality gates. */
public final class JetpackParticles {
    public static void exhaust(int entityId, int mode) {
        var mc = Minecraft.getInstance(); var world = mc.level;
        if (world == null || mc.options.particles().get() == ParticleStatus.MINIMAL || !(world.getEntity(entityId) instanceof Player player)) return;
        boolean blackjack = mode == 2, dns = mode == 4;
        // Original Vec3.rotateAroundY: x'=x*cos+z*sin, z'=z*cos-x*sin; angle uses body yaw, not head yaw.
        float angle = (float) -Math.toRadians(player.yBodyRot), cosine = Mth.cos(angle), sine = Mth.sin(angle);
        double behind = dns ? 0 : blackjack ? .3125 : .25;
        double vx = -behind * sine, vz = -behind * cosine, ox = .125 * cosine, oz = -.125 * sine;
        double x = player.getX() + vx, y = dns ? player.getY() - .5 : player.getY() + player.getEyeHeight() - (blackjack ? .9375 : 1), z = player.getZ() + vz;
        Vec3 thrust = mode == 1 ? player.getLookAngle().scale(-.1) : new Vec3(0, blackjack || dns ? -1 : -.2, 0);
        boolean all = mc.options.particles().get() == ParticleStatus.ALL;
        if (all) {
            var start = new Vec3(x, y, z); var target = start.add(sourceNormalize(thrust).scale(10));
            var hit = world.clip(new ClipContext(start, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hit.getType() == HitResult.Type.BLOCK && hit.getDirection() == Direction.UP) {
                double distance = (float) Math.sqrt(start.distanceToSqr(hit.getLocation())), speed = .75 - distance * .075;
                Vec3 velocity = new Vec3(speed, 0, 0); var state = world.getBlockState(hit.getBlockPos());
                for (int i = 0; i < 10 - distance; i++) {
                    float rotation = world.random.nextFloat() * (float) Math.PI * 2F, c = Mth.cos(rotation), s = Mth.sin(rotation);
                    velocity = new Vec3(velocity.x * c + velocity.z * s, 0, velocity.z * c - velocity.x * s);
                    mc.particleEngine.createParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), hit.getLocation().x, hit.getLocation().y + .1, hit.getLocation().z, velocity.x, .1, velocity.z);
                }
            }
        }
        if (blackjack || dns) {
            var velocity = player.getDeltaMovement();
            com.hbm.client.entity.crab.CrabParticles.colored(world, new Vec3(x + ox, y, z + oz), velocity, dns ? .01F : .8F, dns ? 1F : .5F, 1F);
            com.hbm.client.entity.crab.CrabParticles.colored(world, new Vec3(x - ox, y, z - oz), velocity, dns ? .01F : .8F, dns ? 1F : .5F, 1F);
            return;
        }
        var motion = player.getDeltaMovement().add(thrust.scale(2));
        double mx = clamp(motion.x, 5), my = clamp(motion.y, 5), mz = clamp(motion.z, 5);
        mc.particleEngine.createParticle(ParticleTypes.FLAME, x + ox, y, z + oz, mx, my, mz);
        mc.particleEngine.createParticle(ParticleTypes.FLAME, x - ox, y, z - oz, mx, my, mz);
        if (all) {
            mx = clamp(motion.x, 10); my = clamp(motion.y, 10); mz = clamp(motion.z, 10);
            mc.particleEngine.createParticle(ParticleTypes.SMOKE, x + ox, y, z + oz, mx, my, mz);
            mc.particleEngine.createParticle(ParticleTypes.SMOKE, x - ox, y, z - oz, mx, my, mz);
        }
    }
    private static Vec3 sourceNormalize(Vec3 value) { double length = (float) Math.sqrt(value.lengthSqr()); return length < 1E-4 ? Vec3.ZERO : value.scale(1 / length); }
    private static double clamp(double value, double limit) { return Math.max(-limit, Math.min(limit, value)); }
    private JetpackParticles() { }
}
