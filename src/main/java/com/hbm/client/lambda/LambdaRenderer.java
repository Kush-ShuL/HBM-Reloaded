package com.hbm.client.lambda;

import com.hbm.client.model.LegacyContactShadows;
import com.hbm.machine.lambda.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/** Every source clamp/rotor/erector transform and all three original clipping planes. */
public final class LambdaRenderer implements BlockEntityRenderer<LambdaEntity> {
    public LambdaRenderer(BlockEntityRendererProvider.Context context){}
    private static float rotation(LambdaEntity machine){return switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
    @Override public void render(LambdaEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        float rotation=rotation(machine);LegacyContactShadows.render(machine,LambdaModels.pad(),new Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(rotation)),poses,buffers);
        poses.pushPose();try{
            poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(rotation));
            double doors=machine.getInterpPos(LambdaEntity.DOORS,partial),erector=machine.getInterpPos(LambdaEntity.ERECTOR,partial)-25,rotor=machine.getInterpPos(LambdaEntity.ROTOR,partial),clamps=machine.getInterpPos(LambdaEntity.CLAMPS,partial),pistons=machine.getInterpPos(LambdaEntity.PISTONS,partial);
            if(machine.erected){poses.pushPose();poses.translate(0,2,0);poses.mulPose(Axis.YP.rotationDegrees(-rotation));LambdaModels.rocket(poses,buffers,light,overlay);poses.popPose();}
            LambdaModels.part("Silo",poses,buffers,light,overlay);
            LambdaModels.clipped(LambdaModels.pad(),"DoorLeft",LambdaModels.PAD,new Matrix4f().translation(0,0,(float)doors),new Vector4f(0,0,-1,6),poses,buffers,light,overlay);
            LambdaModels.clipped(LambdaModels.pad(),"DoorRight",LambdaModels.PAD,new Matrix4f().translation(0,0,(float)-doors),new Vector4f(0,0,1,6),poses,buffers,light,overlay);
            var clip=new Vector4f(0,1,0,-.25F);var moving=new Matrix4f().translation(0,(float)erector,0);
            part("Erector",moving,clip,poses,buffers,light,overlay);moving.translate(0,13.25F,0).rotateX((float)Math.toRadians(rotor)).translate(0,-13.25F,0);part("Rotor",moving,clip,poses,buffers,light,overlay);
            for(int side=0;side<2;side++)for(int upper=0;upper<2;upper++){
                float x=side==0?3.5F:-3.5F,y=upper==1?19.75F:6.75F,angle=(float)clamps*(side==0?(upper==1?-1:1):(upper==1?1:-1));String suffix=(upper==1?"Upper":"Lower")+(side+1);
                var limb=new Matrix4f(moving).translate(x,y,0).rotateZ((float)Math.toRadians(angle)).translate(-x,-y,0);part("Pivot"+suffix,limb,clip,poses,buffers,light,overlay);limb.translate((float)pistons*(side==0?1:-1),0,0);part("Clamp"+suffix,limb,clip,poses,buffers,light,overlay);
            }
            if((machine.erecting||machine.erected)&&!machine.erected){moving.translate(0,2,0).rotateY((float)Math.toRadians(-rotation));LambdaModels.clipped(LambdaModels.rocket(),null,LambdaModels.ROCKET,moving,clip,poses,buffers,light,overlay);}
        }finally{poses.popPose();}
    }
    private static void part(String name,Matrix4f transform,Vector4f plane,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LambdaModels.clipped(LambdaModels.pad(),name,LambdaModels.PAD,transform,plane,poses,buffers,light,overlay);}
    @Override public boolean shouldRenderOffScreen(LambdaEntity machine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(LambdaEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-7,p.getY(),p.getZ()-7,p.getX()+8,p.getY()+30,p.getZ()+8);}
}
