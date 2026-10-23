package com.hbm.client.hephaestus;
import com.hbm.client.model.*;
import com.hbm.machine.hephaestus.HephaestusEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderHephaestus: no world yaw, smooth body/rotor, flat animated core. */
public final class HephaestusRenderer implements BlockEntityRenderer<HephaestusEntity> {
    private static final ResourceLocation MODEL=id("models/machines/hephaestus.obj"),BODY=id("textures/models/machines/hephaestus.png"),LAVA=id("textures/models/machines/lava.png"),COBBLE=id("textures/models/machines/cobblestone.png");
    public HephaestusRenderer(BlockEntityRendererProvider.Context context){}
    private static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void body(PoseStack pose,MultiBufferSource buffers,int light,int overlay,float rotation){
        var out=buffers.getBuffer(RenderType.entityCutoutNoCull(BODY));
        LegacyObjRenderer.renderOnly(model(),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Main");
        pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rotation));
        for(int i=0;i<3;i++){
            LegacyObjRenderer.renderOnly(model(),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Rotor");
            pose.mulPose(Axis.YP.rotationDegrees(120));
        }
        pose.popPose();
    }
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.scale(.5F,.5F,.5F);
        body(pose,buffers,light,overlay,0);
        // Item provider has white, unscrolled cobble: it does NOT call the world's half-scale texture matrix.
        LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(COBBLE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,"Core");
    }
    @Override public void render(HephaestusEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var transform=new org.joml.Matrix4f().translation(.5F,0,.5F);
        LegacyContactShadows.render(machine,model(),transform,pose,buffers);
        pose.pushPose();pose.mulPose(transform);
        float movement=machine.prevRot+(machine.rot-machine.prevRot)*partial;
        body(pose,buffers,light,overlay,movement);
        boolean on=machine.bufferedHeat>0;
        var out=on?HephaestusRenderTypes.unlitVertices(buffers.getBuffer(HephaestusRenderTypes.unlit(LAVA))):buffers.getBuffer(RenderType.entityCutout(COBBLE));
        // GL_TEXTURE scale(.5) * translate(0,movement/10): translation is scaled too.
        out=textureMatrix(out,movement/10F);
        LegacyObjRenderer.renderOnly(model(),pose,out,on?LightTexture.FULL_BRIGHT:light,overlay,on?-1:0xff7f7f7f,LegacyObjRenderer.Convention.HFR_FLAT,"Core");
        pose.popPose();
    }
    private static VertexConsumer textureMatrix(VertexConsumer out,float shift){
        return new VertexConsumer(){
            public VertexConsumer addVertex(float x,float y,float z){out.addVertex(x,y,z);return this;}
            public VertexConsumer setColor(int r,int g,int b,int a){out.setColor(r,g,b,a);return this;}
            public VertexConsumer setUv(float u,float v){out.setUv(u*.5F,(v+shift)*.5F);return this;}
            public VertexConsumer setUv1(int u,int v){out.setUv1(u,v);return this;}
            public VertexConsumer setUv2(int u,int v){out.setUv2(u,v);return this;}
            public VertexConsumer setNormal(float x,float y,float z){out.setNormal(x,y,z);return this;}
        };
    }
    @Override public boolean shouldRenderOffScreen(HephaestusEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(HephaestusEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-3,p.getY(),p.getZ()-3,p.getX()+4,p.getY()+12,p.getZ()+4);}
}
