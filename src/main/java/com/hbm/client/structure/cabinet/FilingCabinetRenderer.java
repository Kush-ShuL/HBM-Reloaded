package com.hbm.client.structure.cabinet;
import com.hbm.client.model.*;
import com.hbm.world.structure.cabinet.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original RenderFileCabinet parts, rotation-bit switch, texture selection and interpolated drawer offsets. */
public final class FilingCabinetRenderer implements BlockEntityRenderer<FilingCabinetBlockEntity>{
 public static final ResourceLocation MODEL=FilingCabinetContent.id("models/file_cabinet.obj"),GREEN=FilingCabinetContent.id("textures/models/file_cabinet.png"),STEEL=FilingCabinetContent.id("textures/models/file_cabinet_steel.png");
 public FilingCabinetRenderer(BlockEntityRendererProvider.Context context){}
 public static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 public static ResourceLocation texture(int meta,boolean item){return (item?meta:meta&3)==1?STEEL:GREEN;}
 public static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay,int metadata,float lower,float upper,boolean item){var mesh=mesh();var out=buffers.getBuffer(RenderType.entityCutout(texture(metadata,item)));if(item){LegacyObjRenderer.renderAll(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.FORGE);return;}LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"Cabinet");poses.pushPose();poses.translate(0,0,.6875F*lower);LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"LowerDrawer");poses.popPose();poses.pushPose();poses.translate(0,0,.6875F*upper);LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"UpperDrawer");poses.popPose();}
 @Override public void render(FilingCabinetBlockEntity cabinet,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(cabinet.getBlockState().getValue(FilingCabinetBlock.META)>>2){case 1->0;case 2->270;case 3->90;default->180;}));model(poses,buffers,light,overlay,cabinet.getBlockState().getValue(FilingCabinetBlock.META),Mth.lerp(partial,cabinet.prevLowerExtent,cabinet.lowerExtent),Mth.lerp(partial,cabinet.prevUpperExtent,cabinet.upperExtent),false);poses.popPose();}
 @Override public AABB getRenderBoundingBox(FilingCabinetBlockEntity cabinet){return new AABB(cabinet.getBlockPos()).inflate(1,.05,1);}@Override public boolean shouldRenderOffScreen(FilingCabinetBlockEntity cabinet){return true;}@Override public int getViewDistance(){return 256;}
}
