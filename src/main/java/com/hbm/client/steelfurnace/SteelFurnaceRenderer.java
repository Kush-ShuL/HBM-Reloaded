package com.hbm.client.steelfurnace;
import com.hbm.client.model.*;
import com.hbm.machine.steelfurnace.SteelFurnaceEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source Forge OBJ yaw minus90, source four flickering vent sheets, unchanged UV and shape. */
public final class SteelFurnaceRenderer implements BlockEntityRenderer<SteelFurnaceEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/furnace_steel.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/furnace_steel.png");
    public SteelFurnaceRenderer(BlockEntityRendererProvider.Context context){}
    static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
    @Override public void render(SteelFurnaceEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        int direction=machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10;
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees((switch(direction){case 5->90;case 2->180;case 4->270;default->0;})-90));model(pose,buffers,light,overlay);
        if(machine.wasOn){float color=(float)Math.sin(System.currentTimeMillis()*.001);var out=buffers.getBuffer(SteelFurnaceRenderTypes.VENT);for(int i=0;i<4;i++){float x=1+i*.0625F;for(float[] vertex:new float[][]{{x,1,-1},{x,1,1},{x,.5F,1},{x,.5F,-1}})out.addVertex(pose.last(),vertex[0],vertex[1],vertex[2]).setColor(.875F+color*.125F,.625F+color*.375F,0,.5F).setLight(LightTexture.FULL_BRIGHT);}}
        pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(SteelFurnaceEntity machine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(SteelFurnaceEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
