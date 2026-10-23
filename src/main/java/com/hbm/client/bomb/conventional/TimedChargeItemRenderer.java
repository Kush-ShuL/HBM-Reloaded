package com.hbm.client.bomb.conventional;

import com.hbm.client.gui.font.LegacyFonts;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Item presentation shares the complete authored charge mesh and its mounted green timer face. */
public final class TimedChargeItemRenderer extends BlockEntityWithoutLevelRenderer {
    public TimedChargeItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var model=Minecraft.getInstance().getItemRenderer().getModel(stack,null,null,0);
        var vertices=buffers.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        for(var quad:model.getQuads(null,null,RandomSource.create(0)))vertices.putBulkData(poses.last(),quad,1,1,1,1,light,overlay);
        poses.pushPose();poses.translate(.5,.5,.5);poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(-90));poses.translate(0,.375,0);
        poses.translate(-.05,.315F-.5F,.15);poses.scale(.0125F,-.0125F,.0125F);poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.XP.rotationDegrees(90));
        LegacyFonts.get().drawInBatch("00:00",0,0,0x00FF00,false,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,light);poses.popPose();
    }
}
