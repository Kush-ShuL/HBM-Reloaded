package com.hbm.machine.reactor.zirnox.client;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.zirnox.ZirnoxDebris;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
/** Original Forge OBJ convention, individual salvage meshes and tumble axis. */
public final class ZirnoxDebrisRenderer extends EntityRenderer<ZirnoxDebris> {
    public ZirnoxDebrisRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    private ResourceLocation model(ZirnoxDebris debris){return ZirnoxRenderer.id("models/"+(debris.debrisType()==ZirnoxDebris.Type.GRAPHITE?"projectiles/deb_graphite":"zirnox/deb_"+debris.debrisType().name().toLowerCase(java.util.Locale.ROOT))+".obj");}
    @Override public ResourceLocation getTextureLocation(ZirnoxDebris debris){return ZirnoxRenderer.id(switch(debris.debrisType()){case CONCRETE->"textures/models/zirnox_destroyed.png";case ELEMENT->"textures/models/machines/zirnox_deb_element.png";case GRAPHITE->"textures/block/block_graphite.png";default->"textures/models/zirnox.png";});}
    @Override public void render(ZirnoxDebris debris,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){poses.pushPose();poses.translate(0,.125,0);poses.mulPose(Axis.YP.rotationDegrees(debris.getId()%360));float unit=(float)(1/Math.sqrt(3));poses.mulPose(new Quaternionf().rotationAxis((float)Math.toRadians(Mth.lerp(partial,debris.lastRotation,debris.rotation)),unit,unit,unit));var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model(debris));LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(debris))),light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.FORGE);poses.popPose();super.render(debris,yaw,partial,poses,buffers,light);}
}
