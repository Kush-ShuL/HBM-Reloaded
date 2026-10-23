package com.hbm.client.weapon.pepperbox;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

/** Actual per-call draw state for original BulletConfig renderer callbacks; null renderer still emits nothing. */
public record GunBulletRenderContext(PoseStack poses,MultiBufferSource buffers,int light) {
    private static final ThreadLocal<GunBulletRenderContext> ACTIVE=new ThreadLocal<>();
    public static GunBulletRenderContext current(){var value=ACTIVE.get();if(value==null)throw new IllegalStateException("No MK4 renderer is active");return value;}
    public static void begin(PoseStack poses,MultiBufferSource buffers,int light){ACTIVE.set(new GunBulletRenderContext(poses,buffers,light));}
    public static void end(){ACTIVE.remove();}
}
