package com.hbm.client.bomb.conventional;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original ItemRenderDetonatorLaser, including the oscillating trace and three half-second numeric displays. */
public final class LaserDetonatorRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/weapons/detonator_laser.obj"), TEXTURE = ResourceLocation.parse("hbm:textures/models/weapons/detonator_laser.png");
    public LaserDetonatorRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose();
        com.hbm.client.model.LegacyItemTransforms.heldFull3D(poses,context);
        switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> { poses.scale(.25F,.25F,.25F); poses.mulPose(Axis.YP.rotationDegrees(80)); poses.mulPose(Axis.XP.rotationDegrees(-20)); poses.translate(1,.5,3); }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> { poses.scale(-.125F,-.125F,-.125F); poses.mulPose(Axis.YP.rotationDegrees(85)); poses.mulPose(Axis.XP.rotationDegrees(145)); poses.translate(-.5,-1,6.5); }
            case GROUND -> { poses.translate(.5,0,.5); poses.mulPose(Axis.YP.rotationDegrees(-90)); poses.scale(.25F,.25F,.25F); }
            case GUI -> { poses.translate(0,1,.5); poses.scale(1/16F,-1/16F,1/16F); poses.scale(3.5F,3.5F,-3.5F); poses.translate(1.5,2.75,0); poses.mulPose(Axis.XP.rotationDegrees(180)); poses.mulPose(Axis.YP.rotationDegrees(-90)); poses.mulPose(Axis.XP.rotationDegrees(-45)); }
            default -> { }
        }
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        LegacyObjRenderer.renderOnly(mesh, poses, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay, 0xFFFFFFFF, LegacyObjRenderer.Convention.HFR, "Main");
        var solid = buffers.getBuffer(ExplosiveRenderTypes.UNLIT);
        for (var face : mesh.faces()) {
            if (!face.legacyPart().equalsIgnoreCase("Lights")) continue;
            var corners = face.corners();
            if (corners.size() == 4) for (var corner : corners) { var p = mesh.positions().get(corner.position()); solid.addVertex(poses.last(), p.x(), p.y(), p.z()).setColor(0xFFFF0000); }
            else for (int i=1; i+1<corners.size(); i++) for (int index : new int[]{0,i,i+1,i+1}) { var p = mesh.positions().get(corners.get(index).position()); solid.addVertex(poses.last(), p.x(), p.y(), p.z()).setColor(0xFFFF0000); }
        }
        poses.pushPose(); poses.translate(.5626F, .0625F*18, -.0625F*14);
        double segment = .5 / 32, time = System.currentTimeMillis() / -100D;
        for (int i=0;i<32;i++) {
            double h0=Math.sin(i*.5+time)*.075,h1=Math.sin((i+1)*.5+time)*.075;
            solid.addVertex(poses.last(),0,(float)(-.0625F*.25F+h1),(float)(segment*(i+1))).setColor(0xFFFFFF00);
            solid.addVertex(poses.last(),0,(float)(.0625F*.25F+h1),(float)(segment*(i+1))).setColor(0xFFFFFF00);
            solid.addVertex(poses.last(),0,(float)(.0625F*.25F+h0),(float)(segment*i)).setColor(0xFFFFFF00);
            solid.addVertex(poses.last(),0,(float)(-.0625F*.25F+h0),(float)(segment*i)).setColor(0xFFFFFF00);
        }
        poses.popPose(); poses.pushPose();
        poses.translate(.5625F,1.3125F,.875F); poses.scale(.01F,-.01F,.01F); poses.mulPose(Axis.YP.rotationDegrees(90)); poses.translate(3,-2,.2);
        var random = new Random(System.currentTimeMillis()/500);
        for(int i=0;i<3;i++) { LegacyFonts.get().drawInBatch(Integer.toString(random.nextInt(900000)+100000),0,0,0xFF0000,false,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT); poses.translate(0,12.5,0); }
        poses.popPose(); poses.popPose();
    }
}
