package com.hbm.client.digamma;

import com.hbm.block.digamma.DigammaMatterBlock;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import org.joml.Vector3f;

/** One source six-bound random cuboid per block tessellation; full cube inventory remains the original item form. */
public final class DigammaMatterModel extends BakedModelWrapper<BakedModel> {
 private static final ModelProperty<List<BakedQuad>> QUADS=new ModelProperty<>();
 private final TextureAtlasSprite texture;
 public DigammaMatterModel(BakedModel parent,TextureAtlasSprite texture){super(parent);this.texture=texture;}
 @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){return data.derive().with(QUADS,bake(DigammaMatterBlock.sourceBounds(),world,pos)).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
  if(state==null)return originalModel.getQuads(state,side,random,data,layer);if(side!=null)return List.of();var result=data.get(QUADS);return result==null?bake(DigammaMatterBlock.sourceBounds(),null,null):result;
 }
 private List<BakedQuad> bake(float[] b,BlockAndTintGetter world,BlockPos pos){
  var result=new ArrayList<BakedQuad>(6);var bakery=new FaceBakery();var from=new Vector3f(b[0]*16,b[1]*16,b[2]*16);var to=new Vector3f(b[3]*16,b[4]*16,b[5]*16);
  for(var side:Direction.values()){
   boolean boundary=switch(side){case DOWN->b[1]==0;case UP->b[4]==1;case NORTH->b[2]==0;case SOUTH->b[5]==1;case WEST->b[0]==0;case EAST->b[3]==1;};
   if(boundary&&world!=null&&world.getBlockState(pos.relative(side)).isSolidRender(world,pos.relative(side)))continue;
   float[] uv=switch(side){
    case DOWN->new float[]{b[0],1-b[5],b[3],1-b[2]};case UP->new float[]{b[0],b[2],b[3],b[5]};
    case NORTH->new float[]{1-b[3],1-b[4],1-b[0],1-b[1]};case SOUTH->new float[]{b[0],1-b[4],b[3],1-b[1]};
    case WEST->new float[]{b[2],1-b[4],b[5],1-b[1]};case EAST->new float[]{1-b[5],1-b[4],1-b[2],1-b[1]};};
   for(int i=0;i<4;i++)uv[i]*=16;
   result.add(bakery.bakeQuad(from,to,new BlockElementFace(null,-1,"",new BlockFaceUV(uv,0)),texture,side,BlockModelRotation.X0_Y0,null,true));
  }return List.copyOf(result);
 }
}
