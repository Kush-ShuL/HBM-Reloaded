package com.hbm.client.vacuumdistill;
import com.hbm.client.model.*;
import com.hbm.machine.vacuumdistill.VacuumDistillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original authored HFR normals and UVs, only source center translation, no refinery rotation/tilt. */
public final class VacuumDistillRenderer implements BlockEntityRenderer<VacuumDistillBlockEntity> {
    static final ResourceLocation MODEL=id("models/machines/vacuum_distill.obj"),TEXTURE=id("textures/models/machines/vacuum_distill.png");
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public VacuumDistillRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(VacuumDistillBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(VacuumDistillBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(VacuumDistillBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+9,p.getZ()+2);}
}
