package com.hbm.client.assemblyfactory;

import com.hbm.client.model.*;
import com.hbm.machine.assemblyfactory.AssemblyFactoryEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;

/** Original HFR body, conditional frame, four cumulative arm hierarchies and exact animated sparks. */
public final class AssemblyFactoryRenderer implements BlockEntityRenderer<AssemblyFactoryEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/assembly_factory.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/assembly_factory.png");
    private static final Map<WavefrontMesh,WavefrontMesh> NO_FRAME=new WeakHashMap<>();
    public AssemblyFactoryRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String name){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    private static WavefrontMesh withoutFrame(WavefrontMesh mesh){return NO_FRAME.computeIfAbsent(mesh,key->{var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var remap=new int[mesh.faces().size()];Arrays.fill(remap,-1);for(int i=0;i<remap.length;i++)if(!mesh.faces().get(i).legacyPart().equalsIgnoreCase("Frame")){remap[i]=faces.size();faces.add(mesh.faces().get(i));}for(var triangle:mesh.triangles())if(remap[triangle.face()]>=0)triangles.add(new WavefrontMesh.Triangle(remap[triangle.face()],triangle.first(),triangle.second(),triangle.third()));return new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());});}
    private static void pivot(PoseStack pose,double y,double z,double angle){pose.translate(0,y,z);pose.mulPose(Axis.XP.rotationDegrees((float)angle));pose.translate(0,-y,-z);}
    private static void arm(PoseStack pose,MultiBufferSource buffers,int light,int overlay,int index,double slide,double[] values){
        int sign=index==0||index==3?-1:1,number=index+1;
        pose.pushPose();pose.translate(sign<0?.5-slide:-.5+slide,0,0);part(pose,buffers,light,overlay,"Slider"+number);
        pivot(pose,1.625,.9375*sign,values[0]*sign);part(pose,buffers,light,overlay,"ArmLower"+number);
        pivot(pose,2.375,.9375*sign,values[1]*sign);part(pose,buffers,light,overlay,"ArmUpper"+number);
        pivot(pose,2.375,.4375*sign,values[2]*sign);part(pose,buffers,light,overlay,"Head"+number);
        pose.translate(0,values[3],0);part(pose,buffers,light,overlay,"Striker"+number);
        if(index==1||index==3){pivot(pose,1.625,.3125*sign,-values[4]*sign);part(pose,buffers,light,overlay,"Blade"+number);}
        pose.popPose();
    }
    @Override public void render(AssemblyFactoryEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var state=AssemblyFactoryAnimation.get(machine);int yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};
        LegacyContactShadows.render(machine,state.frame?model():withoutFrame(model()),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));part(pose,buffers,light,overlay,"Base");if(state.frame)part(pose,buffers,light,overlay,"Frame");
        double slide1=state.animations[0].getSlider(partial),slide2=state.animations[1].getSlider(partial);
        double[][] arms={state.animations[0].striker.getPositions(partial),state.animations[0].saw.getPositions(partial),state.animations[1].striker.getPositions(partial),state.animations[1].saw.getPositions(partial)};
        for(int i=0;i<4;i++)arm(pose,buffers,light,overlay,i,i<2?slide1:slide2,arms[i]);
        var player=Minecraft.getInstance().player;var p=machine.getBlockPos();
        if(player!=null&&player.distanceToSqr(p.getX()+.5,p.getY()+1,p.getZ()+.5)<1225){
            for(int i=0;i<4;i++){
                var recipe=machine.modules[i].getRecipe();if(recipe==null)continue;
                pose.pushPose();pose.translate(1.5-i,0,0);pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,1.0625,0);
                var stack=recipe.icon().copyWithCount(1);
                if(stack.getItem() instanceof BlockItem){boolean model3d=Minecraft.getInstance().getItemRenderer().getModel(stack,machine.getLevel(),null,0).isGui3d();pose.translate(0,model3d?-.0625:-.125,0);if(!model3d)pose.scale(.5F,.5F,.5F);}
                else{pose.mulPose(Axis.XP.rotationDegrees(-90));pose.translate(0,-.25,0);}
                pose.scale(1.25F,1.25F,1.25F);Minecraft.getInstance().getItemRenderer().renderStatic(stack,ItemDisplayContext.FIXED,light,overlay,pose,buffers,machine.getLevel(),0);pose.popPose();
            }
            double u=(machine.getLevel().getGameTime()/10D+partial)%10;
            if(arms[1][3]<=-.375){pose.pushPose();pose.translate(.5+slide1,1.0625,-arms[1][2]/45D);sparks(pose,buffers,u,1);pose.popPose();}
            if(arms[3][3]<=-.375){pose.pushPose();pose.translate(-.5-slide2,1.0625,arms[3][2]/45D);sparks(pose,buffers,u,-1);pose.popPose();}
        }
        pose.popPose();
    }
    private static void sparks(PoseStack pose,MultiBufferSource buffers,double u,int sign){
        var out=buffers.getBuffer(AssemblyFactoryRenderTypes.SPARKS);var matrix=pose.last().pose();float length=1.25F*sign,wide=.1875F,epsilon=.01F;
        out.addVertex(matrix,-epsilon,-wide,length).setUv((float)u+.5F,0).setColor(0x00ffffff);
        out.addVertex(matrix,-epsilon,wide,length).setUv((float)u+.5F,1).setColor(0x00ffffff);
        out.addVertex(matrix,-epsilon,0,0).setUv((float)u+1.5F,1).setColor(0xffffffff);
        out.addVertex(matrix,-epsilon,0,0).setUv((float)u+1.5F,0).setColor(0xffffffff);
        out.addVertex(matrix,epsilon,-wide,length).setUv((float)u,1).setColor(0x00ffffff);
        out.addVertex(matrix,epsilon,wide,length).setUv((float)u,0).setColor(0x00ffffff);
        out.addVertex(matrix,epsilon,0,0).setUv((float)u+1,0).setColor(0xffffffff);
        out.addVertex(matrix,epsilon,0,0).setUv((float)u+1,1).setColor(0xffffffff);
    }
    @Override public boolean shouldRenderOffScreen(AssemblyFactoryEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(AssemblyFactoryEntity machine){var pos=machine.getBlockPos();return new AABB(pos.getX()-2,pos.getY(),pos.getZ()-2,pos.getX()+3,pos.getY()+3,pos.getZ()+3);}
}
