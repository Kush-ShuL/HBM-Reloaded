package com.hbm.client.entity.boat;
import com.hbm.client.model.legacy.*;
import net.minecraft.world.entity.Entity;
/** Official1.7 ModelBoat selected by RenderBoatRubber: same five boxes,UV,pivots and static rotations. */
public final class LegacyBoatModel extends ModelBase {
 private final ModelRenderer[] sides=new ModelRenderer[5];
 public LegacyBoatModel(){
  sides[0]=new ModelRenderer(this,0,8);sides[0].addBox(-12,-8,-3,24,16,4,0);sides[0].setRotationPoint(0,4,0);
  for(int i=1;i<5;i++){sides[i]=new ModelRenderer(this,0,0);sides[i].addBox(-10,-7,-1,20,6,2,0);}
  sides[1].setRotationPoint(-11,4,0);sides[2].setRotationPoint(11,4,0);sides[3].setRotationPoint(0,4,-9);sides[4].setRotationPoint(0,4,9);
  sides[0].rotateAngleX=(float)Math.PI/2F;sides[1].rotateAngleY=(float)Math.PI*1.5F;sides[2].rotateAngleY=(float)Math.PI/2F;sides[3].rotateAngleY=(float)Math.PI;
 }
 @Override public void render(Entity entity,float swing,float amount,float age,float yaw,float pitch,float scale){for(var part:sides)part.render(scale);}
}
