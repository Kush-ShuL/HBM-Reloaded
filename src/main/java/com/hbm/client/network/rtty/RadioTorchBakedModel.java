package com.hbm.client.network.rtty;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.hbm.network.rtty.*;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.TriState;
/** RenderRTTY's noSmooth face normals, white vertices, source Z-then-Y rotations and unmodified icon UVs. */
public final class RadioTorchBakedModel extends BakedModelWrapper<BakedModel> {
 private static final ModelProperty<Boolean> ACTIVE=new ModelProperty<>();
 private static final ResourceLocation OBJ=ResourceLocation.parse("hbm:models/blocks/rtty.obj");
 private final TextureAtlasSprite off,on;private final List<BakedQuad> inactive,active;private final java.util.function.Predicate<net.minecraft.world.level.block.entity.BlockEntity> activeTest;
 RadioTorchBakedModel(BakedModel original,boolean sender,int meta,Function<Material,TextureAtlasSprite> textures){this(original,sender?"rtty_sender":"rtty_rec",meta,textures,entity->entity instanceof RadioTorchEntity radio&&radio.lastState>0);}
 public RadioTorchBakedModel(BakedModel original,String stem,int meta,Function<Material,TextureAtlasSprite> textures,java.util.function.Predicate<net.minecraft.world.level.block.entity.BlockEntity> activeTest){super(original);this.activeTest=activeTest;off=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+stem+"_off")));on=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+stem+"_on")));inactive=bake(meta,off);active=bake(meta,on);}
 @Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType layer){return TriState.FALSE;}
 @Override public boolean isCustomRenderer(){return false;}@Override public TextureAtlasSprite getParticleIcon(){return off;}@Override public TextureAtlasSprite getParticleIcon(ModelData data){return off;}
 @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data){return data.derive().with(ACTIVE,activeTest.test(level.getBlockEntity(pos))).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?inactive:List.of();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return side!=null?List.of():Boolean.TRUE.equals(data.get(ACTIVE))?active:inactive;}
 private static List<BakedQuad> bake(int meta,TextureAtlasSprite sprite){
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),OBJ);float pitch=meta==0?(float)Math.PI:0,rotation=0;
  if(meta==2)rotation=90F/180F*(float)Math.PI;if(meta==3)rotation=270F/180F*(float)Math.PI;if(meta==4)rotation=180F/180F*(float)Math.PI;if(rotation!=0||meta==5)pitch=(float)Math.PI*.5F;
  var result=new ArrayList<BakedQuad>(mesh.faces().size());
  for(var face:mesh.faces()){
   if(face.corners().size()!=3)throw new IllegalStateException("Original RTTY OBJ must retain its triangulated faces at line "+face.sourceLine());
   var normal=normal(mesh,face);normal.rotateAroundZ(pitch);normal.rotateAroundY(rotation);float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;
   int packed=((byte)(nx*127F)&255)|((byte)(ny*127F)&255)<<8|((byte)(nz*127F)&255)<<16;var vertices=new int[32];int[] corners={0,1,2,2};
   for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var vertex=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var p=LegacyVector.createVectorHelper(vertex.x(),vertex.y(),vertex.z());p.rotateAroundZ(pitch);p.rotateAroundY(rotation);int a=i*8;vertices[a]=Float.floatToRawIntBits((float)p.xCoord+.5F);vertices[a+1]=Float.floatToRawIntBits((float)p.yCoord+.5F);vertices[a+2]=Float.floatToRawIntBits((float)p.zCoord+.5F);vertices[a+3]=0xffffffff;vertices[a+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[a+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));vertices[a+6]=0;vertices[a+7]=packed;}
   result.add(new BakedQuad(vertices,-1,Direction.getNearest(nx,ny,nz),sprite,false));
  }return List.copyOf(result);
 }
 private static LegacyVector normal(WavefrontMesh mesh,WavefrontMesh.Face face){var a=mesh.positions().get(face.corners().get(0).position());var b=mesh.positions().get(face.corners().get(1).position());var c=mesh.positions().get(face.corners().get(2).position());double ax=b.x()-a.x(),ay=b.y()-a.y(),az=b.z()-a.z(),bx=c.x()-a.x(),by=c.y()-a.y(),bz=c.z()-a.z();var n=LegacyVector.createVectorHelper(ay*bz-az*by,az*bx-ax*bz,ax*by-ay*bx).normalize();return LegacyVector.createVectorHelper((float)n.xCoord,(float)n.yCoord,(float)n.zCoord);}
}
