package com.hbm.client.weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

/** Modern draw state for migrated source rendererBeam callbacks, scoped to one render invocation. */
public record SednaBeamRenderContext(PoseStack poses,MultiBufferSource buffers,int light) {
    private static final ThreadLocal<SednaBeamRenderContext> ACTIVE=new ThreadLocal<>();
    public static SednaBeamRenderContext current(){var value=ACTIVE.get();if(value==null)throw new IllegalStateException("No beam render is active");return value;}
    static void begin(PoseStack poses,MultiBufferSource buffers,int light){ACTIVE.set(new SednaBeamRenderContext(poses,buffers,light));}
    static void end(){ACTIVE.remove();}
}
