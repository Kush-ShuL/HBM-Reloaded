package com.hbm.client.tesla;
import com.hbm.client.model.*;
import com.hbm.machine.tesla.TeslaBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
/** Original RenderTesla model matrix and per-world-tick RANDOM/SOLID beams. */
public final class TeslaRenderer implements BlockEntityRenderer<TeslaBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/tesla.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/tesla.png");
    public TeslaRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);}
    @Override public void render(TeslaBlockEntity coil,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        LegacyContactShadows.render(coil,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));inventory(poses,buffers,light,overlay);poses.translate(0,TeslaBlockEntity.OFFSET,0);
        Vec3 origin=coil.origin();for(Vec3 target:coil.targets()){Vec3 difference=target.subtract(origin);TeslaArcRenderer.beam(new Vec3(-difference.x,difference.y,-difference.z),poses,buffers,(int)coil.getLevel().getGameTime()%1000+1,(int)(difference.length()*5));}poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(TeslaBlockEntity coil){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(TeslaBlockEntity coil){Vec3 origin=coil.origin();return new AABB(origin.x-10,coil.getBlockPos().getY()-10,origin.z-10,origin.x+10,origin.y+10,origin.z+10);}
}
