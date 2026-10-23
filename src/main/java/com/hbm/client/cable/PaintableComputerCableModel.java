package com.hbm.client.cable;
import com.hbm.compat.computercraft.cable.PaintableComputerCableEntity;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Exact source3pass full-cube model; same existing CableQuads UV/shape backend. Item stays source pass0 only. */
final class PaintableComputerCableModel extends BakedModelWrapper<BakedModel> {
    private final TextureAtlasSprite base,overlay,color;
    PaintableComputerCableModel(BakedModel parent,Function<Material,TextureAtlasSprite> textures){super(parent);base=sprite(textures,"oc_cable_base");overlay=sprite(textures,"oc_cable_overlay");color=sprite(textures,"oc_cable_color");}
    private static TextureAtlasSprite sprite(Function<Material,TextureAtlasSprite> textures,String name){return textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+name)));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
        if(state==null)return originalModel.getQuads(null,side,random,ModelData.EMPTY,layer);if(side==null)return List.of();
        var paint=data.get(PaintableComputerCableEntity.PAINT);var source=base;
        if(paint!=null&&!paint.is(state.getBlock())){
            // Source getIcon(face,meta), with this cable's white colourMultiplier. Do not copy facade's geometry/tint.
            var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(paint);var quads=model.getQuads(paint,side,random,ModelData.EMPTY,null);
            if(!quads.isEmpty())source=quads.getFirst().getSprite();else{
                var general=model.getQuads(paint,null,random,ModelData.EMPTY,null);source=general.stream().filter(q->q.getDirection()==side).findFirst().map(BakedQuad::getSprite).orElseGet(()->model.getParticleIcon(ModelData.EMPTY));
            }
        }
        var result=new ArrayList<BakedQuad>(3);var points=CableQuads.face(side,0,0,0,1,1,1);
        result.add(CableQuads.face(source,side,points,0,0,1,1,0));
        result.add(CableQuads.face(overlay,side,points,0,0,1,1,0));
        var tinted=CableQuads.face(color,side,points,0,0,1,1,0);result.add(new BakedQuad(tinted.getVertices(),1,side,color,true));
        return result;
    }
}
