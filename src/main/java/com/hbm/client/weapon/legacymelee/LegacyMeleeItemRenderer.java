package com.hbm.client.weapon.legacymelee;

import com.hbm.client.model.*;
import com.hbm.weapon.legacymelee.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Exact ItemRenderShim and ItemRenderBigSword local matrices; ordinary GUI icons remain native. */
public final class LegacyMeleeItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final ModelBigSword bigSword=new ModelBigSword();
    public LegacyMeleeItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        boolean big=stack.getItem() instanceof BigSwordItem;
        poses.pushPose();
        try {
            if(LegacyItemTransforms.isHeld(context))LegacyItemTransforms.heldFull3D(poses,context);
            else {poses.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);poses.scale(.5F,.5F,.5F);}
            if(big){
                poses.mulPose(Axis.ZP.rotationDegrees(-135));poses.mulPose(Axis.YP.rotationDegrees(90));poses.translate(0,.4F,-.7F);
                bigSword.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(id("textures/models/modelbigswordtexture.png"))),light,overlay,-1,1F/16F);
                return;
            }
            var kind=((LegacySpecialWeaponItem)stack.getItem()).kind;
            boolean sign=kind==LegacySpecialWeaponItem.Kind.STOP||kind==LegacySpecialWeaponItem.Kind.SOP||kind==LegacySpecialWeaponItem.Kind.CHERNOBYL;
            if(context.firstPerson()&&sign){poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-90));poses.translate(-1,-1.5F,0);}
            String obj,texture;
            if(sign){
                poses.mulPose(Axis.ZP.rotationDegrees(45));poses.scale(.35F,.35F,.35F);poses.translate(2,-2,0);poses.mulPose(Axis.YP.rotationDegrees(90));
                obj="models/weapons/stopsign.obj";texture="textures/models/weapons/"+switch(kind){case STOP->"stopsign";case SOP->"sopsign";default->"chernobylsign";}+".png";
            } else {
                poses.mulPose(Axis.ZP.rotationDegrees(-135));poses.mulPose(Axis.ZP.rotationDegrees(180));poses.scale(1.5F,1.5F,1.5F);poses.translate(.45F,-.3F,0);
                String source=kind==LegacySpecialWeaponItem.Kind.SLEDGE?"shimmer_sledge":"shimmer_axe";
                obj="models/"+source+".obj";texture="textures/models/"+source+".png";
            }
            var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id(obj));
            LegacyObjRenderer.renderAll(model,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(id(texture))),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);
        }finally{poses.popPose();}
    }
}
