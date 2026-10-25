package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleRenderType;

/** The actual source smoke emitter, with no simulation, deferred submission or feedback. */
public interface SourceSmokeGeometry {
    ParticleRenderType smokeMaterial();
    void emitSmokeGeometry(VertexConsumer vertices, Camera camera, float partial);
}
