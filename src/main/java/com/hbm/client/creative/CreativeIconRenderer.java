package com.hbm.client.creative;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmCreativeTabs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original inventory matrices, applied in their original 16px GUI coordinate system. */
public final class CreativeIconRenderer extends BlockEntityWithoutLevelRenderer {
    public CreativeIconRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        if(context!=ItemDisplayContext.GUI) return;
        int index=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);
        String tab=HbmCreativeTabs.ORDER.get(Math.max(0,Math.min(HbmCreativeTabs.ORDER.size()-1,index)));
        var spec=HbmCreativeTabs.iconModels().get(tab);
        if(spec==null || !"obj".equals(spec.renderer())) return;
        poses.pushPose();
        // Modern item rendering supplies model-local0..1 axes; restore the original top-left GUI pixel frame.
        poses.translate(0,1,.5); poses.scale(1/16F,-1/16F,1/16F);
        switch(tab) {
            case "nuke" -> {
                poses.translate(8,10,0); poses.mulPose(Axis.XP.rotationDegrees(-30)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.scale(-1,-1,-1);
                poses.translate(0,-2,0); poses.scale(5,5,5); poses.mulPose(Axis.YP.rotationDegrees(180)); poses.translate(-.75,0,0);
            }
            case "missile" -> {
                poses.scale(1.375F,1.375F,1.375F); poses.mulPose(Axis.ZP.rotationDegrees(135));
                poses.mulPose(Axis.YP.rotationDegrees(System.currentTimeMillis()/15%360)); poses.translate(0,-14.5,0);
            }
            case "weapon" -> {
                poses.scale(1,1,-1); poses.translate(8,8,0); poses.mulPose(Axis.ZP.rotationDegrees(225)); poses.mulPose(Axis.YP.rotationDegrees(90));
                poses.scale(1.5F,1.5F,1.5F); poses.mulPose(Axis.XP.rotationDegrees(25)); poses.mulPose(Axis.YP.rotationDegrees(45)); poses.translate(-.5,2,0);
            }
        }
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(spec.model()));
        var texture=ResourceLocation.parse(spec.texture());
        var type=tab.equals("weapon")?RenderType.entityCutout(texture):RenderType.entityCutoutNoCull(texture);
        LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(type),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);
        poses.popPose();
    }
}
