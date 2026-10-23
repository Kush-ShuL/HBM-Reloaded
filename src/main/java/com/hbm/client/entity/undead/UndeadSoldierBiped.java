package com.hbm.client.entity.undead;

import com.hbm.client.armor.LegacyBipedModel;
import com.hbm.client.model.legacy.ModelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Official 1.7.10 ModelZombie pose, with source ModelSkeletonNT box overrides. */
final class UndeadSoldierBiped extends LegacyBipedModel {
    UndeadSoldierBiped(boolean skeleton){
        super(0,0,64,skeleton?32:64);
        if(skeleton){
            rightArm=new ModelRenderer(this,40,16);rightArm.addBox(-1,-2,-1,2,12,2,0);rightArm.setRotationPoint(-5,2,0);
            leftArm=new ModelRenderer(this,40,16);leftArm.mirror=true;leftArm.addBox(-1,-2,-1,2,12,2,0);leftArm.setRotationPoint(5,2,0);
            rightLeg=new ModelRenderer(this,0,16);rightLeg.addBox(-1,0,-1,2,12,2,0);rightLeg.setRotationPoint(-2,12,0);
            leftLeg=new ModelRenderer(this,0,16);leftLeg.mirror=true;leftLeg.addBox(-1,0,-1,2,12,2,0);leftLeg.setRotationPoint(2,12,0);
        }
    }
    @Override public void setRotationAngles(float swing,float amount,float age,float yaw,float pitch,float scale,Entity entity){
        super.setRotationAngles(swing,amount,age,yaw,pitch,scale,entity);
        float first=Mth.sin(onGround*(float)Math.PI);
        float second=Mth.sin((1-(1-onGround)*(1-onGround))*(float)Math.PI);
        rightArm.rotateAngleZ=leftArm.rotateAngleZ=0;
        rightArm.rotateAngleY=-(.1F-first*.6F);leftArm.rotateAngleY=.1F-first*.6F;
        rightArm.rotateAngleX=leftArm.rotateAngleX=-((float)Math.PI/2F);
        rightArm.rotateAngleX-=first*1.2F-second*.4F;leftArm.rotateAngleX-=first*1.2F-second*.4F;
        rightArm.rotateAngleZ+=Mth.cos(age*.09F)*.05F+.05F;leftArm.rotateAngleZ-=Mth.cos(age*.09F)*.05F+.05F;
        rightArm.rotateAngleX+=Mth.sin(age*.067F)*.05F;leftArm.rotateAngleX-=Mth.sin(age*.067F)*.05F;
    }
}
