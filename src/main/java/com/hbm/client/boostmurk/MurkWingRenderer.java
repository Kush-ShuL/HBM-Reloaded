package com.hbm.client.boostmurk;

import com.hbm.client.jetpack.ModelJetPack;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.equipment.boostmurk.SourceWingsItem;
import com.hbm.equipment.jetpack.SourceJetpackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Literal ModelArmorWings four source groups, original body frame and every left/right wing pivot. */
public final class MurkWingRenderer {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/armor/murk.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/armor/wings_murk.png"),BOOST=ResourceLocation.parse("hbm:textures/models/jetpack.png");
    private static final ModelJetPack BOOST_MODEL=new ModelJetPack();
    public static void render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity wearer,ItemStack stack,SourceWingsItem item,HumanoidModel<?> parent){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var vertices=ItemRenderer.getArmorFoilBuffer(buffers,RenderType.entityCutout(TEXTURE),stack.hasFoil());
        double rot=Math.sin(wearer.tickCount*.2D)*20,rot2=Math.sin(wearer.tickCount*.2D-Math.PI*.5D)*50+30;
        if(!item.limp&&wearer.onGround()){rot=20;rot2=160;}
        if(item.limp){if(wearer.onGround()){rot=30;rot2=-30;}else if(wearer.getDeltaMovement().y<-.1){rot=0;rot2=10;}else{rot=30;rot2=20;}}
        poses.pushPose();try{
            var body=parent.body;poses.translate(body.x/16F,body.y/16F,body.z/16F);
            if(body.zRot!=0)poses.mulPose(Axis.ZP.rotation(body.zRot));if(body.yRot!=0)poses.mulPose(Axis.YP.rotation(body.yRot));if(body.xRot!=0)poses.mulPose(Axis.XP.rotation(body.xRot));
            poses.translate(0,-2D/16,0);
            side(mesh,poses,vertices,light,rot,rot2,false);side(mesh,poses,vertices,light,rot,rot2,true);
        }finally{poses.popPose();}
    }
    private static void side(WavefrontMesh mesh,PoseStack poses,VertexConsumer vertices,int light,double rot,double rot2,boolean right){
        double sign=right?-1:1;poses.pushPose();try{
            rotate(poses,Axis.YP,-10*sign);
            poses.translate(sign/16D,5D/16,3D/16);rotate(poses,Axis.YP,rot*.5*sign);rotate(poses,Axis.ZP,(rot+5)*sign);rotate(poses,Axis.XP,45);poses.translate(-sign/16D,-5D/16,-3D/16);
            poses.translate(sign/16D,5D/16,3D/16);rotate(poses,Axis.ZP,rot*sign);poses.translate(-sign/16D,-5D/16,-3D/16);
            part(mesh,poses,vertices,light,right?"RightBase":"LeftBase");
            poses.translate(sign,5D/16,2D/16);rotate(poses,Axis.YP,rot2*sign);rotate(poses,Axis.ZP,(rot2*.25+5)*sign);poses.translate(-sign,-5D/16,-2D/16);
            part(mesh,poses,vertices,light,right?"RightTip":"LeftTip");
        }finally{poses.popPose();}
    }
    private static void rotate(PoseStack poses,Axis axis,double degrees){poses.mulPose(axis.rotationDegrees((float)degrees));}
    private static void part(WavefrontMesh mesh,PoseStack poses,VertexConsumer vertices,int light,String name){poses.pushPose();try{poses.scale(1F/16,1F/16,1F/16);LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,name);}finally{poses.popPose();}}
    /** The native armor layer skips non-ArmorItem standalone chest items; players already use the one JetpackLayer. */
    public static boolean renderNonPlayer(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent,float swing,float amount,float age,float yaw,float pitch){
        if(entity instanceof net.minecraft.world.entity.player.Player||slot!=EquipmentSlot.CHEST)return false;
        var stack=entity.getItemBySlot(slot);
        if(stack.getItem() instanceof SourceWingsItem wings){render(poses,buffers,light,entity,stack,wings,parent);return true;}
        if(stack.getItem() instanceof SourceJetpackItem jetpack&&jetpack.kind==SourceJetpackItem.Kind.BOOST){
            BOOST_MODEL.isRiding=parent.riding;BOOST_MODEL.isChild=entity.isBaby();BOOST_MODEL.onGround=parent.attackTime;BOOST_MODEL.isSneak=entity.isCrouching();
            BOOST_MODEL.renderToBuffer(poses,ItemRenderer.getArmorFoilBuffer(buffers,RenderType.armorCutoutNoCull(BOOST),stack.hasFoil()),light,OverlayTexture.NO_OVERLAY,-1,entity,swing,amount,age,yaw,pitch,1F/16);
            return true;
        }
        return false;
    }
    private MurkWingRenderer(){}
}
