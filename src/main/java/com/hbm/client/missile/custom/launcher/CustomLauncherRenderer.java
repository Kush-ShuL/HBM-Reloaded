package com.hbm.client.missile.custom.launcher;

import com.hbm.client.missile.custom.CustomMissileModels;
import com.hbm.client.model.LegacyContactShadows;
import com.hbm.missile.custom.CustomMissileParts.PartSize;
import com.hbm.missile.custom.launcher.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Exact RenderCompactLauncher and RenderLaunchTable, original size-controlled scaffolds and actual multipart missile. */
public final class CustomLauncherRenderer implements BlockEntityRenderer<CustomLauncherEntity> {
    public CustomLauncherRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(CustomLauncherEntity machine, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        float angle = machine.large ? switch (CustomLauncherBlock.orientation(machine.getBlockState())) { case 0 -> 90; case 1 -> 270; case 3 -> 180; default -> 0; } : 0;
        String base = machine.large ? "launch_table_base" : "compact_launcher";
        LegacyContactShadows.render(machine, CustomLauncherModels.mesh(base), new Matrix4f().translation(.5F, 0, .5F).rotateY((float) Math.toRadians(angle)), poses, buffers);
        poses.pushPose();
        try {
            poses.translate(.5, 0, .5); poses.mulPose(Axis.YP.rotationDegrees(angle)); CustomLauncherModels.draw(base, poses, buffers, light, overlay);
            var missile = CustomMissileModels.fromStruct(machine.load);
            if (!machine.large) { poses.translate(0, 1.0625, 0); CustomMissileModels.draw(missile, poses, buffers, light, overlay); return; }
            if (machine.padSize == PartSize.SIZE_10 || machine.padSize == PartSize.SIZE_15) CustomLauncherModels.draw("launch_table_small_pad", poses, buffers, light, overlay);
            if (machine.padSize == PartSize.SIZE_20) CustomLauncherModels.draw("launch_table_large_pad", poses, buffers, light, overlay);
            if (missile != null && missile.fuselage() != null) machine.height = (int) missile.height();
            boolean matches = machine.load != null && machine.load.fuselage() != null && machine.load.fuselage().definition.top() == machine.padSize;
            String frame = machine.padSize == PartSize.SIZE_10 ? "launch_table_small_scaffold_" : "launch_table_large_scaffold_";
            int connectorHeight = (int) (machine.height * .75);
            poses.pushPose();
            try {
                if (machine.padSize == PartSize.SIZE_10) poses.translate(0, 0, -1);
                poses.translate(0, 1, 3.5);
                for (int i = 0; i < machine.height + 1; i++) {
                    CustomLauncherModels.draw(frame + (i < connectorHeight || i == connectorHeight && !matches ? "base" : i > connectorHeight ? "empty" : "connector"), poses, buffers, light, overlay);
                    poses.translate(0, 1, 0);
                }
            } finally { poses.popPose(); }
            poses.translate(0, 2.0625, 0); if (matches) CustomMissileModels.draw(missile, poses, buffers, light, overlay);
        } finally { poses.popPose(); }
    }
    @Override public boolean shouldRenderOffScreen(CustomLauncherEntity machine) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(CustomLauncherEntity machine) { var p = machine.getBlockPos(); return new AABB(p.getX() - 6, p.getY() - 1, p.getZ() - 6, p.getX() + 7, p.getY() + 40, p.getZ() + 7); }
}
