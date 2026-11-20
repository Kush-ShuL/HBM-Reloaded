package com.hbm.machine.reactor.client;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.ResearchReactorBlockEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original base and control rod models. */
public final class ResearchReactorRenderer implements BlockEntityRenderer<ResearchReactorBlockEntity> {
    static final ResourceLocation BASE=id("models/reactors/reactor_small_base.obj"),RODS=id("models/reactors/reactor_small_rods.obj"),BASE_TEXTURE=id("textures/models/reactor_small_base.png"),ROD_TEXTURE=id("textures/models/reactor_small_rods.png");
    static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public ResearchReactorRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh mesh(ResourceLocation path){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),path);}
    private static void part(ResourceLocation model,ResourceLocation texture,PoseStack poses,MultiBufferSource buffers,int light,int overlay){part(model,texture,poses,buffers,light,overlay,false);}
    private static void part(ResourceLocation model,ResourceLocation texture,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean cull){LegacyObjRenderer.renderAll(mesh(model),poses,buffers.getBuffer(cull?RenderType.entityCutout(texture):RenderType.entityCutoutNoCull(texture)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){part(BASE,BASE_TEXTURE,poses,buffers,light,overlay,true);part(RODS,ROD_TEXTURE,poses,buffers,light,overlay,true);}
    @Override public void render(ResearchReactorBlockEntity reactor,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        com.hbm.client.model.LegacyContactShadows.render(reactor,mesh(BASE),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));part(BASE,BASE_TEXTURE,poses,buffers,light,overlay);
        poses.pushPose();poses.translate(0,reactor.lastLevel+(reactor.rodLevel-reactor.lastLevel)*partial,0);part(RODS,ROD_TEXTURE,poses,buffers,light,overlay);poses.popPose();
        poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(ResearchReactorBlockEntity reactor){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(ResearchReactorBlockEntity reactor){var p=reactor.getBlockPos();return new AABB(p.getX()-.25,p.getY(),p.getZ()-.25,p.getX()+1.25,p.getY()+4,p.getZ()+1.25);}
}
