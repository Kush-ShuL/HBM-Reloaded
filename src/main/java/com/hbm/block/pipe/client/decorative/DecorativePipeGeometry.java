package com.hbm.block.pipe.client.decorative;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.HbmReloaded;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

/** Original RenderPipe meshes and ObjUtil flat-normal shading baked into a normal decorative block model. */
public record DecorativePipeGeometry(int shape,int axis) implements IUnbakedGeometry<DecorativePipeGeometry> {
    public static final IGeometryLoader<DecorativePipeGeometry> LOADER = (JsonObject json,JsonDeserializationContext context) -> new DecorativePipeGeometry(json.get("shape").getAsInt(),json.has("axis")?json.get("axis").getAsInt():0);
    @Override public BakedModel bake(IGeometryBakingContext context,ModelBaker baker,Function<Material,TextureAtlasSprite> spriteGetter,ModelState state,ItemOverrides overrides) {
        var world=new SimpleBakedModel.Builder(false,true,true,context.getTransforms(),overrides).particle(spriteGetter.apply(context.getMaterial("particle")));
        var inventory=new SimpleBakedModel.Builder(false,true,true,context.getTransforms(),overrides).particle(spriteGetter.apply(context.getMaterial("particle")));
        String model=shape==0?"pipe":shape==2?"pipe_quad":"pipe_rim";
        emit(model,"Top","top",context,spriteGetter,world,inventory);
        emit(model,"Side","side",context,spriteGetter,world,inventory);
        if(shape==3) { emit("pipe_frame","Frame","frame",context,spriteGetter,world,inventory); emit("pipe_frame","Mesh","mesh",context,spriteGetter,world,inventory); }
        var renderTypes=context.getRenderType(net.minecraft.resources.ResourceLocation.withDefaultNamespace("cutout"));
        BakedModel itemModel=inventory.build(renderTypes);
        return new BakedModelWrapper<BakedModel>(world.build(renderTypes)) {
            @Override public BakedModel applyTransform(ItemDisplayContext display,PoseStack poses,boolean leftHand) { return itemModel.applyTransform(display,poses,leftHand); }
        };
    }
    private void emit(String model,String part,String texture,IGeometryBakingContext context,Function<Material,TextureAtlasSprite> spriteGetter,SimpleBakedModel.Builder world,SimpleBakedModel.Builder inventory) {
        WavefrontMesh mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),HbmReloaded.id("models/blocks/"+model+".obj"));
        TextureAtlasSprite sprite=spriteGetter.apply(context.getMaterial(texture));
        int triangle=0;
        for(int faceIndex=0;faceIndex<mesh.faces().size();faceIndex++) {
            var face=mesh.faces().get(faceIndex); int first=triangle;
            while(triangle<mesh.triangles().size()&&mesh.triangles().get(triangle).face()==faceIndex) triangle++;
            if(!face.legacyPart().equalsIgnoreCase(part)) continue;
            if(face.corners().size()==4) quad(mesh,face,face.corners(),sprite,world,inventory);
            else for(int i=first;i<triangle;i++) { var tri=mesh.triangles().get(i); quad(mesh,face,List.of(face.corners().get(tri.first()),face.corners().get(tri.second()),face.corners().get(tri.third()),face.corners().get(tri.third())),sprite,world,inventory); }
        }
    }
    private void quad(WavefrontMesh mesh,WavefrontMesh.Face face,List<WavefrontMesh.Corner> corners,TextureAtlasSprite sprite,SimpleBakedModel.Builder world,SimpleBakedModel.Builder inventory) {
        Vector3f normal=rotate(new Vector3f(face.normal().x(),face.normal().y(),face.normal().z()));
        float brightness=Math.max(.45F,normal.y*.3F+.7F-Math.abs(normal.x)*.1F+Math.abs(normal.z)*.1F);
        world.addUnculledFace(bakeQuad(mesh,corners,sprite,normal,brightness));
        inventory.addUnculledFace(bakeQuad(mesh,corners,sprite,normal,1F));
    }
    private BakedQuad bakeQuad(WavefrontMesh mesh,List<WavefrontMesh.Corner> corners,TextureAtlasSprite sprite,Vector3f normal,float brightness) {
        var out=new QuadBakingVertexConsumer(); out.setSprite(sprite); out.setShade(false); out.setHasAmbientOcclusion(false); out.setDirection(Direction.getNearest(normal.x,normal.y,normal.z));
        // Match legacy Tessellator and ordinary PipeMeshRenderer: clamp before the native byte pack.
        int color=Math.clamp((int)(255*brightness),0,255);
        for(var corner:corners) {
            var point=mesh.positions().get(corner.position()); Vector3f position=rotate(new Vector3f(point.x(),point.y(),point.z())).add(.5F,.5F,.5F);
            var uv=corner.texture()<0?new WavefrontMesh.TexCoord(0,1,0):mesh.textureCoordinates().get(corner.texture());
            out.addVertex(position.x,position.y,position.z).setColor(color,color,color,255).setUv(sprite.getU(uv.u()),sprite.getV(1-uv.v())).setLight(0).setNormal(normal.x,normal.y,normal.z);
        }
        return out.bakeQuad();
    }
    private Vector3f rotate(Vector3f value) { if(axis==4||axis==8) value.rotateZ(-(float)Math.PI/2); if(axis==8) value.rotateY((float)Math.PI/2); return value; }
}
