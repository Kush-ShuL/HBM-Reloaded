package com.hbm.client.structure.forest;
import com.hbm.client.model.*;
import com.hbm.world.structure.forest.ForestFixtureBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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
import net.neoforged.neoforge.common.util.TriState;
/** Source RenderTapeBlock/ObjUtil face geometry, icon UVs and exact source normal-based shading. */
final class FixtureBakedModel extends BakedModelWrapper<BakedModel> {
 private final boolean inventory,poles;private final TextureAtlasSprite sprite;private final Map<Integer,List<BakedQuad>> quads=new java.util.concurrent.ConcurrentHashMap<>();
 FixtureBakedModel(BakedModel original,boolean inventory,boolean poles,Function<Material,TextureAtlasSprite> sprites){super(original);this.inventory=inventory;this.poles=poles;sprite=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+(poles?"steel_beam":"deco_tape_recorder"))));}
 @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType layer){return TriState.FALSE;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
 @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();int meta=inventory||state==null?-1:state.getValue(ForestFixtureBlock.META);return quads.computeIfAbsent(meta,this::build);}
 private List<BakedQuad> build(int meta){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/"+(poles?"pole":"taperecorder")+".obj"));float degrees=meta<0?0:switch(meta){case 2->90;case 3->270;case 4->180;default->0;};if(poles&&meta>=0)degrees-=90;double angle=degrees*Math.PI/180,c=Math.cos(angle),s=Math.sin(angle);var result=new ArrayList<BakedQuad>();for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());var n=face.normal();float nx=(float)(n.x()*c+n.z()*s),ny=n.y(),nz=(float)(n.z()*c-n.x()*s);float brightness=meta<0?1:Math.max(.45F,(ny+.7F)*.9F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray,packed=((byte)(nx*127)&255)|((byte)(ny*127)&255)<<8|((byte)(nz*127)&255)<<16;int[] vertices=new int[32],corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());int k=i*8;vertices[k]=Float.floatToRawIntBits((float)(p.x()*c+p.z()*s)+.5F);vertices[k+1]=Float.floatToRawIntBits(p.y());vertices[k+2]=Float.floatToRawIntBits((float)(p.z()*c-p.x()*s)+.5F);vertices[k+3]=color;vertices[k+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[k+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));vertices[k+6]=0;vertices[k+7]=packed;}result.add(new BakedQuad(vertices,-1,Direction.getNearest(nx,ny,nz),sprite,false));}return List.copyOf(result);}
}
