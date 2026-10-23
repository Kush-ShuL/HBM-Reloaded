package com.hbm.client.phosphorvine;

import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;

/** Exact RenderHangingVine .45-factor, four double-sided source quads plus the same four source glow quads. */
final class PhosphorVineModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> CHOICE=new ModelProperty<>();
    private final List<BakedQuad>[] choices;
    @SuppressWarnings("unchecked") PhosphorVineModel(BakedModel original,Function<Material,TextureAtlasSprite> sprites){super(original);choices=(List<BakedQuad>[])new List<?>[6];String[] base={"vine_phosphor_ground","vine_phosphor","vine_phosphor_hang"};for(int kind=0;kind<3;kind++)for(int air=0;air<2;air++){var quads=new ArrayList<BakedQuad>();cross(quads,sprites.apply(material(base[kind])),0);cross(quads,sprites.apply(material(air==1?"vine_phosphor_spots_hang":"vine_phosphor_spots")),240);choices[kind*2+air]=List.copyOf(quads);}}
    private static Material material(String name){return new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+name));}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public net.neoforged.neoforge.common.util.TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return net.neoforged.neoforge.common.util.TriState.FALSE;}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){var below=world.getBlockState(pos.below());int base=below.isFaceSturdy(world,pos,Direction.UP)?0:below.is(state.getBlock())?1:2;return data.derive().with(CHOICE,base*2+(below.isAir()?1:0)).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return side!=null?List.of():choices[data.has(CHOICE)?data.get(CHOICE):5];}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side!=null?List.of():choices[5];}
    private static void cross(List<BakedQuad> quads,TextureAtlasSprite sprite,int light){float a=.05F,b=.95F;float[][][] faces={{{a,0,a},{a,1,a},{b,1,b},{b,0,b}},{{b,0,b},{b,1,b},{a,1,a},{a,0,a}},{{b,0,a},{b,1,a},{a,1,b},{a,0,b}},{{a,0,b},{a,1,b},{b,1,a},{b,0,a}}};for(var face:faces){float nx=face[2][2]-face[0][2],nz=-(face[2][0]-face[0][0]),length=(float)Math.sqrt(nx*nx+nz*nz);nx/=length;nz/=length;int normal=((byte)(nx*127)&255)|((byte)(nz*127)&255)<<16;var vertices=new int[32];for(int i=0;i<4;i++){int o=i*8;vertices[o]=Float.floatToRawIntBits(face[i][0]);vertices[o+1]=Float.floatToRawIntBits(face[i][1]);vertices[o+2]=Float.floatToRawIntBits(face[i][2]);vertices[o+3]=0xffffffff;vertices[o+4]=Float.floatToRawIntBits(sprite.getU(i<2?1F:0F));vertices[o+5]=Float.floatToRawIntBits(sprite.getV(i==0||i==3?1F:0F));vertices[o+6]=light;vertices[o+7]=normal;}quads.add(new BakedQuad(vertices,-1,Direction.getNearest(nx,0,nz),sprite,false,false));}}
}
