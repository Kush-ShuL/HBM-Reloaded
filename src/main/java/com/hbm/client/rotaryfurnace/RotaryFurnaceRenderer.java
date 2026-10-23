package com.hbm.client.rotaryfurnace;
import com.hbm.client.model.*;
import com.hbm.machine.rotaryfurnace.RotaryFurnaceEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR Furnace/Piston with exact source soft-peak sine translation. */
public final class RotaryFurnaceRenderer implements BlockEntityRenderer<RotaryFurnaceEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/rotary_furnace.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/rotary_furnace.png");
 private static final Map<WavefrontMesh,Map<String,WavefrontMesh>> PARTS=new WeakHashMap<>();
 public RotaryFurnaceRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String name){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 private static WavefrontMesh mesh(String part){var full=model();return PARTS.computeIfAbsent(full,key->new HashMap<>()).computeIfAbsent(part,key->{var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var remap=new int[full.faces().size()];Arrays.fill(remap,-1);for(int i=0;i<remap.length;i++)if(full.faces().get(i).legacyPart().equals(part)){remap[i]=faces.size();faces.add(full.faces().get(i));}for(var triangle:full.triangles())if(remap[triangle.face()]>=0)triangles.add(new WavefrontMesh.Triangle(remap[triangle.face()],triangle.first(),triangle.second(),triangle.third()));return new WavefrontMesh(full.positions(),full.textureCoordinates(),full.normals(),faces,triangles,full.materialLibraries(),full.nonFaceElements(),full.otherDirectives());});}
 @Override public void render(RotaryFurnaceEntity furnace,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=switch(DummyableBlock.facing(furnace.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};float anim=furnace.lastAnim+(furnace.anim-furnace.lastAnim)*partial;double piston=Math.sin(Math.PI/2D*Math.cos((anim*.75)*.125))*.5-.5;var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(furnace,mesh("Furnace"),transform,pose,buffers);LegacyContactShadows.render(furnace,mesh("Piston"),new org.joml.Matrix4f(transform).translate(0,(float)piston,0),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));part(pose,buffers,light,overlay,"Furnace");pose.translate(0,piston,0);part(pose,buffers,light,overlay,"Piston");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(RotaryFurnaceEntity mill){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RotaryFurnaceEntity mill){var p=mill.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+5,p.getZ()+3);}
}
