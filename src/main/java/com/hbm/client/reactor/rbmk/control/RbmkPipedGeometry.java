package com.hbm.client.reactor.rbmk.control;

import com.google.gson.*;
import com.hbm.client.model.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

/** Shared actual RenderRBMKControl cube/four clipped-UV pipes/inventory Lid geometry, reusable by true boiler/heater. */
public record RbmkPipedGeometry(boolean inventory,boolean top,boolean lid)implements IUnbakedGeometry<RbmkPipedGeometry> {
    public static final IGeometryLoader<RbmkPipedGeometry> LOADER=(JsonObject json,JsonDeserializationContext context)->new RbmkPipedGeometry(flag(json,"inventory"),flag(json,"top"),flag(json,"lid"));
    private static boolean flag(JsonObject json,String key){return json.has(key)&&json.get(key).getAsBoolean();}
    @Override public BakedModel bake(IGeometryBakingContext context,ModelBaker baker,Function<Material,TextureAtlasSprite> sprites,ModelState state,ItemOverrides overrides){
        var result=new SimpleBakedModel.Builder(!inventory,true,true,context.getTransforms(),overrides).particle(sprites.apply(context.getMaterial("side")));var bakery=new FaceBakery();
        for(int i=0;i<(inventory?4:1);i++){
            float min=inventory?5.2F:0,max=inventory?10.8F:16,low=inventory?-4+i*5.6F:0,high=inventory?1.6F+i*5.6F:16;
            for(var side:Direction.values()){
                var sprite=sprites.apply(context.getMaterial(side==Direction.UP?"top":side==Direction.DOWN?"bottom":"side"));
                var quad=bakery.bakeQuad(new Vector3f(min,low,min),new Vector3f(max,high,max),new BlockElementFace(side,-1,"",new BlockFaceUV(new float[]{0,0,16,16},0)),sprite,side,BlockModelRotation.X0_Y0,null,!inventory);
                if(inventory)result.addUnculledFace(quad);else result.addCulledFace(side,quad);
            }
        }
        if(inventory||top)for(int[] bounds:new int[][]{{1,1,7,7},{1,9,7,15},{9,9,15,15},{9,1,15,7}}){
            for(var side:Direction.values()){
                if(side==Direction.DOWN)continue;
                float x0=bounds[0],z0=bounds[1],x1=bounds[2],z1=bounds[3];float[] uv=switch(side){case DOWN->new float[]{x0,16-z1,x1,16-z0};case UP->new float[]{x0,z0,x1,z1};case NORTH->new float[]{16-x1,14,16-x0,16};case SOUTH->new float[]{x0,14,x1,16};case WEST->new float[]{z0,14,z1,16};case EAST->new float[]{16-z1,14,16-z0,16};};
                float low=inventory?18.4F:16,high=inventory?19.1F:18;
                if(inventory){x0=8+(x0-8)*.35F;x1=8+(x1-8)*.35F;z0=8+(z0-8)*.35F;z1=8+(z1-8)*.35F;}
                var sprite=sprites.apply(context.getMaterial(side==Direction.UP||side==Direction.DOWN?"pipe_top":"pipe_side"));
                var quad=bakery.bakeQuad(new Vector3f(x0,low,z0),new Vector3f(x1,high,z1),new BlockElementFace(null,-1,"",new BlockFaceUV(uv,0)),sprite,side,BlockModelRotation.X0_Y0,null,!inventory);result.addUnculledFace(quad);
            }
        }
        if(inventory&&lid){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/rbmk/rbmk_rods.obj"));var sprite=sprites.apply(context.getMaterial("top"));for(var face:mesh.faces())if(face.legacyPart().equals("Lid"))emitLid(result,mesh,face,sprite);}
        return result.build(context.getRenderType(ResourceLocation.withDefaultNamespace("cutout")));
    }
    private static void emitLid(SimpleBakedModel.Builder result,WavefrontMesh mesh,WavefrontMesh.Face face,TextureAtlasSprite sprite){
        var corners=face.corners();if(corners.size()==3)corners=List.of(corners.get(0),corners.get(1),corners.get(2),corners.get(2));if(corners.size()!=4)throw new IllegalStateException("Unexpected original RBMK Lid polygon "+corners.size());
        var out=new QuadBakingVertexConsumer();var normal=face.normal();out.setSprite(sprite);out.setShade(false);out.setHasAmbientOcclusion(false);out.setDirection(Direction.getNearest(normal.x(),normal.y(),normal.z()));float shade=Math.max(.45F,normal.y()*.3F+.7F-Math.abs(normal.x())*.1F+Math.abs(normal.z())*.1F);int color=(int)(shade*255);
        for(var corner:corners){var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());out.addVertex(.5F+p.x()*.35F,.8F+p.y()*.35F,.5F+p.z()*.35F).setColor(color,color,color,255).setUv(sprite.getU(uv.u()),sprite.getV(1-uv.v())).setLight(0).setNormal(normal.x(),normal.y(),normal.z());}result.addUnculledFace(out.bakeQuad());
    }
}
