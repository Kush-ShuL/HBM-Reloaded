package com.hbm.client.combinationfurnace;
import com.hbm.client.model.*;
import com.hbm.machine.combinationfurnace.CombinationFurnaceEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact source Forge model with no world yaw, plus its 14frame/%5 UV quirk and yaw-only fire billboard. */
public final class CombinationFurnaceRenderer implements BlockEntityRenderer<CombinationFurnaceEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/combination_oven.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/combination_oven.png");
    public CombinationFurnaceRenderer(BlockEntityRendererProvider.Context context){}
    static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
    @Override public void render(CombinationFurnaceEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();pose.translate(.5,0,.5);model(pose,buffers,light,overlay);
        if(machine.wasOn&&machine.getLevel()!=null){int frame=(int)(machine.getLevel().getGameTime()/2%14);float u0=(frame%5)*(1F/14F),u1=u0+1F/14F;pose.pushPose();pose.translate(0,1.75,0);pose.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));var out=buffers.getBuffer(CombinationFurnaceRenderTypes.FIRE);out.addVertex(pose.last(),-1,0,0).setUv(u1,1).setColor(-1);out.addVertex(pose.last(),-1,3,0).setUv(u1,0).setColor(-1);out.addVertex(pose.last(),1,3,0).setUv(u0,0).setColor(-1);out.addVertex(pose.last(),1,0,0).setUv(u0,1).setColor(-1);pose.popPose();}
        pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(CombinationFurnaceEntity machine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(CombinationFurnaceEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+2.125,p.getZ()+2);}
}
