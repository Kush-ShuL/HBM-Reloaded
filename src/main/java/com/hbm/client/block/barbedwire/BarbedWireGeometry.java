package com.hbm.client.block.barbedwire;
import com.google.gson.*;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.bomb.nuclear.LegacyVector;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.client.model.geometry.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
/** RenderBarbedWire: original HFR polygons/UV, source metadata yaw, one scene-light sample and ObjUtil flat shading. */
public record BarbedWireGeometry(boolean inventory,float yaw) implements IUnbakedGeometry<BarbedWireGeometry> {
    public static final IGeometryLoader<BarbedWireGeometry> LOADER=(JsonObject json,JsonDeserializationContext context)->new BarbedWireGeometry(json.has("inventory")&&json.get("inventory").getAsBoolean(),json.has("source_yaw")?json.get("source_yaw").getAsFloat():0F);
    @Override public BakedModel bake(IGeometryBakingContext context,ModelBaker baker,Function<Material,TextureAtlasSprite> sprites,ModelState state,ItemOverrides overrides){
        var sprite=sprites.apply(context.getMaterial("texture"));
        var result=new SimpleBakedModel.Builder(false,true,true,context.getTransforms(),overrides).particle(sprite);
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/barbed_wire.obj"));
        float rotation=inventory?0:yaw*(float)Math.PI/180F;
        for(var face:mesh.faces()){
            var n=face.normal();var normal=LegacyVector.createVectorHelper(n.x(),n.y(),n.z());normal.rotateAroundY(rotation);
            float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;
            float shade=inventory?1F:Math.max(.45F,ny*.3F+.7F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);
            int gray=(int)(255F*shade);
            var vertices=face.corners();var corners=vertices.size()==3?List.of(vertices.get(0),vertices.get(1),vertices.get(2),vertices.get(2)):vertices;
            if(corners.size()!=4)throw new IllegalStateException("Unexpected source barbed-wire face "+corners.size());
            var quad=new QuadBakingVertexConsumer();quad.setSprite(sprite);quad.setShade(false);quad.setHasAmbientOcclusion(false);quad.setDirection(Direction.getNearest(nx,ny,nz));
            for(var corner:corners){
                var position=mesh.positions().get(corner.position());var point=LegacyVector.createVectorHelper(position.x(),position.y(),position.z());point.rotateAroundY(rotation);
                var uv=mesh.textureCoordinates().get(corner.texture());
                // Native block-item rendering subtracts(.5,.5,.5): this exactly retains source inventory T(0,-.5,0).
                quad.addVertex((float)point.xCoord+.5F,(float)point.yCoord,(float)point.zCoord+.5F).setColor(gray,gray,gray,255).setUv(sprite.getU(uv.u()),sprite.getV(1-uv.v())).setLight(0).setNormal(nx,ny,nz);
            }
            result.addUnculledFace(quad.bakeQuad());
        }
        var model=result.build(context.getRenderType(ResourceLocation.withDefaultNamespace("cutout")));
        return inventory?model:new SourceLightModel(model);
    }
    /** Source renderer sets one own-cell brightness before every actual polygon; preserve it without fullbright. */
    private static final class SourceLightModel extends BakedModelWrapper<BakedModel> {
        private static final ModelProperty<Integer> LIGHT=new ModelProperty<>();
        private static final int STRIDE=DefaultVertexFormat.BLOCK.getVertexSize()/Integer.BYTES;
        private static final int UV2=DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV2)/Integer.BYTES;
        private SourceLightModel(BakedModel original){super(original);}
        @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){return originalModel.getModelData(world,pos,state,data).derive().with(LIGHT,LevelRenderer.getLightColor(world,state,pos)).build();}
        @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
        @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
            var source=originalModel.getQuads(state,side,random,data,layer);var light=data.get(LIGHT);if(light==null||source.isEmpty())return source;
            var quads=new ArrayList<BakedQuad>(source.size());
            for(var quad:source){var vertices=quad.getVertices().clone();for(int i=0;i<4;i++)vertices[i*STRIDE+UV2]=light;quads.add(new BakedQuad(vertices,quad.getTintIndex(),quad.getDirection(),quad.getSprite(),quad.isShade(),quad.hasAmbientOcclusion()));}
            return quads;
        }
    }
}
