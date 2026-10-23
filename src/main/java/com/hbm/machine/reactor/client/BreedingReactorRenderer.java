package com.hbm.machine.reactor.client;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.BreedingReactorBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
/** Source HFR breeder model with its exact seeded two-width green/white sparks. */
public final class BreedingReactorRenderer implements BlockEntityRenderer<BreedingReactorBlockEntity> {
    static final ResourceLocation MODEL=ResearchReactorRenderer.id("models/reactors/breeder.obj"),TEXTURE=ResearchReactorRenderer.id("textures/models/machines/breeder.png");
    public BreedingReactorRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(BreedingReactorBlockEntity reactor,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int angle=switch(DummyableBlock.facing(reactor.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->360;};LegacyContactShadows.render(reactor,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));
        if(reactor.progress>0)for(int i=0;i<3;i++){poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees((float)(Math.PI*i)));spark(poses,buffers,(int)((System.currentTimeMillis()%10000)/100+i));poses.popPose();}
        inventory(poses,buffers,light,overlay);poses.popPose();
    }
    private static void spark(PoseStack poses,MultiBufferSource buffers,int seed){var random=new Random(seed);var vec=new Vec3(random.nextDouble()-.5,random.nextDouble()-.5,random.nextDouble()-.5).normalize();double x=0,y=1.5625,z=0;
        for(int i=0;i<3+random.nextInt(4);i++){double px=x,py=y,pz=z;var dir=vec.normalize();x+=dir.x*.15F*random.nextFloat();y+=dir.y*.15F*random.nextFloat();z+=dir.z*.15F*random.nextFloat();line(buffers.getBuffer(ReactorRenderTypes.SPARK_OUTER),poses,px,py,pz,x,y,z,0xff00ff00);line(buffers.getBuffer(ReactorRenderTypes.SPARK_INNER),poses,px,py,pz,x,y,z,0xffffffff);}
    }
    private static void line(VertexConsumer vertices,PoseStack poses,double ax,double ay,double az,double bx,double by,double bz,int argb){var direction=new Vec3(bx-ax,by-ay,bz-az).normalize();vertices.addVertex(poses.last(),(float)ax,(float)ay,(float)az).setColor(argb).setNormal(poses.last(),(float)direction.x,(float)direction.y,(float)direction.z);vertices.addVertex(poses.last(),(float)bx,(float)by,(float)bz).setColor(argb).setNormal(poses.last(),(float)direction.x,(float)direction.y,(float)direction.z);}
    @Override public boolean shouldRenderOffScreen(BreedingReactorBlockEntity reactor){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(BreedingReactorBlockEntity reactor){return new AABB(reactor.getBlockPos()).expandTowards(0,2,0);}
}
