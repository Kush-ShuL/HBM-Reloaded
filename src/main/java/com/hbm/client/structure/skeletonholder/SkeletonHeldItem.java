package com.hbm.client.structure.skeletonholder;
import com.hbm.block.skeletonholder.SkeletonHolderEntity;
import com.hbm.client.structureloot.LootPileRenderer;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.GunVisualState;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
/** Frozen dummy age0/hover0. Source187seed stack copies and true item-frame sprite matrices, without a live dummy entity. */
final class SkeletonHeldItem {
 private record Layer(TextureAtlasSprite sprite,int tint){}
 static void render(ItemStack stack,BakedModel model,boolean block,SkeletonHolderEntity holder,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  var mc=Minecraft.getInstance();BakedModel ground=model.applyTransform(ItemDisplayContext.GROUND,new PoseStack(),false);
  if(ground.isCustomRenderer()){
   var renderer=IClientItemExtensions.of(stack).getCustomRenderer();
   if(renderer instanceof LegacyGunRenderer gun){poses.pushPose();poses.scale(.5F,.5F,.5F);try(var visual=GunVisualState.bind(stack);var scope=LegacyGunGL.push(poses,buffers,light,overlay)){gun.setupEntity(stack);gun.renderEntity(stack);}catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}finally{poses.popPose();}}
   else mc.getItemRenderer().renderStatic(stack,ItemDisplayContext.GROUND,light,overlay,poses,buffers,holder.getLevel(),0);
   return;
  }
  // Vanilla RenderItem always adds this dummy's .1 bob before the frame-specific branch.
  poses.translate(0,.1F,0);
  if(block){int count=stack.getCount()>40?5:stack.getCount()>20?4:stack.getCount()>5?3:stack.getCount()>1?2:1;var random=new Random(187);poses.scale(1.25F,1.25F,1.25F);poses.translate(0,.05F,0);poses.mulPose(Axis.YP.rotationDegrees(-90));poses.scale(.25F,.25F,.25F);for(int i=0;i<count;i++){poses.pushPose();if(i>0)poses.translate((random.nextFloat()*2-1)*.2F/.25F,(random.nextFloat()*2-1)*.2F/.25F,(random.nextFloat()*2-1)*.2F/.25F);mc.getItemRenderer().renderStatic(stack,ItemDisplayContext.NONE,light,overlay,poses,buffers,holder.getLevel(),0);poses.popPose();}return;}
  // Source fancy/fast item rendering uses the item's original layered sprites and native tint/glint.
  poses.scale(.5128205F,.5128205F,.5128205F);poses.translate(0,-.05F,0);var layers=new ArrayList<Layer>();for(var quad:model.getQuads(null,null,RandomSource.create(0))){var layer=new Layer(quad.getSprite(),quad.isTinted()?quad.getTintIndex():-1);if(!layers.contains(layer))layers.add(layer);}if(layers.isEmpty())layers.add(new Layer(model.getParticleIcon(),-1));layers.sort(Comparator.comparingInt(Layer::tint));boolean fancy=mc.options.graphicsMode().get()!=GraphicsStatus.FAST;
  for(var layer:layers){poses.pushPose();int argb=layer.tint<0?-1:0xff000000|mc.getItemColors().getColor(stack,layer.tint)&0xffffff;var sprite=layer.sprite;var out=ItemRenderer.getFoilBufferDirect(buffers,RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS),true,stack.hasFoil());if(fancy){int count=stack.getCount()<2?1:stack.getCount()<16?2:stack.getCount()<32?3:4;poses.mulPose(Axis.YP.rotationDegrees(180));poses.translate(-.5F,-.25F,-(.0625F+.021875F)*count/2);for(int i=0;i<count;i++){poses.translate(0,0,.0625F+.021875F);LootPileRenderer.extrude(sprite.getU1(),sprite.getU0(),sprite.getV0(),sprite.getV1(),sprite.contents().width(),sprite.contents().height(),.0625F,poses.last(),out,light,overlay,argb);}}
   else{int count=stack.getCount()>40?5:stack.getCount()>20?4:stack.getCount()>5?3:stack.getCount()>1?2:1;var random=new Random(187);for(int i=0;i<count;i++){poses.pushPose();if(i>0)poses.translate((random.nextFloat()*2-1)*.3F,(random.nextFloat()*2-1)*.3F,(random.nextFloat()*2-1)*.3F);quad(out,poses,sprite,argb,light,overlay);poses.popPose();}}poses.popPose();
  }
 }
 private static void quad(VertexConsumer out,PoseStack poses,TextureAtlasSprite sprite,int color,int light,int overlay){float[][] v={{-.5F,-.25F,0,sprite.getU0(),sprite.getV1()},{.5F,-.25F,0,sprite.getU1(),sprite.getV1()},{.5F,.75F,0,sprite.getU1(),sprite.getV0()},{-.5F,.75F,0,sprite.getU0(),sprite.getV0()}};for(var p:v)out.addVertex(poses.last(),p[0],p[1],p[2]).setColor(color).setUv(p[3],p[4]).setOverlay(overlay).setLight(light).setNormal(poses.last(),0,1,0);}
 private SkeletonHeldItem(){}
}
