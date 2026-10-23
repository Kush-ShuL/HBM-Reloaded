package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.client.model.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
/** Exact original RenderBoiler HFR smooth mesh, source pressure breathing and uncullable burst body. */
public final class HeatBoilerRenderer implements BlockEntityRenderer<HeatBoilerBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/boiler.obj"),BURST=ResourceLocation.parse("hbm:models/machines/boiler_burst.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/boiler.png");
    public HeatBoilerRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(boolean burst){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),burst?BURST:MODEL);}
    static void model(boolean burst,boolean cull,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(burst),poses,buffers.getBuffer(cull?RenderType.entityCutout(TEXTURE):RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(HeatBoilerBlockEntity boiler,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int angle=switch(DummyableBlock.facing(boiler.getBlockState())){case WEST->270;case NORTH->180;case EAST->90;default->0;};var local=new PoseStack();local.translate(.5D,0D,.5D);local.mulPose(Axis.YP.rotationDegrees(angle));LegacyContactShadows.render(boiler,model(boiler.hasExploded),new Matrix4f(local.last().pose()),poses,buffers);poses.pushPose();poses.translate(.5D,0D,.5D);poses.mulPose(Axis.YP.rotationDegrees(angle));if(!boiler.hasExploded&&boiler.tanks[1].getFill()>boiler.tanks[1].getMaxFill()*.9D){double sine=Math.sin(System.currentTimeMillis()/50D%(Math.PI*2))*.01D;poses.scale((float)(1D-sine),(float)(1D+sine),(float)(1D-sine));}model(boiler.hasExploded,!boiler.hasExploded,poses,buffers,light,overlay);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(HeatBoilerBlockEntity boiler){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(HeatBoilerBlockEntity boiler){var pos=boiler.getBlockPos();return new AABB(pos.getX()-1,pos.getY(),pos.getZ()-1,pos.getX()+2,pos.getY()+4,pos.getZ()+2);}
}
