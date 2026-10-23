package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
import com.hbm.machine.foundry.FoundryContent.Kind;
import com.hbm.machine.anvil.MoldItem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Source RenderFoundry plus exact five source block renderers; geometry changes only with real state. */
public final class FoundryRenderer implements BlockEntityRenderer<FoundryBlockEntity>{
 private static final ResourceLocation LIQUID=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/lava_gray.png");
 private final Map<FoundryBlockEntity,Cached> cache=new WeakHashMap<>();
 private record Cached(int state,int light,int neighborHash,List<FoundryFaces.Face> faces){}
 public FoundryRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(FoundryBlockEntity foundry,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  if(foundry.kind()==Kind.SLAG){slag(foundry,poses,buffers,light,overlay);return;}
  int neighbors=1;for(Direction side:Direction.values())neighbors=31*neighbors+foundry.getLevel().getBlockState(foundry.getBlockPos().relative(side)).hashCode();
  int geometryState=java.util.Objects.hash(foundry.type,foundry.amount,foundry.filter,foundry.kind().outlet()&&foundry.closed());
  Cached previous=cache.get(foundry);if(previous==null||previous.state!=geometryState||previous.light!=light||previous.neighborHash!=neighbors){previous=new Cached(geometryState,light,neighbors,FoundryGeometry.build(foundry.kind(),foundry.getLevel(),foundry.getBlockPos(),light,false));cache.put(foundry,previous);}
  var atlas=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);var out=buffers.getBuffer(FoundryRenderTypes.WORLD);
  for(var face:previous.faces){var sprite=atlas.apply(ResourceLocation.fromNamespaceAndPath("hbm","block/"+face.texture()));for(var point:face.vertices())out.addVertex(poses.last().pose(),point.x(),point.y(),point.z()).setColor(face.red(),face.green(),face.blue(),1).setUv(sprite.getU(point.u()),sprite.getV(point.v())).setLight(face.light());}
  if(foundry.kind().cast()){
   if(!foundry.slots.getFirst().isEmpty())drawItem(foundry.slots.getFirst(),.13,foundry,poses,buffers,light,overlay);
   ItemStack result=foundry.slots.get(1);if(!result.isEmpty()){if(result.getItem() instanceof BlockItem)blockOutput(result,foundry.kind()==Kind.MOLD?.25F:.875F,poses,buffers,light,overlay);else drawItem(result,foundry.kind()==Kind.MOLD?.25:.875,foundry,poses,buffers,light,overlay);}
   if(foundry.type!=null&&foundry.amount>0&&foundry.capacity()>0){float y=(float)(.125+foundry.amount*(foundry.kind()==Kind.MOLD?.25:.75)/foundry.capacity());int rgb=foundry.type.moltenColor();surface(poses,buffers.getBuffer(FoundryRenderTypes.base(LIQUID)),y,(rgb>>16&255)/255F,(rgb>>8&255)/255F,(rgb&255)/255F,1);surface(poses,buffers.getBuffer(FoundryRenderTypes.glow(LIQUID)),y,1,1,1,.3F);}
  }
 }
 private static void drawItem(ItemStack stack,double height,FoundryBlockEntity foundry,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  if(stack.getItem() instanceof MoldItem){drawMold(stack,height,foundry,poses,buffers,light);return;}
  // Old GUI icon draws 16x16 pixels after scale (.0625*12/16); convert its centered modern GUI unit back to the same .75 block square.
  poses.pushPose();poses.translate(.5,height,.5);poses.mulPose(Axis.XP.rotationDegrees(90));poses.scale(.75F,-.75F,-.75F);Minecraft.getInstance().getItemRenderer().renderStatic(stack,ItemDisplayContext.GUI,light,overlay,poses,buffers,foundry.getLevel(),0);poses.popPose();
 }
 private static void drawMold(ItemStack stack,double height,FoundryBlockEntity foundry,PoseStack poses,MultiBufferSource buffers,int light){
  // RenderFoundry used RenderItem.renderItemIntoGUI: a zero-depth 16px icon, not an extruded item.
  // T(.125,height,.125) * S(.0625*12/16) * Rx(90) maps its four corners to this upward plane.
  // The modern SOUTH face would be height-.75/32=.1065625, below the source tray floor at .125.
  // Resolve the real MoldBakedModel so stable IDs, configured custom molds and resource packs keep their artwork.
  var sprite=Minecraft.getInstance().getItemRenderer().getModel(stack,foundry.getLevel(),null,0).getParticleIcon();
  var out=buffers.getBuffer(FoundryRenderTypes.MOLD_ICON);var matrix=poses.last().pose();float y=(float)height;
  out.addVertex(matrix,.125F,y,.875F).setUv(sprite.getU0(),sprite.getV1()).setColor(1F,1F,1F,1F).setLight(light);
  out.addVertex(matrix,.875F,y,.875F).setUv(sprite.getU1(),sprite.getV1()).setColor(1F,1F,1F,1F).setLight(light);
  out.addVertex(matrix,.875F,y,.125F).setUv(sprite.getU1(),sprite.getV0()).setColor(1F,1F,1F,1F).setLight(light);
  out.addVertex(matrix,.125F,y,.125F).setUv(sprite.getU0(),sprite.getV0()).setColor(1F,1F,1F,1F).setLight(light);
 }
 private static void surface(PoseStack poses,VertexConsumer out,float y,float r,float g,float b,float alpha){float[][] corners={{.125F,.125F,.125F,.875F},{.125F,.875F,.875F,.875F},{.875F,.875F,.875F,.125F},{.875F,.125F,.125F,.125F}};for(var point:corners)out.addVertex(poses.last().pose(),point[0],y,point[1]).setUv(point[2],point[3]).setColor(r,g,b,alpha).setLight(0xF000F0);}
 private static void blockOutput(ItemStack stack,float y,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var item=(BlockItem)stack.getItem();var state=item.getBlock().defaultBlockState();var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);var random=net.minecraft.util.RandomSource.create(0);var quads=model.getQuads(state,Direction.UP,random);TextureAtlasSprite sprite=quads.isEmpty()?model.getParticleIcon():quads.getFirst().getSprite();var out=buffers.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));float[][] points={{.125F,.125F,0,1},{.125F,.875F,1,1},{.875F,.875F,1,0},{.875F,.125F,0,0}};for(var point:points)out.addVertex(poses.last().pose(),point[0],y,point[1]).setUv(sprite.getU(point[2]),sprite.getV(point[3])).setColor(1F,1F,1F,.3F).setLight(light).setOverlay(overlay).setNormal(poses.last(),0,1,0);}
 private static void slag(FoundryBlockEntity slag,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(FoundrySprites.slag(slag.type));float height=(float)slag.amount/slag.capacity();var faceBuilder=new FoundryFaces(light);faceBuilder.setRenderBounds(0,0,0,1,height,1);faceBuilder.renderFaceYNeg(null,0,0,0,"slag");faceBuilder.renderFaceYPos(null,0,0,0,"slag");faceBuilder.renderFaceXNeg(null,0,0,0,"slag");faceBuilder.renderFaceXPos(null,0,0,0,"slag");faceBuilder.renderFaceZNeg(null,0,0,0,"slag");faceBuilder.renderFaceZPos(null,0,0,0,"slag");int color=slag.type==null||slag.type.solidColorLight()!=slag.type.solidColorDark()?0xFFFFFF:slag.type.moltenColor();var out=buffers.getBuffer(FoundryRenderTypes.WORLD);for(var face:faceBuilder.faces){float shade=face.ny()<0?.5F:face.ny()>0?1:face.nx()!=0?.6F:.8F;for(var point:face.vertices())out.addVertex(poses.last().pose(),point.x(),point.y(),point.z()).setColor((color>>16&255)/255F*shade,(color>>8&255)/255F*shade,(color&255)/255F*shade,1).setUv(sprite.getU(point.u()),sprite.getV(point.v())).setLight(light);}}
}
