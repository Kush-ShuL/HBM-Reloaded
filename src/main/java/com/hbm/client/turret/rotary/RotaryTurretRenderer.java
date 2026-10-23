package com.hbm.client.turret.rotary;
import com.hbm.client.model.*;
import com.hbm.energy.HbmEnergy;
import com.hbm.machine.turret.rotary.*;
import com.hbm.machine.turret.TurretBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original Chekhov/Friendly shared HFR geometry, separately textured base/carriage/body/barrels and actual connected plugs. */
public final class RotaryTurretRenderer implements BlockEntityRenderer<RotaryTurretEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/turrets/turret_chekhov.obj");private static WavefrontMesh shadowSource,shadowBase;
    public RotaryTurretRenderer(BlockEntityRendererProvider.Context context){}
    private static ResourceLocation texture(String name){return ResourceLocation.parse("hbm:textures/models/turrets/"+name+".png");}
    public static WavefrontMesh shadow(WavefrontMesh mesh){if(shadowSource!=mesh){shadowSource=mesh;var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var map=new HashMap<Integer,Integer>();for(int i=0;i<mesh.faces().size();i++)if(mesh.faces().get(i).legacyPart().equalsIgnoreCase("Base")){map.put(i,faces.size());faces.add(mesh.faces().get(i));}for(var triangle:mesh.triangles()){var next=map.get(triangle.face());if(next!=null)triangles.add(new WavefrontMesh.Triangle(next,triangle.first(),triangle.second(),triangle.third()));}shadowBase=new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());}return shadowBase;}
    public static void part(WavefrontMesh mesh,PoseStack pose,MultiBufferSource buffers,int light,int overlay,String material,String part){LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(RenderType.entityCutout(texture(material))),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
    @Override public void render(RotaryTurretEntity turret,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var offset=turret.horizontalOffset();LegacyContactShadows.render(turret,shadow(mesh),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),pose,buffers);pose.pushPose();pose.translate(offset.x,0,offset.z);connectors(turret,mesh,pose,buffers,light,overlay);boolean friendly=turret.kind==RotaryTurretKind.FRIENDLY;part(mesh,pose,buffers,light,overlay,friendly?"base_friendly":"base","Base");pose.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));part(mesh,pose,buffers,light,overlay,friendly?"carriage_friendly":"carriage","Carriage");pose.translate(0,1.5,0);pose.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));pose.translate(0,-1.5,0);part(mesh,pose,buffers,light,overlay,"chekhov","Body");pose.translate(0,1.5,0);pose.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partial,turret.lastSpin,turret.spin)));pose.translate(0,-1.5,0);part(mesh,pose,buffers,light,overlay,"chekhov_barrels","Barrels");pose.popPose();}
    public static void connectors(TurretBaseEntity turret,WavefrontMesh mesh,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var offset=turret.horizontalOffset();int x=(int)(turret.getBlockPos().getX()+offset.x),y=turret.getBlockPos().getY(),z=(int)(turret.getBlockPos().getZ()+offset.z);int[][] data={{-2,0,0,0,4},{-2,-1,-1,0,4},{-1,1,-1,90,3},{0,1,0,90,3},{1,0,-1,180,5},{1,-1,0,180,5},{0,-2,-1,270,2},{-1,-2,0,270,2}};for(var plug:data){var pos=new BlockPos(x+plug[0],y,z+plug[1]);var direction=Direction.from3DDataValue(plug[4]);if(!turret.getLevel().hasChunkAt(pos)||turret.getLevel().getCapability(HbmEnergy.HE,pos,direction.getOpposite())==null)continue;pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(plug[3]));pose.translate(0,0,plug[2]);part(mesh,pose,buffers,light,overlay,"connector","Connectors");pose.popPose();}}
    @Override public boolean shouldRenderOffScreen(RotaryTurretEntity turret){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RotaryTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
