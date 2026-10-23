package com.hbm.client.oldlantern;
import com.hbm.client.model.*;
import com.hbm.world.oldlantern.OldLanternEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Exact RenderLanternBehemoth center/tilt/part matrices and wall-clock pulse; source model has no yaw. */
public final class OldLanternRenderer implements BlockEntityRenderer<OldLanternEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/trinkets/lantern.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/trinkets/lantern_rusty.png");
    public OldLanternRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(OldLanternEntity lantern,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        var transform=new Matrix4f().translation(.5F,0,.5F);if(lantern.isBroken)transform.rotateX((float)Math.toRadians(5)).rotateZ((float)Math.toRadians(10));
        LegacyContactShadows.render(lantern,model,transform,poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);if(lantern.isBroken){poses.mulPose(Axis.XP.rotationDegrees(5));poses.mulPose(Axis.ZP.rotationDegrees(10));}
        LegacyObjRenderer.renderOnly(model,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,"Lantern");
        double wave=Math.sin(System.currentTimeMillis()/200D)/2+.5;int intensity=(int)(255*(lantern.isBroken?wave:wave*.5+.5));
        int argb=0xff000000|(lantern.isBroken?intensity<<16:intensity<<8);
        LegacyObjRenderer.renderOnly(model,poses,OldLanternRenderTypes.colorOnly(buffers.getBuffer(OldLanternRenderTypes.LIGHT)),LightTexture.FULL_BRIGHT,overlay,argb,LegacyObjRenderer.Convention.HFR_FLAT,"Light");
        poses.popPose();
    }
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(OldLanternEntity lantern){var pos=lantern.getBlockPos();return new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+6,pos.getZ()+1);}
}
