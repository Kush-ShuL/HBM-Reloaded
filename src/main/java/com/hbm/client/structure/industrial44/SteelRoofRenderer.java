package com.hbm.client.structure.industrial44;
import com.hbm.world.structure.dependencies.industrial44.SteelRoofEntity;
import com.hbm.client.model.legacy.generated.ModelSteelRoof;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source RenderDecoBlock roof branch deliberately has no facing rotation, despite saved DecoBlock metadata. */
public final class SteelRoofRenderer implements BlockEntityRenderer<SteelRoofEntity> {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/steel_roof.png");private final ModelSteelRoof model=new ModelSteelRoof();
 public SteelRoofRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(SteelRoofEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,1.5,.5);poses.mulPose(Axis.ZP.rotationDegrees(180));model.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,.0625F);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(SteelRoofEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SteelRoofEntity tile){return AABB.INFINITE;}
}
