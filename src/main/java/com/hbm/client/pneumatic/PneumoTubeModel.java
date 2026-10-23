package com.hbm.client.pneumatic;
import com.hbm.pneumatic.tube.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
/** Source RenderPneumoTube straight/cross/terminal geometry, and both original paintable render passes. */
final class PneumoTubeModel extends BakedModelWrapper<BakedModel> {
 record State(int mask,int air,Direction input,Direction output,BlockState paint,boolean hidden){}private static final ModelProperty<State> STATE=new ModelProperty<>();private static final State DEFAULT=new State(0,0,null,null,null,false);
 private final boolean painted;private final Map<String,TextureAtlasSprite> sprites=new HashMap<>();private final Map<State,List<BakedQuad>> cache=new ConcurrentHashMap<>();private List<BakedQuad> inventory;
 PneumoTubeModel(BakedModel model,boolean painted,Function<Material,TextureAtlasSprite> textures){super(model);this.painted=painted;for(String texture:List.of("pneumatic_tube","pneumatic_tube_in","pneumatic_tube_out","pneumatic_tube_connector","pneumatic_tube_straight","pneumatic_tube_paintable","pneumatic_tube_paintable_overlay","pneumatic_tube_paintable_overlay_in","pneumatic_tube_paintable_overlay_out"))sprites.put(texture,textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,PneumaticTubeContent.id("block/"+texture))));}
 @Override public boolean isCustomRenderer(){return false;}@Override public boolean isGui3d(){return true;}@Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);if(painted)poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));return this;}
 @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState block,ModelData data){int mask=PneumoTubeBlock.mask(world,pos,false),air=0;for(var side:Direction.values())if(PneumoTubeBlock.canAir(world,pos,side))air|=1<<side.get3DDataValue();var tube=world.getBlockEntity(pos) instanceof PneumoTubeBlockEntity t?t:null;return data.derive().with(STATE,new State(mask,air,tube==null?null:tube.insertionDir,tube==null?null:tube.ejectionDir,tube==null?null:tube.paint,block.getValue(PneumoTubeBlock.HIDE_OVERLAY))).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}@Override public List<BakedQuad> getQuads(BlockState block,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();if(block==null){if(inventory==null)inventory=List.copyOf(build(null,random));return inventory;}var state=data.get(STATE);if(state==null)state=DEFAULT;return cache.computeIfAbsent(state,s->List.copyOf(build(s,random)));}
 private List<BakedQuad> build(State state,RandomSource random){var out=new ArrayList<BakedQuad>();if(painted){if(state!=null&&state.paint!=null){var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state.paint);var all=new ArrayList<>(model.getQuads(state.paint,null,random,ModelData.EMPTY,null));for(var side:Direction.values())all.addAll(model.getQuads(state.paint,side,random,ModelData.EMPTY,null));for(var quad:all)out.add(new BakedQuad(quad.getVertices(),-1,quad.getDirection(),quad.getSprite(),quad.isShade(),quad.hasAmbientOcclusion()));}else cube(out,"pneumatic_tube_paintable",0,new int[6],0,0,0,1,1,1);if(state!=null&&!state.hidden)for(var side:Direction.values()){String icon=state.output==side?"pneumatic_tube_paintable_overlay_in":state.input==side?"pneumatic_tube_paintable_overlay_out":"pneumatic_tube_paintable_overlay";float inset=.0001F;PneumaticQuads.face(out,sprites.get(icon),side,0,-inset,-inset,-inset,1+inset,1+inset,1+inset);}return out;}
  int[] r=new int[6];if(state==null){r[1]=2;r[0]=1;for(var side:Direction.values())PneumaticQuads.face(out,sprites.get(side.getAxis()==Direction.Axis.Z?"pneumatic_tube_connector":"pneumatic_tube_straight"),side,r[side.get3DDataValue()],.3125F,.3125F,0,.6875F,.6875F,1);return out;}
  int mask=state.mask;boolean connections=state.input!=null||state.output!=null;String active="pneumatic_tube";if(mask==48&&!connections){active="pneumatic_tube_straight";cube(out,active,48,r,0,.3125F,.3125F,1,.6875F,.6875F);}else if(mask==12&&!connections){active="pneumatic_tube_straight";r[1]=2;r[0]=1;cube(out,active,12,r,.3125F,.3125F,0,.6875F,.6875F,1);Arrays.fill(r,0);}else if(mask==3&&!connections){active="pneumatic_tube_straight";r[2]=r[3]=r[4]=r[5]=2;cube(out,active,3,r,.3125F,0,.3125F,.6875F,1,.6875F);Arrays.fill(r,0);}else{cube(out,active,mask,r,.3125F,.3125F,.3125F,.6875F,.6875F,.6875F);for(var side:Direction.values())if((mask&(1<<side.get3DDataValue()))!=0)arm(out,active,side,r,.3125F,.6875F,0,1);}
  if(state.input!=null){connector(out,active,state.input,"pneumatic_tube_in");active="pneumatic_tube";}if(state.output!=null){connector(out,active,state.output,"pneumatic_tube_out");active="pneumatic_tube";}for(var side:Direction.values())if((state.air&(1<<side.get3DDataValue()))!=0){connector(out,active,side,"pneumatic_tube_connector");active="pneumatic_tube";}return out;
 }
 private void cube(List<BakedQuad> out,String sprite,int omit,int[] r,float x0,float y0,float z0,float x1,float y1,float z1){PneumaticQuads.box(out,sprites.get(sprite),omit,r,x0,y0,z0,x1,y1,z1);}
 private void arm(List<BakedQuad> out,String sprite,Direction side,int[] r,float lo,float hi,float start,float end){float[] a={lo,lo,lo,hi,hi,hi};int axis=side.getAxis()==Direction.Axis.X?0:side.getAxis()==Direction.Axis.Y?1:2;if(side.getAxisDirection()==Direction.AxisDirection.POSITIVE){a[axis]=hi;a[axis+3]=end;}else{a[axis]=start;a[axis+3]=lo;}cube(out,sprite,(1<<side.get3DDataValue())|(1<<side.getOpposite().get3DDataValue()),r,a[0],a[1],a[2],a[3],a[4],a[5]);}
 private void connector(List<BakedQuad> out,String active,Direction side,String connector){arm(out,active,side,new int[6],.3125F,.6875F,.25F,.75F);float[] a={.25F,.25F,.25F,.75F,.75F,.75F};int axis=side.getAxis()==Direction.Axis.X?0:side.getAxis()==Direction.Axis.Y?1:2;if(side.getAxisDirection()==Direction.AxisDirection.POSITIVE){a[axis]=.75F;a[axis+3]=1;}else{a[axis]=0;a[axis+3]=.25F;}cube(out,connector,0,new int[6],a[0],a[1],a[2],a[3],a[4],a[5]);}
}
