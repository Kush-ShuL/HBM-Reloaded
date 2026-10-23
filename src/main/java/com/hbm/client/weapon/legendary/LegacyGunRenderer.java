package com.hbm.client.weapon.legendary;

import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

/** Source ItemRenderWeaponBase transforms and primitive effects, expressed through modern buffers. */
public abstract class LegacyGunRenderer extends BlockEntityWithoutLevelRenderer implements GunFirstPersonRenderer {
    protected enum ItemRenderType{EQUIPPED_FIRST_PERSON,EQUIPPED,INVENTORY,ENTITY}
    protected static float interp;
    protected static final Map<LivingEntity,Long> flashMap=GunClient.FLASHES;
    protected LegacyGunRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    protected float getTurnMagnitude(ItemStack stack){return 2.75F;}
    public float turnMagnitude(ItemStack stack){return getTurnMagnitude(stack);}
    protected float getBaseFOV(ItemStack stack){return 70F;}
    @Override public float modelFov(ItemStack stack,float partial){interp=partial;return getBaseFOV(stack);}
    public float getViewFOV(ItemStack stack,float fov){return fov;}
    public float viewFov(ItemStack stack,float fov,float partial){interp=partial;return getViewFOV(stack,fov);}
    public final void firstPerson(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float partial){interp=partial;poses.pushPose();try(var scope=LegacyGunGL.push(poses,buffers,light,overlay)){setupFirstPerson(stack);renderFirstPerson(stack);}catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}}
    @Override public final void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        if(context.firstPerson()){poses.pushPose();try(var hand=GunHandTransforms.held(poses,context)){poses.translate(.5,.5,.5);poses.mulPose(Axis.YP.rotationDegrees(180));firstPerson(stack,poses,buffers,light,overlay,0);}finally{poses.popPose();}return;}
        poses.pushPose();try(var hand=GunHandTransforms.held(poses,context);var scope=LegacyGunGL.push(poses,buffers,light,overlay)){
            ItemRenderType type;
            if(context==ItemDisplayContext.GUI){poses.translate(0,1,.3125);poses.scale(1F/16,-1F/16,1F/16);setupInv(stack);type=ItemRenderType.INVENTORY;}
            else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){setupThirdPerson(stack);type=ItemRenderType.EQUIPPED;}
            else{poses.translate(.5,context==ItemDisplayContext.GROUND?.25:.5,.5);poses.scale(.5F,.5F,.5F);setupEntity(stack);type=ItemRenderType.ENTITY;}
            var owner=GunRenderOwner.current();if(type==ItemRenderType.INVENTORY)renderInv(stack);else if(type==ItemRenderType.ENTITY)renderEntity(stack);else renderEquipped(stack,null,owner);
        }catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}
    }
    public void setupFirstPerson(ItemStack stack){LegacyGunGL.glTranslated(0,0,1);}
    public void setupThirdPerson(ItemStack stack){LegacyGunGL.glScaled(.125,.125,.125);LegacyGunGL.glRotated(15,0,0,1);LegacyGunGL.glRotated(12.5,0,1,0);LegacyGunGL.glRotated(15,1,0,0);LegacyGunGL.glTranslated(3.5,0,0);}
    public void setupInv(ItemStack stack){LegacyGunGL.glScaled(1,1,-1);LegacyGunGL.glTranslated(8,8,0);LegacyGunGL.glRotated(225,0,0,1);LegacyGunGL.glRotated(90,0,1,0);}
    public void setupEntity(ItemStack stack){LegacyGunGL.glScaled(.125,.125,.125);LegacyGunGL.glRotated(-90,0,1,0);}
    public void setupModTable(ItemStack stack){LegacyGunGL.glScaled(-5,-5,-5);LegacyGunGL.glRotated(90,0,1,0);}
    @Override public final void renderFirstPerson(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float partial){firstPerson(stack,poses,buffers,light,overlay,partial);}
    @Override public final void renderModificationTable(ItemStack stack,int configuration,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float partial){interp=partial;poses.pushPose();try(var scope=LegacyGunGL.push(poses,buffers,light,overlay)){setupModTable(stack);renderModTable(stack,configuration);}catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}}
    public boolean isAkimbo(LivingEntity entity){return false;}
    public void setupThirdPersonAkimbo(ItemStack stack){LegacyGunGL.glScaled(.125,.125,.125);LegacyGunGL.glRotated(15,0,0,1);LegacyGunGL.glRotated(12.5,0,1,0);LegacyGunGL.glRotated(10,1,0,0);LegacyGunGL.glTranslated(5,0,0);}
    public void renderEquipped(ItemStack stack,Object... data){renderOther(stack,ItemRenderType.EQUIPPED,data);}
    public void renderEquippedAkimbo(ItemStack stack,LivingEntity entity){renderOther(stack,ItemRenderType.EQUIPPED);}
    public void renderInv(ItemStack stack){renderOther(stack,ItemRenderType.INVENTORY);}
    public void renderEntity(ItemStack stack){renderOther(stack,ItemRenderType.ENTITY);}
    public void renderModTable(ItemStack stack,int index){renderOther(stack,ItemRenderType.INVENTORY);}
    public abstract void renderFirstPerson(ItemStack stack);
    public void renderOther(ItemStack stack,ItemRenderType type,Object... data){}
    protected static double lerp(double from,double to,double alpha){return from+(to-from)*alpha;}
    protected static void standardAimingTransform(ItemStack stack,double sx,double sy,double sz,double ax,double ay,double az){float aim=ItemGunBaseNT.prevAimingProgress+(ItemGunBaseNT.aimingProgress-ItemGunBaseNT.prevAimingProgress)*interp;LegacyGunGL.glTranslated(lerp(sx,ax,aim),lerp(sy,ay,aim),lerp(sz,az,aim));}
    protected static void renderSmokeNodes(List<ItemGunBaseNT.SmokeNode> nodes,double scale){if(nodes.size()<2)return;var frame=LegacyGunGL.frame();var out=frame.buffers().getBuffer(LegacyGunSmokeType.TYPE);for(int i=0;i<nodes.size()-1;i++){var node=nodes.get(i);var past=nodes.get(i+1);for(int side:new int[]{1,-1}){vertex(out,frame.pose(),node.forward,node.lift,node.side,0,0,(float)node.alpha,frame.light());vertex(out,frame.pose(),node.forward,node.lift,node.side+side*node.width*scale,0,0,0,frame.light());vertex(out,frame.pose(),past.forward,past.lift,past.side+side*past.width*scale,0,0,0,frame.light());vertex(out,frame.pose(),past.forward,past.lift,past.side,0,0,(float)past.alpha,frame.light());}}}
    protected static void renderMuzzleFlash(long lastShot){renderMuzzleFlash(lastShot,75,15);}
    protected static void renderMuzzleFlash(long lastShot,int duration,double size){long age=System.currentTimeMillis()-lastShot;if(age>=duration)return;double time=age/(double)duration,width=6*time,length=size*time,inset=2;double[][] vertices={{0,-width,-inset,1,1},{0,width,-inset,0,1},{.1,width,length-inset,0,0},{.1,-width,length-inset,1,0},{0,width,inset,0,1},{0,-width,inset,1,1},{.1,-width,-length+inset,1,0},{.1,width,-length+inset,0,0},{0,-inset,width,0,1},{0,-inset,-width,1,1},{.1,length-inset,-width,1,0},{.1,length-inset,width,0,0},{0,inset,-width,1,1},{0,inset,width,0,1},{.1,-length+inset,width,0,0},{.1,-length+inset,-width,1,0}};flash(vertices);}
    protected static void renderGapFlash(long lastShot){long age=System.currentTimeMillis()-lastShot;if(age>=75)return;double time=age/75D,height=4*time,length=15*time,lift=3*time,offset=time,forward=.125;double[][] vertices={{0,-height,-offset,1,1},{0,height,-offset,0,1},{0,height+lift,length-offset,0,0},{0,-height+lift,length-offset,1,0},{0,height,offset,0,1},{0,-height,offset,1,1},{0,-height+lift,-length+offset,1,0},{0,height+lift,-length+offset,0,0},{0,-height,-offset,1,1},{0,height,-offset,0,1},{forward,height,length-offset,0,0},{forward,-height,length-offset,1,0},{0,height,offset,0,1},{0,-height,offset,1,1},{forward,-height,-length+offset,1,0},{forward,height,-length+offset,0,0}};flash(vertices,LegacyGunSmokeType.GAP);}
    private static void flash(double[][] vertices){flash(vertices,GunRenderTypes.FLASH);}
    private static void flash(double[][] vertices,RenderType type){var frame=LegacyGunGL.frame();var out=frame.buffers().getBuffer(type);for(var vertex:vertices)vertex(out,frame.pose(),vertex[0],vertex[1],vertex[2],(float)vertex[3],(float)vertex[4],1,240);}
    private static void vertex(VertexConsumer out,PoseStack pose,double x,double y,double z,float u,float v,float alpha,int light){out.addVertex(pose.last(),(float)x,(float)y,(float)z).setColor(1,1,1,alpha).setUv(u,v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(),0,1,0);}
}
