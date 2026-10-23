package com.hbm.client.structure.lights;
import com.hbm.block.light.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.TriState;
/** Original RenderLight/ObjUtil geometry, modular cap/mid selection and source rotation order. */
final class SourceLightModel extends BakedModelWrapper<BakedModel>{
 private record Join(int count,Direction direction){}private record Key(int metadata,Join join){}
 private static final ModelProperty<Join> JOIN=new ModelProperty<>();private static final Join NONE=new Join(0,null);
 private final SpotlightKind kind;private final boolean inventory;private final TextureAtlasSprite texture;private final Map<Key,List<BakedQuad>> quads=new ConcurrentHashMap<>();
 SourceLightModel(BakedModel original,SpotlightKind kind,boolean on,boolean inventory,Function<Material,TextureAtlasSprite> sprites){super(original);this.kind=kind;this.inventory=inventory;texture=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,SourceLightContent.id("block/"+kind.texture+(on?"":"_off"))));}
 @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType layer){return TriState.FALSE;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){int hand=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND?-1:1;if(context.firstPerson()){poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(-hand/16D,-.125,.625);poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180));poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);}else getTransforms().getTransform(context).apply(left,poses);return this;}@Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){Join result=NONE;if(kind==SpotlightKind.FLUORESCENT){var axis=SpotlightBlock.direction(state.getValue(SpotlightBlock.META));if(axis!=null)for(var available:Direction.values()){if(available==axis||available==axis.getOpposite())continue;if(world.getBlockState(pos.relative(available)).is(state.getBlock())){result=new Join(world.getBlockState(pos.relative(available.getOpposite())).is(state.getBlock())?2:1,available);break;}}}return data.derive().with(JOIN,result).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();int meta=inventory||state==null?-1:state.getValue(SpotlightBlock.META);var join=data.get(JOIN);return quads.computeIfAbsent(new Key(meta,join==null?NONE:join),this::bake);}
 private List<BakedQuad> bake(Key key){boolean item=key.metadata<0;var axis=item?null:SpotlightBlock.direction(key.metadata);var join=key.join;float yaw=item?(float)(-Math.PI/2):yaw(axis),pitch=item?0:pitch(axis),roll=item||join.direction==null?0:roll(join.direction,axis);float ox=item?.5F:.5F-(axis==null?0:axis.getStepX())*.5F,oy=item?.5F:.5F-(axis==null?0:axis.getStepY())*.5F,oz=item?.5F:.5F-(axis==null?0:axis.getStepZ())*.5F;float scale=item?1.5F:1;
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SourceLightContent.id("models/lights/"+kind.texture+".obj"));var result=new ArrayList<BakedQuad>();String part=kind.part(item?0:join.count);
  for(var triangle:mesh.triangles()){var face=mesh.faces().get(triangle.face());if(!face.legacyPart().equals(part))continue;var n=face.normal();var normal=rotate(n.x(),n.y(),n.z(),0,pitch,yaw);int packed=((byte)(normal[0]*127)&255)|((byte)(normal[1]*127)&255)<<8|((byte)(normal[2]*127)&255)<<16;int[] vertices=new int[32],corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
   for(int i=0;i<4;i++){var corner=face.corners().get(corners[i]);var p=mesh.positions().get(corner.position());var position=rotate(p.x(),p.y(),p.z(),roll,pitch,yaw);var uv=mesh.textureCoordinates().get(corner.texture());int k=i*8;vertices[k]=Float.floatToRawIntBits((float)position[0]*scale+ox);vertices[k+1]=Float.floatToRawIntBits((float)position[1]*scale+oy);vertices[k+2]=Float.floatToRawIntBits((float)position[2]*scale+oz);vertices[k+3]=-1;vertices[k+4]=Float.floatToRawIntBits(texture.getU(uv.u()));vertices[k+5]=Float.floatToRawIntBits(texture.getV(1-uv.v()));vertices[k+6]=0;vertices[k+7]=packed;}
   result.add(new BakedQuad(vertices,-1,Direction.getNearest((float)normal[0],(float)normal[1],(float)normal[2]),texture,false));
  }return List.copyOf(result);
 }
 /** Original mutable1.7Vec3 uses float sine-table factors, and notably rotates normals without roll. */
 private static double[] rotate(double x,double y,double z,float roll,float pitch,float yaw){float c=Mth.cos(roll),s=Mth.sin(roll);double ry=y*c+z*s,rz=z*c-y*s;y=ry;z=rz;c=Mth.cos(pitch);s=Mth.sin(pitch);double rx=x*c+y*s;ry=y*c-x*s;x=rx;y=ry;c=Mth.cos(yaw);s=Mth.sin(yaw);return new double[]{x*c+z*s,y,z*c-x*s};}
 private static float pitch(Direction dir){return dir==Direction.UP?(float)Math.PI*-.5F:dir==Direction.DOWN?(float)Math.PI*.5F:0;}
 private static float yaw(Direction dir){return dir==Direction.NORTH?(float)Math.PI*.5F:dir==Direction.SOUTH?(float)Math.PI*-.5F:dir==Direction.WEST?(float)Math.PI:0;}
 private static float roll(Direction dir,Direction axis){float flip=axis==Direction.DOWN||axis==Direction.NORTH||axis==Direction.WEST?-.5F:.5F,add=axis==Direction.NORTH||axis==Direction.SOUTH?-.5F:0;boolean flipNS=axis==Direction.WEST;return switch(dir){case NORTH->flipNS?(float)Math.PI:0;case SOUTH->!flipNS?(float)Math.PI:0;case EAST->(float)Math.PI*(flip+add);case WEST->(float)Math.PI*(-flip+add);case UP->(float)Math.PI*-.5F;case DOWN->(float)Math.PI*.5F;};}
}
