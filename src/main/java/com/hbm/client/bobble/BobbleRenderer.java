package com.hbm.client.bobble;
import com.hbm.block.bobble.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import java.util.*;
public final class BobbleRenderer implements BlockEntityRenderer<BobbleBlockEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/trinkets/bobble.obj");
    private static WavefrontMesh lastModel,socketMesh;
    /** Only the socket contacts the supporting block; hidden alternative figurines/glow billboards cast no shadow. */
    private static WavefrontMesh socket(WavefrontMesh source){if(source==lastModel)return socketMesh;lastModel=source;List<WavefrontMesh.Face> faces=new ArrayList<>();List<WavefrontMesh.Triangle> triangles=new ArrayList<>();for(int i=0;i<source.faces().size();i++)if(source.faces().get(i).legacyPart().equalsIgnoreCase("Socket")){int index=faces.size();faces.add(source.faces().get(i));for(var t:source.triangles())if(t.face()==i)triangles.add(new WavefrontMesh.Triangle(index,t.first(),t.second(),t.third()));}return socketMesh=new WavefrontMesh(source.positions(),source.textureCoordinates(),source.normals(),faces,triangles,source.materialLibraries(),List.of(),List.of());}
    public BobbleRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(BobbleBlockEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        float rotation=22.5F*tile.getBlockState().getValue(BobbleBlock.META)+90;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        LegacyContactShadows.render(tile,socket(mesh),new Matrix4f().translation(.5F,0,.5F).scale(.25F).rotateY((float)Math.toRadians(-rotation)),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.scale(.25F,.25F,.25F);poses.mulPose(Axis.YP.rotationDegrees(-rotation));draw(tile.type,poses,buffers,light,overlay,System.currentTimeMillis());poses.popPose();
    }
    static void draw(BobbleType type,PoseStack poses,MultiBufferSource buffers,int light,int overlay,long time){new BobblePose(new BobbleDraw(poses,buffers,light,overlay),time).renderBobble(type);}
}
