package com.hbm.world.structure.templates.client;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
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
import net.neoforged.neoforge.common.util.TriState;

/** Original RenderSpikeBlock: all 96 authored triangles, icon UVs, noSmooth face normals and source ObjUtil shade. */
final class TemplateSpikesModel extends BakedModelWrapper<BakedModel> {
    private final TextureAtlasSprite sprite;
    private final List<BakedQuad> quads;
    TemplateSpikesModel(BakedModel parent,boolean inventory,Function<Material,TextureAtlasSprite> textures){
        super(parent);sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/spikes")));
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.fromNamespaceAndPath("hbm","models/blocks/spikes.obj"));var result=new ArrayList<BakedQuad>();
        for(var face:mesh.faces()){
            if(face.corners().size()!=3)throw new IllegalStateException("Source spike face is not a triangle at "+face.sourceLine());
            var a=mesh.positions().get(face.corners().get(0).position());var b=mesh.positions().get(face.corners().get(1).position());var c=mesh.positions().get(face.corners().get(2).position());
            double ax=b.x()-a.x(),ay=b.y()-a.y(),az=b.z()-a.z(),bx=c.x()-a.x(),by=c.y()-a.y(),bz=c.z()-a.z();
            var n=LegacyVector.createVectorHelper(ay*bz-az*by,az*bx-ax*bz,ax*by-ay*bx).normalize();float nx=(float)n.xCoord,ny=(float)n.yCoord,nz=(float)n.zCoord;
            float shade=inventory?1F:Math.max(.45F,(ny+.7F)*.9F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);int gray=Math.clamp((int)(shade*255F),0,255),color=0xff000000|gray<<16|gray<<8|gray;
            int normal=((byte)(nx*127F)&255)|((byte)(ny*127F)&255)<<8|((byte)(nz*127F)&255)<<16;int[] packed=new int[32],corners={0,1,2,2};
            for(int i=0;i<4;i++){
                var corner=face.corners().get(corners[i]);var point=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());int at=i*8;
                // Source world T(x+.5,y,z+.5). Inventory T(0,-.5,0) cancels native ItemRenderer's Y centering.
                packed[at]=Float.floatToRawIntBits(point.x()+.5F);packed[at+1]=Float.floatToRawIntBits(point.y());packed[at+2]=Float.floatToRawIntBits(point.z()+.5F);packed[at+3]=color;
                packed[at+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));packed[at+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));packed[at+6]=0;packed[at+7]=normal;
            }
            result.add(new BakedQuad(packed,-1,Direction.getNearest(nx,ny,nz),sprite,false));
        }
        quads=List.copyOf(result);
    }
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public TextureAtlasSprite getParticleIcon(){return sprite;}
    @Override public TextureAtlasSprite getParticleIcon(ModelData data){return sprite;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads:List.of();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return getQuads(state,side,random);}
}
