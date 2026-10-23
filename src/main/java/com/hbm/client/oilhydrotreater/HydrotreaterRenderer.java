package com.hbm.client.oilhydrotreater;
import com.hbm.client.model.*;
import com.hbm.machine.oilhydrotreater.HydrotreaterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Actual smooth, culled HFR model; the source does not rotate it with core metadata. */
public final class HydrotreaterRenderer implements BlockEntityRenderer<HydrotreaterBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/hydrotreater.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/hydrotreater.png");
 public HydrotreaterRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(HydrotreaterBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);inventory(poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(HydrotreaterBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(HydrotreaterBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+7,p.getZ()+2);}
}
