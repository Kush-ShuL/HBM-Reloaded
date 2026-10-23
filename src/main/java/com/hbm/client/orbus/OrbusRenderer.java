package com.hbm.client.orbus;
import com.hbm.client.entity.ufo.UfoBeamRenderer;
import com.hbm.client.model.*;
import com.hbm.machine.orbus.OrbusEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
/** Original RenderOrbus, including its untranslated yaw, proportional UV-sphere and exact three beam calls. */
public final class OrbusRenderer implements BlockEntityRenderer<OrbusEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/orbus.obj"),SPHERE=ResourceLocation.parse("hbm:models/sphere_uv.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/orbus.png");
 public OrbusRenderer(BlockEntityRendererProvider.Context context){}private static WavefrontMesh mesh(ResourceLocation path){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),path);}
 static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(MODEL),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 private static int x(Direction d){return d==Direction.NORTH||d==Direction.WEST?1:0;}private static int z(Direction d){return d==Direction.NORTH||d==Direction.EAST?1:0;}
 @Override public void render(OrbusEntity orbus,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var dir=DummyableBlock.facing(orbus.getBlockState());var transform=new org.joml.Matrix4f().translation(x(dir),0,z(dir));LegacyContactShadows.render(orbus,mesh(MODEL),transform,pose,buffers);pose.pushPose();pose.mulPose(transform);double scale=(double)orbus.tank.getFill()/orbus.tank.getMaxFill();long time=orbus.getLevel().getGameTime();if(orbus.tank.getFill()>0){pose.pushPose();pose.translate(0,2.5+Math.sin(((time+partial)*.1)%(Math.PI*2))*.125*scale,0);pose.scale((float)scale,(float)scale,(float)scale);LegacyObjRenderer.renderAll(mesh(SPHERE),pose,buffers.getBuffer(OrbusRenderTypes.SPHERE),LightTexture.FULL_BRIGHT,overlay,0xff000000|orbus.tank.getTankType().getColor(),LegacyObjRenderer.Convention.FORGE);pose.popPose();}model(pose,buffers,light,overlay);if(orbus.tank.getFill()>0){pose.translate(0,1,0);var skeleton=new Vec3(0,3,0);UfoBeamRenderer.beam(skeleton,pose,buffers,false,0x101020,0,1,0,6,(float)scale*.5F);UfoBeamRenderer.beam(skeleton,pose,buffers,true,0x202060,(int)(time/2)%1000,6,(float)scale,2,.0625F*(float)scale);UfoBeamRenderer.beam(skeleton,pose,buffers,true,0x202060,(int)(time/4)%1000,6,(float)scale,2,.0625F*(float)scale);}pose.popPose();}
 @Override public boolean shouldRenderOffScreen(OrbusEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(OrbusEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+5,p.getZ()+3);}
}
