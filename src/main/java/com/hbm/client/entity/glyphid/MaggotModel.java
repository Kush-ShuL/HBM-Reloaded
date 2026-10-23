package com.hbm.client.entity.glyphid;

import com.hbm.entity.mob.glyphid.ParasiteMaggot;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

public final class MaggotModel extends EntityModel<ParasiteMaggot> {
    private final LegacySilverfishModel original=new LegacySilverfishModel();private ParasiteMaggot entity;private float swing,amount,age,yaw,pitch;
    public MaggotModel(){super(RenderType::entityCutout);}
    @Override public void setupAnim(ParasiteMaggot entity,float swing,float amount,float age,float yaw,float pitch){this.entity=entity;this.swing=swing;this.amount=amount;this.age=age;this.yaw=yaw;this.pitch=pitch;}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer output,int light,int overlay,int color){poses.pushPose();poses.translate(0,-.0068125,0);original.renderToBuffer(poses,output,light,overlay,color,entity,swing,amount,age,yaw,pitch,.0625F);poses.popPose();}
}
