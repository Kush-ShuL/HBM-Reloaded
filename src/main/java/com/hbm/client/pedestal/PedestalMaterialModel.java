package com.hbm.client.pedestal;
import com.hbm.client.model.*;
import com.hbm.machine.pedestal.*;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
/** Original RenderChain vertices and noSmooth BarbedWire OBJ, with exact ObjUtil whole-model shading and icon UVs. */
final class PedestalMaterialModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Boolean> END=new ModelProperty<>();
    private final boolean wire,inventory;private final TextureAtlasSprite texture,endTexture;private final Map<Integer,List<BakedQuad>> cache=new java.util.concurrent.ConcurrentHashMap<>();
    PedestalMaterialModel(BakedModel original,boolean wire,boolean inventory,Function<Material,TextureAtlasSprite> sprites){super(original);this.wire=wire;this.inventory=inventory;texture=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+(wire?"barbed_wire_model":"chain"))));endTexture=wire?texture:sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/chain_end")));}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public net.neoforged.neoforge.common.util.TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return net.neoforged.neoforge.common.util.TriState.FALSE;}
    @Override public boolean isGui3d(){return wire;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){getTransforms().getTransform(context).apply(left,pose);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return !wire&&state.getBlock() instanceof SourceDungeonChainBlock chain?data.derive().with(END,chain.end(state,level,pos)).build():data;}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();int meta=state==null?0:state.getValue(wire?SourceBarbedWireBlock.META:SourceDungeonChainBlock.META);boolean end=!wire&&(data.has(END)?Boolean.TRUE.equals(data.get(END)):state!=null&&state.getValue(SourceDungeonChainBlock.END));return cache.computeIfAbsent(meta+(end?8:0),key->wire?wire(meta):chain(meta,end));}
    private List<BakedQuad> wire(int meta){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/barbed_wire.obj"));var result=new ArrayList<BakedQuad>();float angle=inventory?0:meta<4?-(float)Math.PI/2:-(float)Math.PI;double c=net.minecraft.util.Mth.cos(angle),s=net.minecraft.util.Mth.sin(angle);
        for(var triangle:mesh.triangles()){
            var face=mesh.faces().get(triangle.face());var n=face.normal();float nx=(float)(n.x()*c+n.z()*s),ny=n.y(),nz=(float)(n.z()*c-n.x()*s);float brightness=inventory?1:Math.max(.45F,(ny+.7F)*.9F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);
            double[][] vertices=new double[4][5];int[] corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
            for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var v=mesh.positions().get(corner.position());var uv=corner.texture()>=0?mesh.textureCoordinates().get(corner.texture()):new WavefrontMesh.TexCoord(0,0,0);vertices[i]=new double[]{v.x()*c+v.z()*s+.5,v.y(),v.z()*c-v.x()*s+.5,uv.u(),1-uv.v()};}
            result.add(quad(vertices,texture,nx,ny,nz,brightness));
        }return List.copyOf(result);
    }
    private List<BakedQuad> chain(int meta,boolean end){
        var sprite=end?endTexture:texture;var result=new ArrayList<BakedQuad>();double[][] front;
        if(meta==0){
            result.add(quad(new double[][]{{0,1,0,0,0},{0,0,0,0,1},{1,0,1,1,1},{1,1,1,1,0}},sprite,-.70710677F,0,.70710677F,1));
            result.add(quad(new double[][]{{1,1,1,0,0},{1,0,1,0,1},{0,0,0,1,1},{0,1,0,1,0}},sprite,.70710677F,0,-.70710677F,1));
            result.add(quad(new double[][]{{0,1,1,0,0},{0,0,1,0,1},{1,0,0,1,1},{1,1,0,1,0}},sprite,.70710677F,0,.70710677F,1));
            result.add(quad(new double[][]{{1,1,0,0,0},{1,0,0,0,1},{0,0,1,1,1},{0,1,1,1,0}},sprite,-.70710677F,0,-.70710677F,1));return List.copyOf(result);
        }
        if(meta==5)front=new double[][]{{.05,1,1,0,0},{.05,0,1,0,1},{.05,0,0,1,1},{.05,1,0,1,0}};
        else if(meta==4)front=new double[][]{{.95,0,1,1,1},{.95,1,1,1,0},{.95,1,0,0,0},{.95,0,0,0,1}};
        else if(meta==3)front=new double[][]{{1,0,.05,1,1},{1,1,.05,1,0},{0,1,.05,0,0},{0,0,.05,0,1}};
        else if(meta==2)front=new double[][]{{1,1,.95,0,0},{1,0,.95,0,1},{0,0,.95,1,1},{0,1,.95,1,0}};
        else return List.of();
        float nx=meta==5?1:meta==4?-1:0,nz=meta==3?1:meta==2?-1:0;result.add(quad(front,sprite,nx,0,nz,1));result.add(quad(new double[][]{front[1],front[0],front[3],front[2]},sprite,-nx,0,-nz,1));return List.copyOf(result);
    }
    private static BakedQuad quad(double[][] source,TextureAtlasSprite sprite,float nx,float ny,float nz,float brightness){
        int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray,normal=((byte)(nx*127)&255)|((byte)(ny*127)&255)<<8|((byte)(nz*127)&255)<<16;var data=new int[32];
        for(int i=0;i<4;i++){int a=i*8;var v=source[i];data[a]=Float.floatToRawIntBits((float)v[0]);data[a+1]=Float.floatToRawIntBits((float)v[1]);data[a+2]=Float.floatToRawIntBits((float)v[2]);data[a+3]=color;data[a+4]=Float.floatToRawIntBits(sprite.getU((float)v[3]));data[a+5]=Float.floatToRawIntBits(sprite.getV((float)v[4]));data[a+6]=0;data[a+7]=normal;}
        return new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),sprite,false);
    }
}
