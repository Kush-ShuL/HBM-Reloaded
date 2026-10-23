package com.hbm.client.entity.radbeast;

import com.hbm.client.model.legacy.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Official1.7.10 ModelBlaze, selected explicitly by RenderRADBeast. Original 0,0,0 rod boxes and float rings. */
public final class LegacyBlazeModel extends ModelBase {
    private final ModelRenderer[] rods=new ModelRenderer[12];
    private final ModelRenderer head;
    public LegacyBlazeModel(){for(int i=0;i<rods.length;i++){rods[i]=new ModelRenderer(this,0,16);rods[i].addBox(0,0,0,2,8,2);}head=new ModelRenderer(this,0,0);head.addBox(-4,-4,-4,8,8,8);}
    @Override public void setRotationAngles(float limb,float amount,float age,float yaw,float pitch,float scale,Entity entity){
        float rotation=age*(float)Math.PI*-.1F;
        for(int i=0;i<4;i++){rods[i].rotationPointY=-2F+Mth.cos((i*2+age)*.25F);rods[i].rotationPointX=Mth.cos(rotation)*9F;rods[i].rotationPointZ=Mth.sin(rotation)*9F;rotation+=(float)Math.PI*.5F;}
        rotation=(float)Math.PI*.25F+age*(float)Math.PI*.03F;
        for(int i=4;i<8;i++){rods[i].rotationPointY=2F+Mth.cos((i*2+age)*.25F);rods[i].rotationPointX=Mth.cos(rotation)*7F;rods[i].rotationPointZ=Mth.sin(rotation)*7F;rotation+=(float)Math.PI*.5F;}
        rotation=.47123894F+age*(float)Math.PI*-.05F;
        for(int i=8;i<12;i++){rods[i].rotationPointY=11F+Mth.cos((i*1.5F+age)*.5F);rods[i].rotationPointX=Mth.cos(rotation)*5F;rods[i].rotationPointZ=Mth.sin(rotation)*5F;rotation+=(float)Math.PI*.5F;}
        head.rotateAngleY=yaw/57.295776F;head.rotateAngleX=pitch/57.295776F;
    }
    @Override public void render(Entity entity,float limb,float amount,float age,float yaw,float pitch,float scale){setRotationAngles(limb,amount,age,yaw,pitch,scale,entity);head.render(scale);for(var rod:rods)rod.render(scale);}
}
