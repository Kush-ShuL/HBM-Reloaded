package com.hbm.client.barrel;

import com.hbm.client.model.*;
import com.hbm.machine.barrel.BarrelContent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Original noSmooth Barrel part, icon UV mapping and ObjUtil per-face world shading. */
public final class BarrelBakedModel extends BakedModelWrapper<BakedModel> {
    private final boolean inventory;private final TextureAtlasSprite sprite;private List<BakedQuad> quads;
    BarrelBakedModel(BakedModel original,boolean inventory,Function<Material,TextureAtlasSprite> sprites,BarrelContent.Variant variant){this(original,inventory,sprites,ResourceLocation.fromNamespaceAndPath("hbm","block/"+variant.id()));}
    public BarrelBakedModel(BakedModel original,boolean inventory,Function<Material,TextureAtlasSprite> sprites,ResourceLocation texture){super(original);this.inventory=inventory;this.sprite=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,texture));}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public net.neoforged.neoforge.common.util.TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return net.neoforged.neoforge.common.util.TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){getTransforms().getTransform(context).apply(left,pose);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){if(side!=null)return List.of();if(quads==null)quads=build();return quads;}
    private List<BakedQuad> build(){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.fromNamespaceAndPath("hbm","models/blocks/barrel.obj"));var result=new ArrayList<BakedQuad>();
        for(var triangle:mesh.triangles()){
            var face=mesh.faces().get(triangle.face());if(!face.legacyPart().equalsIgnoreCase("Barrel"))continue;var normal=face.normal();float brightness=inventory?1:Math.max(.45F,normal.y()*.3F+.7F-Math.abs(normal.x())*.1F+Math.abs(normal.z())*.1F);
            int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray;
            int packed=((byte)(normal.x()*127)&255)|((byte)(normal.y()*127)&255)<<8|((byte)(normal.z()*127)&255)<<16;var vertices=new int[32];int[] corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
            for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var pos=mesh.positions().get(corner.position());var uv=corner.texture()>=0?mesh.textureCoordinates().get(corner.texture()):new WavefrontMesh.TexCoord(0,0,0);int offset=i*8;vertices[offset]=Float.floatToRawIntBits(pos.x()+.5F);vertices[offset+1]=Float.floatToRawIntBits(pos.y());vertices[offset+2]=Float.floatToRawIntBits(pos.z()+.5F);vertices[offset+3]=color;vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));vertices[offset+6]=0;vertices[offset+7]=packed;}
            result.add(new BakedQuad(vertices,-1,Direction.getNearest(normal.x(),normal.y(),normal.z()),sprite,false));
        }
        return List.copyOf(result);
    }
}
