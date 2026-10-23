package com.hbm.client.coolingtower;
import com.hbm.client.model.*;import com.hbm.machine.coolingtower.*;import com.mojang.blaze3d.vertex.PoseStack;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.blockentity.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.phys.AABB;
/** Exact original whole HFR meshes at(.5,0,.5), no invented facing rotation or opacity proxy shadow. */
public final class CoolingTowerRenderer implements BlockEntityRenderer<CoolingTowerBlockEntity> {
 public CoolingTowerRenderer(BlockEntityRendererProvider.Context context){}
 static String name(CoolingTowerKind kind){return kind==CoolingTowerKind.SMALL?"tower_small":"tower_large";}
 static WavefrontMesh mesh(CoolingTowerKind kind){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/"+name(kind)+".obj"));}
 static void model(CoolingTowerKind kind,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(kind),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.parse("hbm:textures/models/machines/"+name(kind)+".png"))),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(CoolingTowerBlockEntity tower,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(tower,mesh(tower.kind),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);model(tower.kind,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(CoolingTowerBlockEntity tower){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(CoolingTowerBlockEntity tower){var pos=tower.getBlockPos();int r=tower.kind.radius(),h=tower.kind==CoolingTowerKind.SMALL?20:13;return new AABB(pos.getX()-r,pos.getY(),pos.getZ()-r,pos.getX()+r+1,pos.getY()+h,pos.getZ()+r+1);}
}
