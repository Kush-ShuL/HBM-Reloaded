package com.hbm.client.jetpack;

import com.hbm.client.model.*;
import com.hbm.equipment.jetpack.JetpackPartItem.Part;
import com.hbm.equipment.jetpack.ModularJetpackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public final class ModularJetpackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/armor/jetpack.obj");
    public ModularJetpackRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    public static void renderPack(ItemStack stack, boolean inSlot, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        part(mesh, "Base", "base", pose, buffers, light, overlay);
        var thruster=ModularJetpackItem.getInstalledThruster(stack);
        if(thruster!=null)renderPart(mesh,thruster,inSlot,pose,buffers,light,overlay);
        var tank=ModularJetpackItem.getInstalledTank(stack);
        if(tank!=null){
            renderPart(mesh,tank,inSlot,pose,buffers,light,overlay);
            if(ModularJetpackItem.isPartInstalled(stack,Part.EXTENSION_TANKS))renderPart(mesh,Part.EXTENSION_TANKS,inSlot,pose,buffers,light,overlay);
        }
        var flight=ModularJetpackItem.getInstalledFlightModel(stack);
        if(flight!=null)renderPart(mesh,flight,inSlot,pose,buffers,light,overlay);
    }
    private static void renderPart(WavefrontMesh mesh,Part installed,boolean inSlot,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        switch(installed){
            case TANK_COALGAS,TANK_KEROSENE,TANK_JETFUEL->part(mesh,"Tank","tank",pose,buffers,light,overlay);
            case EXTENSION_TANKS->part(mesh,"ExtensionTanks","tank",pose,buffers,light,overlay);
            case THRUSTERS->part(mesh,"Thrusters","thruster",pose,buffers,light,overlay);
            case THRUSTER_LARGE->part(mesh,"ThrusterLarge","thruster_large",pose,buffers,light,overlay);
            case STABILIZER->part(mesh,"Stabilizer","stabilizer",pose,buffers,light,overlay);
            case WINGS->{
                int extension=inSlot?12:0;
                pose.pushPose();pose.translate(-extension,0,0);part(mesh,"WingLeft","wings",pose,buffers,light,overlay);pose.popPose();
                pose.pushPose();pose.translate(extension,0,0);part(mesh,"WingRight","wings",pose,buffers,light,overlay);pose.popPose();
            }
        }
    }
    private static void part(WavefrontMesh mesh, String group, String texture, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        LegacyObjRenderer.renderOnly(mesh, pose, buffers.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hbm:textures/models/jetpack/jetpack_"+texture+".png"))), light, overlay, -1, LegacyObjRenderer.Convention.HFR, group);
    }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        pose.pushPose(); LegacyItemTransforms.held(pose, context);
        if (context == ItemDisplayContext.GUI) {
            pose.translate(0, 1, .5); pose.scale(1F/16, -1F/16, 1F/16); pose.translate(8, 10, 0);
            pose.mulPose(Axis.XP.rotationDegrees(-30)); pose.mulPose(Axis.YP.rotationDegrees(45)); pose.scale(-1, -1, -1);
            pose.scale(1.125F, 1.125F, 1.125F);
            pose.translate(3, -13, 0);
        } else {
            if (context == ItemDisplayContext.GROUND) { pose.translate(.5, 0, .5); pose.scale(1.5F, 1.5F, 1.5F); }
            else pose.translate(.5, .25, 0);
            pose.scale(.25F, .25F, .25F);
            if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) pose.mulPose(Axis.YP.rotationDegrees(90));
            pose.translate(0, -6, 1);
            pose.scale(.375F, .375F, .375F);
        }
        pose.mulPose(Axis.XP.rotationDegrees(180)); pose.translate(0, -24, 0); pose.scale(.5625F, .5625F, .5625F);
        renderPack(stack, context == ItemDisplayContext.GUI, pose, buffers, light, overlay);
        pose.popPose();
    }
}
