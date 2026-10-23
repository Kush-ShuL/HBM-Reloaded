package com.hbm.client.reactor.rbmk.autoloader;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.rbmk.autoloader.RbmkAutoloaderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR smooth Base and separately translated Piston; item retains its separate authored pose. */
public final class RbmkAutoloaderRenderer implements BlockEntityRenderer<RbmkAutoloaderEntity> {
    static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/rbmk_autoloader.png");private static WavefrontMesh original,baseMesh;
    public RbmkAutoloaderRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/rbmk/autoloader.obj"));}
    private static WavefrontMesh base(WavefrontMesh mesh){if(mesh!=original){original=mesh;var faces=new ArrayList<WavefrontMesh.Face>();var triangles=new ArrayList<WavefrontMesh.Triangle>();var indices=new HashMap<Integer,Integer>();for(int i=0;i<mesh.faces().size();i++)if(mesh.faces().get(i).legacyPart().equals("Base")){indices.put(i,faces.size());faces.add(mesh.faces().get(i));}for(var t:mesh.triangles())if(indices.containsKey(t.face()))triangles.add(new WavefrontMesh.Triangle(indices.get(t.face()),t.first(),t.second(),t.third()));baseMesh=new WavefrontMesh(mesh.positions(),mesh.textureCoordinates(),mesh.normals(),faces,triangles,mesh.materialLibraries(),mesh.nonFaceElements(),mesh.otherDirectives());}return baseMesh;}
    static void part(String name,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean cull){LegacyObjRenderer.render(model(),poses,buffers.getBuffer(cull?RenderType.entityCutout(TEXTURE):RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,face->face.legacyPart().equals(name));}
    @Override public void render(RbmkAutoloaderEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,base(model()),new org.joml.Matrix4f().translation(.5F,0,.5F),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);part("Base",poses,buffers,light,overlay,true);double p=machine.lastPiston+(machine.renderPiston-machine.lastPiston)*partial;poses.translate(0,4-p*4,0);part("Piston",poses,buffers,light,overlay,true);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(RbmkAutoloaderEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RbmkAutoloaderEntity machine){var p=machine.getBlockPos();return new AABB(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+9,p.getZ()+1);}
}
