package com.hbm.client.entity.undead;

import com.hbm.client.model.legacy.ModelRenderer;
import com.hbm.entity.mob.undead.UndeadSoldier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

/** Draws real legacy boxes; modern parts only expose those exact poses to armor/held-item layers. */
final class UndeadSoldierModel extends HumanoidModel<UndeadSoldier> {
    private final UndeadSoldierBiped zombie=new UndeadSoldierBiped(false),skeleton=new UndeadSoldierBiped(true);
    private UndeadSoldierBiped active=zombie;
    UndeadSoldierModel(){super(LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE,0),64,64).bakeRoot());}
    @Override public void setupAnim(UndeadSoldier mob,float swing,float amount,float age,float yaw,float pitch){
        active=mob.bodyType()==UndeadSoldier.TYPE_SKELETON?skeleton:zombie;
        active.isChild=young;active.isRiding=riding;active.isSneak=mob.isCrouching();active.onGround=attackTime;
        active.heldItemRight=mob.getMainHandItem().isEmpty()?0:1;active.heldItemLeft=0;active.aimedBow=false;
        active.setRotationAngles(swing,amount,age,yaw,pitch,1F/16F,mob);
        copy(active.head,head);copy(active.headwear,hat);copy(active.body,body);
        copy(active.rightArm,rightArm);copy(active.leftArm,leftArm);copy(active.rightLeg,rightLeg);copy(active.leftLeg,leftLeg);
        crouching=active.isSneak;
    }
    private static void copy(ModelRenderer original,ModelPart part){
        part.setPos(original.rotationPointX,original.rotationPointY,original.rotationPointZ);
        part.setRotation(original.rotateAngleX,original.rotateAngleY,original.rotateAngleZ);
    }
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer vertices,int light,int overlay,int color){
        // setupAnim already evaluated the source model once. Calling its render
        // again would run non-idempotent arm pivot math twice in one frame.
        try(var ignored=com.hbm.client.model.legacy.LegacyModelRenderContext.push(poses,vertices,light,overlay,color)){
            active.head.render(1F/16F);active.body.render(1F/16F);
            active.rightArm.render(1F/16F);active.leftArm.render(1F/16F);
            active.rightLeg.render(1F/16F);active.leftLeg.render(1F/16F);active.headwear.render(1F/16F);
        }
    }
}
