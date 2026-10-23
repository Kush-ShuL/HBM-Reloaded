package com.hbm.client.press;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.machine.press.PressBlockEntity;
import com.hbm.multiblock.MultiblockRenderBounds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;

/** Original AdvancedModelLoader models with original RenderPress transform order. */
public final class PressRenderer implements BlockEntityRenderer<PressBlockEntity> {
    static final ResourceLocation BODY=ResourceLocation.fromNamespaceAndPath("hbm","models/press_body.obj");
    static final ResourceLocation HEAD=ResourceLocation.fromNamespaceAndPath("hbm","models/press_head.obj");
    static final ResourceLocation BODY_TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/press_body.png");
    static final ResourceLocation HEAD_TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/press_head.png");
    public PressRenderer(BlockEntityRendererProvider.Context context) {}
    static void mesh(ResourceLocation model,ResourceLocation texture,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model),poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);
    }
    @Override public void render(PressBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose(); poses.translate(.5,0,.5); poses.mulPose(Axis.YP.rotationDegrees(180));
        mesh(BODY,BODY_TEXTURE,poses,buffers,light,overlay); poses.popPose();
        poses.pushPose(); poses.translate(.5,0,.5); poses.scale(.99F,1,.99F);
        poses.translate(0,Mth.clamp(1-machine.renderPress(partial)/200D,0,1)*.875,0);
        mesh(HEAD,HEAD_TEXTURE,poses,buffers,light,overlay); poses.popPose();
        if(!machine.renderedInput().isEmpty()) {
            poses.pushPose(); poses.translate(.5,1,-.5); poses.mulPose(Axis.YP.rotationDegrees(180)); poses.mulPose(Axis.XP.rotationDegrees(-90));
            poses.translate(0,1-.0625F*165/100,0);
            // RenderItem.renderInFrame's zero-hover, single item has no time rotation or bob.
            poses.scale(.5F,.5F,.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(machine.renderedInput().copyWithCount(1),ItemDisplayContext.FIXED,light,overlay,poses,buffers,machine.getLevel(),0);
            poses.popPose();
        }
    }
    @Override public boolean shouldRenderOffScreen(PressBlockEntity machine) { return true; }
    @Override public AABB getRenderBoundingBox(PressBlockEntity machine) {
        return MultiblockRenderBounds.of(machine.getBlockPos(), machine.getBlockState());
    }
    @Override public int getViewDistance() { return 256; }
}
