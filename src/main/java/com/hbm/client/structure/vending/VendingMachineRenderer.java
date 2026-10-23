package com.hbm.client.structure.vending;
import com.hbm.client.model.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.vending.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source HFRWavefrontObject.noSmooth/asVBO, original Soda/Obamna groups and TESR caller. */
public final class VendingMachineRenderer implements BlockEntityRenderer<VendingMachineBlockEntity> {
    private static final ResourceLocation MODEL=VendingMachineContent.id("models/machines/vending_machine.obj"),TEXTURE=VendingMachineContent.id("textures/models/machines/vending_machine.png");
    public VendingMachineRenderer(BlockEntityRendererProvider.Context context){}
    public static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay,int variant,boolean world){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var vertices=buffers.getBuffer(world?RenderType.entityCutoutNoCull(TEXTURE):RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,variant==0?"Soda":"Obamna");
    }
    @Override public void render(VendingMachineBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var level=machine.getLevel();if(level==null||!(machine.getBlockState().getBlock()instanceof VendingMachineBlock block)||!level.getBlockState(machine.getBlockPos().above()).is(block))return;
        poses.pushPose();poses.translate(.5,0,.5);
        poses.mulPose(Axis.YP.rotationDegrees(switch(machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->90;case 4->180;case 3->270;default->0;}));
        draw(poses,buffers,light,overlay,block.variant(level,machine.getBlockPos()),true);poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(VendingMachineBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+2,p.getZ()+1);}
    @Override public boolean shouldRenderOffScreen(VendingMachineBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
}
