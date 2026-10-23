package com.hbm.client.weapon.b92;
import com.hbm.weapon.b92.*;import com.mojang.blaze3d.vertex.*;import com.mojang.math.Axis;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.entity.*;import net.minecraft.resources.ResourceLocation;
/** Exact RenderBeam5 four-sided nested additive blue beams, radius.175 and length2, raw current yaw/pitch. */
public final class B92BeamRenderer extends EntityRenderer<EntityB92Beam> {
 public B92BeamRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
 @Override public void render(EntityB92Beam beam,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(beam.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(-beam.getXRot()));var vertices=buffers.getBuffer(B92RenderTypes.BEAM);float radius=.175F;for(float o=0;o<=radius;o+=radius/8){float color=Math.max(1-o*8.333F,0);quad(vertices,poses,color,o,-o,o,o,0,2);point(vertices,poses,-o,-o,0,color);point(vertices,poses,o,-o,0,color);point(vertices,poses,o,-o,2,color);point(vertices,poses,-o,-o,2,color);quad(vertices,poses,color,-o,o,-o,-o,0,2);point(vertices,poses,o,o,0,color);point(vertices,poses,-o,o,0,color);point(vertices,poses,-o,o,2,color);point(vertices,poses,o,o,2,color);}poses.popPose();}
 private static void quad(VertexConsumer vertices,PoseStack poses,float color,float x1,float y1,float x2,float y2,float z1,float z2){point(vertices,poses,x1,y1,z1,color);point(vertices,poses,x2,y2,z1,color);point(vertices,poses,x2,y2,z2,color);point(vertices,poses,x1,y1,z2,color);}
 private static void point(VertexConsumer vertices,PoseStack poses,float x,float y,float z,float color){vertices.addVertex(poses.last().pose(),x,y,z).setColor(color,color,1,1);}
 @Override public ResourceLocation getTextureLocation(EntityB92Beam beam){return B92Content.id("textures/models/plasmabeam.png");}
}
