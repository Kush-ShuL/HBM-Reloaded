package com.hbm.client.entity.glyphid;

import com.hbm.client.model.legacy.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Official1.7.10 ModelSilverfish, the model explicitly selected by source RenderMaggot; original boxes/UV/order/float animation. */
public final class LegacySilverfishModel extends ModelBase {
    private final ModelRenderer[] body=new ModelRenderer[7],wings=new ModelRenderer[3];
    private final float[] zPosition=new float[7];
    private static final int[][] SIZES={{3,2,2},{4,3,2},{6,4,3},{3,3,3},{2,2,3},{2,1,2},{1,1,2}};
    private static final int[][] UV={{0,0},{0,4},{0,9},{0,16},{0,22},{11,0},{13,4}};
    public LegacySilverfishModel(){
        float z=-3.5F;
        for(int i=0;i<body.length;i++){body[i]=new ModelRenderer(this,UV[i][0],UV[i][1]);body[i].addBox(SIZES[i][0]*-.5F,0,SIZES[i][2]*-.5F,SIZES[i][0],SIZES[i][1],SIZES[i][2]);body[i].setRotationPoint(0,24-SIZES[i][1],z);zPosition[i]=z;if(i<body.length-1)z+=(SIZES[i][2]+SIZES[i+1][2])*.5F;}
        wings[0]=new ModelRenderer(this,20,0);wings[0].addBox(-5,0,SIZES[2][2]*-.5F,10,8,SIZES[2][2]);wings[0].setRotationPoint(0,16,zPosition[2]);
        wings[1]=new ModelRenderer(this,20,11);wings[1].addBox(-3,0,SIZES[4][2]*-.5F,6,4,SIZES[4][2]);wings[1].setRotationPoint(0,20,zPosition[4]);
        wings[2]=new ModelRenderer(this,20,18);wings[2].addBox(-3,0,SIZES[4][2]*-.5F,6,5,SIZES[1][2]);wings[2].setRotationPoint(0,19,zPosition[1]);
    }
    @Override public void render(Entity entity,float swing,float amount,float age,float yaw,float pitch,float scale){setRotationAngles(swing,amount,age,yaw,pitch,scale,entity);for(var part:body)part.render(scale);for(var part:wings)part.render(scale);}
    @Override public void setRotationAngles(float swing,float amount,float age,float yaw,float pitch,float scale,Entity entity){
        for(int i=0;i<body.length;i++){body[i].rotateAngleY=Mth.cos(age*.9F+i*.15F*(float)Math.PI)*(float)Math.PI*.05F*(1+Math.abs(i-2));body[i].rotationPointX=Mth.sin(age*.9F+i*.15F*(float)Math.PI)*(float)Math.PI*.2F*Math.abs(i-2);}
        wings[0].rotateAngleY=body[2].rotateAngleY;wings[1].rotateAngleY=body[4].rotateAngleY;wings[1].rotationPointX=body[4].rotationPointX;wings[2].rotateAngleY=body[1].rotateAngleY;wings[2].rotationPointX=body[1].rotationPointX;
    }
}
