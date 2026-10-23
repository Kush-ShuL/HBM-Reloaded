package com.hbm.client.entity.pigeon;

import com.hbm.entity.mob.pigeon.EntityPigeon;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

/** Original ModelPigeon boxes/UV/children evaluated once with the real source flap age. */
public final class PigeonModel extends EntityModel<EntityPigeon> {
    private final ModelPigeon original=new ModelPigeon();
    private EntityPigeon entity;private float limb,amount,age,yaw,pitch;
    public PigeonModel(){super(RenderType::entityCutoutNoCull);}
    @Override public void setupAnim(EntityPigeon entity,float limb,float amount,float age,float yaw,float pitch){this.entity=entity;this.limb=limb;this.amount=amount;this.age=age;this.yaw=yaw;this.pitch=pitch;}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer out,int light,int overlay,int color){original.renderToBuffer(poses,out,light,overlay,color,entity,limb,amount,age,yaw,pitch,1F/16F);}
}
