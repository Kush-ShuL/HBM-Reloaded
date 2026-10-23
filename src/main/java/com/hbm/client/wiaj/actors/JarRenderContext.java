package com.hbm.client.wiaj.actors;
import com.mojang.blaze3d.vertex.PoseStack;import net.minecraft.client.renderer.MultiBufferSource;
/** Current source camera transform is explicit rather than mutable OpenGL global state. */
public record JarRenderContext(PoseStack pose,MultiBufferSource buffers){}
