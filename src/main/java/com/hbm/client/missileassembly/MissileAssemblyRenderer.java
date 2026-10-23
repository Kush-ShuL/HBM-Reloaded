package com.hbm.client.missileassembly;

import com.hbm.client.missile.custom.CustomMissileModels;
import com.hbm.client.model.*;
import com.hbm.machine.missileassembly.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Original machine mesh, source rotation/strut loop and horizontally assembled real component preview. */
public final class MissileAssemblyRenderer implements BlockEntityRenderer<MissileAssemblyBlockEntity> {
    public static final ResourceLocation MODEL = MissileAssemblyContent.id("models/missile_assembly.obj"), TEXTURE = MissileAssemblyContent.id("textures/models/missile_assembly.png");
    private static final ResourceLocation STRUT = MissileAssemblyContent.id("models/strut.obj"), STRUT_TEXTURE = MissileAssemblyContent.id("textures/models/strut.png");
    public MissileAssemblyRenderer(BlockEntityRendererProvider.Context context) {}
    public static void body(PoseStack poses, MultiBufferSource buffers, int light, int overlay) { CustomMissileModels.mesh(MODEL, TEXTURE, true, poses, buffers, light, overlay); }
    @Override public void render(MissileAssemblyBlockEntity machine, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        float rotation = switch (machine.getBlockState().getValue(MissileAssemblyBlock.FACING)) { case NORTH -> 180; case WEST -> 270; case EAST -> 90; default -> 0; };
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        LegacyContactShadows.render(machine, mesh, new Matrix4f().translation(.5F, 0, .5F).rotateY((float) Math.toRadians(rotation)), poses, buffers);
        poses.pushPose();
        try {
            poses.translate(.5, 0, .5); poses.mulPose(Axis.YP.rotationDegrees(rotation)); body(poses, buffers, light, overlay);
            var missile = CustomMissileModels.fromStruct(machine.load); if (missile == null) return;
            // While the menu is open, source slots may arrive before the periodic multipart packet.
            var slots = CustomMissileModels.fromSlots(machine);
            missile = new CustomMissileModels.Multipart(machine.getItem(1).isEmpty() ? missile.warhead() : slots.warhead(),
                    machine.getItem(2).isEmpty() ? missile.fuselage() : slots.fuselage(), machine.getItem(3).isEmpty() ? missile.fins() : slots.fins(),
                    machine.getItem(4).isEmpty() ? missile.thruster() : slots.thruster());
            int range = (int) (missile.height() / 2 - 1), step = range >= 2 ? 2 : 1;
            for (int i = -range; i <= range; i += step) if (i != 0) {
                poses.pushPose(); poses.translate(i, 0, 0); CustomMissileModels.mesh(STRUT, STRUT_TEXTURE, true, poses, buffers, light, overlay); poses.popPose();
            }
            poses.translate(0, 1.5, 0); poses.mulPose(Axis.ZP.rotationDegrees(180)); poses.translate(-missile.height() / 2, 0, 0);
            poses.mulPose(Axis.XP.rotationDegrees(-90)); poses.mulPose(Axis.ZP.rotationDegrees(-90)); CustomMissileModels.draw(missile, poses, buffers, light, overlay);
        } finally { poses.popPose(); }
    }
    @Override public boolean shouldRenderOffScreen(MissileAssemblyBlockEntity machine) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(MissileAssemblyBlockEntity machine) {
        var pos = machine.getBlockPos(); return new AABB(pos.getX() - 16, pos.getY() - 2, pos.getZ() - 16, pos.getX() + 17, pos.getY() + 8, pos.getZ() + 17);
    }
}
