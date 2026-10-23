package com.hbm.client.chemicalplant;

import com.hbm.client.model.*;
import com.hbm.fluid.FluidStack;
import com.hbm.machine.chemicalplant.ChemicalPlantBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original Base/Frame, soft peak Slider, pivoted Spinner and scrolling fluid. */
public final class ChemicalPlantRenderer implements BlockEntityRenderer<ChemicalPlantBlockEntity> {
    static final ResourceLocation MODEL=id("models/machines/chemical_plant.obj"),TEXTURE=id("textures/models/machines/chemical_plant.png"),FLUID=id("textures/models/machines/chemical_plant_fluid.png");
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public ChemicalPlantRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void parts(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String...parts){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,parts);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){parts(poses,buffers,light,overlay,"Base","Slider","Spinner","Frame");}
    private static double sps(double x){return Math.sin(Math.PI/2*Math.cos(x));}
    @Override public void render(ChemicalPlantBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int contactAngle=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};com.hbm.client.model.LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(contactAngle)),poses,buffers);
        var state=ChemicalPlantAnimation.get(machine);float anim=state.animation(partial);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;}));
        parts(poses,buffers,light,overlay,"Base");if(state.frame)parts(poses,buffers,light,overlay,"Frame");
        poses.pushPose();poses.translate(sps(anim*.125)*.375,0,0);parts(poses,buffers,light,overlay,"Slider");poses.popPose();
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(anim*15%360));poses.translate(-.5,0,-.5);parts(poses,buffers,light,overlay,"Spinner");poses.popPose();
        var recipe=machine.module.getRecipe();
        if(machine.processing()&&recipe!=null){
            List<FluidStack> colors=recipe.outputFluids().isEmpty()?recipe.inputFluids():recipe.outputFluids();
            if(!colors.isEmpty()){
                int red=0,green=0,blue=0;for(var fluid:colors){int color=fluid.type.getColor();red+=color>>16&255;green+=color>>8&255;blue+=color&255;}
                VertexConsumer vertices=new FluidVertices(buffers.getBuffer(ChemicalPlantRenderTypes.fluid(red,green,blue,colors.size())),-anim/100F,(float)(sps(anim*.1)*.1-.25));
                LegacyObjRenderer.renderOnly(model(),poses,vertices,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.HFR,"Fluid");
            }
        }
        poses.popPose();
    }
    private record FluidVertices(VertexConsumer delegate,float offsetU,float offsetV)implements VertexConsumer{
        @Override public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,a);return this;}
        @Override public VertexConsumer setUv(float u,float v){delegate.setUv(u+offsetU,v+offsetV);return this;}
        @Override public VertexConsumer setUv1(int u,int v){delegate.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
    }
    @Override public boolean shouldRenderOffScreen(ChemicalPlantBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(ChemicalPlantBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
