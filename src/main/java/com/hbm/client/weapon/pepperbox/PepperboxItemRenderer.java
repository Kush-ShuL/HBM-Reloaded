package com.hbm.client.weapon.pepperbox;

import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;

/** ItemRenderPepperbox and ItemRenderWeaponBase, retaining the six original HFR parts and cumulative GL matrices. */
public final class PepperboxItemRenderer extends BlockEntityWithoutLevelRenderer implements GunFirstPersonRenderer {
    private static final ResourceLocation OBJ=ResourceLocation.parse("hbm:models/weapons/pepperbox.obj"),TEX=ResourceLocation.parse("hbm:textures/models/weapons/pepperbox.png");
    public PepperboxItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    private static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),OBJ);
        LegacyObjRenderer.render(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEX)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart(name));
    }
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();
        try(var hand=GunHandTransforms.held(pose,context)){
        if(context.firstPerson()){
            // Normal gameplay uses the original custom projection via RenderHandEvent. This fallback is for external previews.
            pose.translate(.5,.5,.5);pose.mulPose(Axis.YP.rotationDegrees(180));renderFirstPerson(stack,pose,buffers,light,overlay,0);
        }else{
            if(context==ItemDisplayContext.GUI){
                // Inverse of native GUI T(8,8), S(16,-16,16), ItemRenderer T(-.5), then source pixel projection z=-3.
                pose.translate(0,1,.3125);pose.scale(1F/16,-1F/16,1F/16);
                pose.scale(1,1,-1);pose.translate(8,8,0);pose.mulPose(Axis.ZP.rotationDegrees(225));pose.mulPose(Axis.YP.rotationDegrees(90));
                pose.scale(1.5F,1.5F,1.5F);pose.mulPose(Axis.XP.rotationDegrees(25));pose.mulPose(Axis.YP.rotationDegrees(45));pose.translate(.5,.5,0);
            }else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                pose.scale(.125F,.125F,.125F);pose.mulPose(Axis.ZP.rotationDegrees(15));pose.mulPose(Axis.YP.rotationDegrees(12.5F));pose.mulPose(Axis.XP.rotationDegrees(15));pose.translate(3.5,0,0);pose.translate(0,1,3);
            }else{
                pose.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);pose.scale(.5F,.5F,.5F);pose.scale(.125F,.125F,.125F);pose.mulPose(Axis.YP.rotationDegrees(-90));
            }
            for(var part:List.of("Grip","Cylinder","Hammer","Trigger"))part(part,pose,buffers,light,overlay);
            if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
                var owner=GunRenderOwner.current();if(owner!=null){var gun=(ItemGunBaseNT)stack.getItem();boolean local=owner==Minecraft.getInstance().player;long shot=local?gun.lastShot[0]:GunClient.FLASHES.getOrDefault(owner,-1L);muzzle(pose,buffers,shot,local?gun.shotRand:0);}
            }
        }
        }finally{pose.popPose();}
    }
    @Override public void renderModificationTable(ItemStack stack,int configuration,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial){pose.pushPose();pose.scale(-7.5F,-7.5F,-7.5F);pose.mulPose(Axis.YP.rotationDegrees(90));for(var part:List.of("Grip","Cylinder","Hammer","Trigger"))part(part,pose,buffers,light,overlay);pose.popPose();}
    public void renderFirstPerson(ItemStack stack,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial){
        var gun=(ItemGunBaseNT)stack.getItem();float aim=Mth.lerp(partial,ItemGunBaseNT.prevAimingProgress,ItemGunBaseNT.aimingProgress);
        pose.translate(0,0,1.5);pose.translate(-1+aim,-.6+(-2.5/8D+.6)*aim,.8+(.5-.8)*aim);pose.scale(.25F,.25F,.25F);
        var recoil=HbmAnimations.getRelevantTransformation("RECOIL");var cylinder=HbmAnimations.getRelevantTransformation("ROTATE");var hammer=HbmAnimations.getRelevantTransformation("HAMMER");var trigger=HbmAnimations.getRelevantTransformation("TRIGGER");var translate=HbmAnimations.getRelevantTransformation("TRANSLATE");var loader=HbmAnimations.getRelevantTransformation("LOADER");var shot=HbmAnimations.getRelevantTransformation("SHOT");
        pose.translate(translate[0],translate[1],translate[2]);pose.translate(0,0,-5);pose.mulPose(Axis.XN.rotationDegrees((float)recoil[0]));pose.translate(0,0,5);
        pose.pushPose();pose.translate(0,.5,7);pose.mulPose(Axis.YP.rotationDegrees(90));smoke(gun.getConfig(stack,0).smokeNodes,.5,pose,buffers,light);pose.popPose();
        if(loader[0]!=0||loader[1]!=0||loader[2]!=0){pose.pushPose();pose.translate(loader[0],loader[1],loader[2]);part("Speedloader",pose,buffers,light,overlay);if(shot[0]!=0)part("Shot",pose,buffers,light,overlay);pose.popPose();}
        part("Grip",pose,buffers,light,overlay);
        pose.pushPose();pose.mulPose(Axis.ZP.rotationDegrees((float)cylinder[0]));part("Cylinder",pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(0,.375,-1.875);pose.mulPose(Axis.XP.rotationDegrees((float)hammer[0]));pose.translate(0,-.375,1.875);part("Hammer",pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(0,0,-trigger[0]*.5);part("Trigger",pose,buffers,light,overlay);pose.popPose();
        muzzle(pose,buffers,gun.lastShot[0],gun.shotRand);
    }
    private static void muzzle(PoseStack pose,MultiBufferSource buffers,long lastShot,double random){pose.pushPose();pose.translate(0,.5,7);pose.scale(.5F,.5F,.5F);pose.mulPose(Axis.YP.rotationDegrees(90));pose.mulPose(Axis.XP.rotationDegrees((float)(90*random)));flash(lastShot,pose,buffers);pose.mulPose(Axis.XP.rotationDegrees(45));flash(lastShot,pose,buffers);pose.popPose();}
    private static void smoke(List<ItemGunBaseNT.SmokeNode> nodes,double scale,PoseStack pose,MultiBufferSource buffers,int light){
        if(nodes.size()<2)return;var out=buffers.getBuffer(GunRenderTypes.SMOKE);
        for(int i=0;i<nodes.size()-1;i++){var n=nodes.get(i);var p=nodes.get(i+1);for(int side:new int[]{1,-1}){
            vertex(out,pose,n.forward,n.lift,n.side,0,0,(float)n.alpha,light);vertex(out,pose,n.forward,n.lift,n.side+side*n.width*scale,0,0,0,light);vertex(out,pose,p.forward,p.lift,p.side+side*p.width*scale,0,0,0,light);vertex(out,pose,p.forward,p.lift,p.side,0,0,(float)p.alpha,light);
        }}
    }
    private static void flash(long lastShot,PoseStack pose,MultiBufferSource buffers){
        long elapsed=System.currentTimeMillis()-lastShot;if(elapsed>=75)return;double fire=elapsed/75D,width=6*fire,length=15*fire,inset=2;var out=buffers.getBuffer(GunRenderTypes.FLASH);
        double[][] v={{0,-width,-inset,1,1},{0,width,-inset,0,1},{.1,width,length-inset,0,0},{.1,-width,length-inset,1,0},{0,width,inset,0,1},{0,-width,inset,1,1},{.1,-width,-length+inset,1,0},{.1,width,-length+inset,0,0},{0,-inset,width,0,1},{0,-inset,-width,1,1},{.1,length-inset,-width,1,0},{.1,length-inset,width,0,0},{0,inset,-width,1,1},{0,inset,width,0,1},{.1,-length+inset,width,0,0},{.1,-length+inset,-width,1,0}};
        for(var q:v)vertex(out,pose,q[0],q[1],q[2],(float)q[3],(float)q[4],1,240);
    }
    private static void vertex(VertexConsumer out,PoseStack pose,double x,double y,double z,float u,float v,float alpha,int light){out.addVertex(pose.last(),(float)x,(float)y,(float)z).setColor(1,1,1,Mth.clamp(alpha,0,1)).setUv(u,v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);}
}
