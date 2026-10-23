package com.hbm.client.ironfurnace;
import com.hbm.client.model.*;
import com.hbm.machine.ironfurnace.IronFurnaceEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original Forge OBJ, Main and mutually exclusive On/Off groups, yaw and model-origin translation. */
public final class IronFurnaceRenderer implements BlockEntityRenderer<IronFurnaceEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/furnace_iron.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/furnace_iron.png");
    public IronFurnaceRenderer(BlockEntityRendererProvider.Context context){}
    static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay,boolean on){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var out=buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh,pose,out,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"Main");
        LegacyObjRenderer.renderOnly(mesh,pose,on?buffers.getBuffer(IronFurnaceRenderTypes.ON):out,on?LightTexture.FULL_BRIGHT:light,overlay,-1,LegacyObjRenderer.Convention.FORGE,on?"On":"Off");
    }
    @Override public void render(IronFurnaceEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        int direction=machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10;
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(switch(direction){case 5->90;case 2->180;case 4->270;default->0;}));pose.translate(-.5,0,-.5);model(pose,buffers,light,overlay,machine.wasOn);pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(IronFurnaceEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(IronFurnaceEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
