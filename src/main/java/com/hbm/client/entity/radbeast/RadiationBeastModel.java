package com.hbm.client.entity.radbeast;

import com.hbm.entity.mob.radbeast.EntityRADBeast;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

public final class RadiationBeastModel extends EntityModel<EntityRADBeast> {
    private final LegacyBlazeModel original=new LegacyBlazeModel();
    private EntityRADBeast entity;private float limb,amount,age,yaw,pitch;
    public RadiationBeastModel(){super(RenderType::entityCutoutNoCull);}
    @Override public void setupAnim(EntityRADBeast entity,float limb,float amount,float age,float yaw,float pitch){this.entity=entity;this.limb=limb;this.amount=amount;this.age=age;this.yaw=yaw;this.pitch=pitch;}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer output,int light,int overlay,int color){original.renderToBuffer(poses,output,light,overlay,color,entity,limb,amount,age,yaw,pitch,1F/16F);}
}
