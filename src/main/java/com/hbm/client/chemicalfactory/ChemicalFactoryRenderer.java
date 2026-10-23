package com.hbm.client.chemicalfactory;
import com.hbm.client.model.*;
import com.hbm.machine.chemicalfactory.ChemicalFactoryEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original Base/conditionalFrame and two pivoted fans, exact world yaw and source HFR smooth normals. */
public final class ChemicalFactoryRenderer implements BlockEntityRenderer<ChemicalFactoryEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/chemical_factory.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/chemical_factory.png");private static final Map<WavefrontMesh,WavefrontMesh> NO_FRAME=new WeakHashMap<>();
 public ChemicalFactoryRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 private static void parts(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String... parts){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,parts);}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){parts(poses,buffers,light,overlay,"Base","Frame","Fan1","Fan2");}
 private static WavefrontMesh withoutFrame(WavefrontMesh mesh){return NO_FRAME.computeIfAbsent(mesh,key->{var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var remap=new int[mesh.faces().size()];Arrays.fill(remap,-1);for(int i=0;i<remap.length;i++)if(!mesh.faces().get(i).legacyPart().equalsIgnoreCase("Frame")){remap[i]=faces.size();faces.add(mesh.faces().get(i));}for(var triangle:mesh.triangles())if(remap[triangle.face()]>=0)triangles.add(new WavefrontMesh.Triangle(remap[triangle.face()],triangle.first(),triangle.second(),triangle.third()));return new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());});}
 @Override public void render(ChemicalFactoryEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var animation=ChemicalFactoryAnimation.get(machine);float anim=animation.animation(partial);int yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var model=model();LegacyContactShadows.render(machine,animation.frame?model:withoutFrame(model),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));parts(poses,buffers,light,overlay,"Base");if(animation.frame)parts(poses,buffers,light,overlay,"Frame");for(int i=0;i<2;i++){double x=i==0?1:-1;poses.pushPose();poses.translate(x,0,0);poses.mulPose(Axis.YP.rotationDegrees((float)(-anim*45D%360D)));poses.translate(-x,0,0);parts(poses,buffers,light,overlay,i==0?"Fan1":"Fan2");poses.popPose();}poses.popPose();}
 @Override public boolean shouldRenderOffScreen(ChemicalFactoryEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ChemicalFactoryEntity machine){var pos=machine.getBlockPos();return new AABB(pos.getX()-2,pos.getY(),pos.getZ()-2,pos.getX()+3,pos.getY()+3,pos.getZ()+3);}
}
