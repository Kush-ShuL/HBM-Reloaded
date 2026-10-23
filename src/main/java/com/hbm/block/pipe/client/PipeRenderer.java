package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.hbm.block.pipe.NativePipeBlock;
import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.fluid.HbmFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Original RenderTestPipe part selection, authored HFR UVs, overlay and colors. */
public final class PipeRenderer implements BlockEntityRenderer<NativePipeBlockEntity> {
    private static final ResourceLocation MODEL = HbmReloaded.id("models/blocks/pipe_neo.obj");
    private static final String[] TEXTURES = {"pipe_neo", "pipe_silver", "pipe_colored"};
    public PipeRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(NativePipeBlockEntity pipe, float partialTick, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (pipe.kind() != com.hbm.block.pipe.PipeKind.STANDARD && pipe.kind() != com.hbm.block.pipe.PipeKind.VALVE) {
            SpecialPipeRenderer.render(pipe, poses, buffers, light, overlay); return;
        }
        if (((NativePipeBlock) pipe.getBlockState().getBlock()).valve()) return;
        int mask = NativePipeBlock.mask(pipe.getLevel(), pipe.getBlockPos(), pipe.getFluidType());
        poses.pushPose(); poses.translate(0.5, 0.5, 0.5);
        draw(poses, buffers, light, overlay, pipe.getBlockState().getValue(NativePipeBlock.VARIANT), pipe.getFluidType().getColor(), true, parts(mask));
        poses.popPose();
    }
    public static void draw(PoseStack poses, MultiBufferSource buffers, int light, int overlay, int variant, int color, boolean shadow, String... parts) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        String texture = TEXTURES[variant];
        var base = HbmReloaded.id("textures/block/" + texture + ".png"); var tint = HbmReloaded.id("textures/block/" + texture + "_overlay.png");
        PipeMeshRenderer.render(mesh, poses, buffers.getBuffer(shadow ? PipeRenderTypes.world(base) : RenderType.entityCutout(base)), light, overlay, 0xFFFFFF, shadow, parts);
        PipeMeshRenderer.render(mesh, poses, buffers.getBuffer(shadow ? PipeRenderTypes.world(tint) : RenderType.entityCutout(tint)), light, overlay, color, shadow, parts);
    }
    @Override public boolean shouldRenderOffScreen(NativePipeBlockEntity pipe) { return pipe.kind().anchor(); }
    @Override public int getViewDistance() { return 256; }
    @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(NativePipeBlockEntity pipe) {
        var bounds = new net.minecraft.world.phys.AABB(pipe.getBlockPos());
        if (pipe.kind().anchor()) for (var target : pipe.connected()) bounds = bounds.minmax(new net.minecraft.world.phys.AABB(target));
        return bounds.inflate(0.125);
    }
    public static String[] parts(int mask) {
        var result = new java.util.ArrayList<String>();
        if (mask == 0) return new String[]{"pX", "nX", "pY", "nY", "pZ", "nZ"};
        if (mask == 32 || mask == 16) return new String[]{"pX", "nX"};
        if (mask == 8 || mask == 4) return new String[]{"pY", "nY"};
        if (mask == 2 || mask == 1) return new String[]{"pZ", "nZ"};
        if ((mask & 32) != 0) result.add("pX"); if ((mask & 16) != 0) result.add("nX");
        if ((mask & 8) != 0) result.add("pY"); if ((mask & 4) != 0) result.add("nY");
        // Authored OBJ names use the opposite Z sign: nZ is z=0..+.5, pZ is z=-.5..0.
        if ((mask & 2) != 0) result.add("nZ"); if ((mask & 1) != 0) result.add("pZ");
        String[] corners = {"ppn", "ppp", "npn", "npp", "pnn", "pnp", "nnn", "nnp"};
        int[] directions = {32 | 8 | 2, 32 | 8 | 1, 16 | 8 | 2, 16 | 8 | 1, 32 | 4 | 2, 32 | 4 | 1, 16 | 4 | 2, 16 | 4 | 1};
        for (int i = 0; i < corners.length; i++) if ((mask & directions[i]) == 0) result.add(corners[i]);
        return result.toArray(String[]::new);
    }
}
