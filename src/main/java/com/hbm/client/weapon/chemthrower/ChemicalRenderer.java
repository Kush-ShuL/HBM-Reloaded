package com.hbm.client.weapon.chemthrower;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.hbm.client.weapon.forward.GunForwardEffects;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
/** RenderChemical's actual AMAT/LIGHTNING prism. GAS/GASFLAME keep exactly one source billboard in the shared ordered cloud queue. */
public final class ChemicalRenderer extends EntityRenderer<EntityChemical> {
 public ChemicalRenderer(EntityRendererProvider.Context context){super(context);}@Override public ResourceLocation getTextureLocation(EntityChemical entity){return ResourceLocation.parse("hbm:textures/particle/particle_base.png");}
 @Override public void render(EntityChemical chemical,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){var style=chemical.getStyle();if(style!=ChemicalStyle.AMAT&&style!=ChemicalStyle.LIGHTNING)return;poses.pushPose();float ry=chemical.yRotO+(chemical.getYRot()-chemical.yRotO)*partial,rp=chemical.xRotO+(chemical.getXRot()-chemical.xRotO)*partial;poses.mulPose(Axis.YP.rotationDegrees(ry));poses.mulPose(Axis.XP.rotationDegrees(-rp-90));double length=(double)(float)chemical.getDeltaMovement().length()*(chemical.tickCount+partial)*.75,size=.0625;var material=style==ChemicalStyle.AMAT?ChemicalRenderTypes.ANTIMATTER_BEAM:ChemicalRenderTypes.BEAM;
  if(style!=ChemicalStyle.AMAT||!GunForwardEffects.captureWorldGeometry(material,immediate->prism(poses,immediate,material,size,length)))prism(poses,buffers,material,size,length);poses.popPose();}
 private static void prism(PoseStack poses,MultiBufferSource buffers,RenderType material,double size,double length){var out=buffers.getBuffer(material);quad(out,poses,-size,-size,size,-size,length);quad(out,poses,-size,size,size,size,length);quad(out,poses,-size,-size,-size,size,length);quad(out,poses,size,-size,size,size,length);}
 private static void quad(VertexConsumer out,PoseStack poses,double x1,double z1,double x2,double z2,double length){out.addVertex(poses.last(),(float)x1,0,(float)z1).setColor(1F,1F,1F,.2F);out.addVertex(poses.last(),(float)x2,0,(float)z2).setColor(1F,1F,1F,.2F);out.addVertex(poses.last(),(float)x2,(float)length,(float)z2).setColor(1F,1F,1F,0);out.addVertex(poses.last(),(float)x1,(float)length,(float)z1).setColor(1F,1F,1F,0);}
}
