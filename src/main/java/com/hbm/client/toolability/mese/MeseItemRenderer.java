package com.hbm.client.toolability.mese;
import com.hbm.client.meteoritesword.MeteoriteSwordRenderer;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.structureloot.LootPileRenderer;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Actual ItemRenderTransformer(rtp,ttp_high,stp,rfp,zero,one,zero,zero,one), not the vanilla pickaxe display transform. */
public final class MeseItemRenderer extends BlockEntityWithoutLevelRenderer {
 private static final ResourceLocation ICON=ResourceLocation.parse("hbm:textures/item/mese_pickaxe.png");
 private static final ModelResourceLocation ORDINARY=ModelResourceLocation.standalone(ResourceLocation.parse("hbm:item/mese_pickaxe_ordinary"));
 // Original glint: .5/.25/.8 * .76, the same two source glint render passes already used by the actual meteorite sword.
 private static final int GLINT=0xff60309b;
 private final ResourceLocation icon;private final ModelResourceLocation ordinary;
 private final double thirdX,thirdY;private final float sourceFirstX,sourceFirstY,sourceFirstZ;private final int pixelsX,pixelsY;
 public MeseItemRenderer(){this(ICON,ORDINARY);}
 public MeseItemRenderer(ResourceLocation icon,ModelResourceLocation ordinary){this(icon,ordinary,.125,.625,1,1,1,16,16);}
 /** Actual ClientProxy high/low ttp and sfp arrays; old constructors retain their original16px/one-scale behavior. */
 public MeseItemRenderer(ResourceLocation icon,ModelResourceLocation ordinary,double thirdX,double thirdY,float firstX,float firstY,float firstZ,int pixelsX,int pixelsY){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());this.icon=icon;this.ordinary=ordinary;this.thirdX=thirdX;this.thirdY=thirdY;sourceFirstX=firstX;sourceFirstY=firstY;sourceFirstZ=firstZ;this.pixelsX=pixelsX;this.pixelsY=pixelsY;}
 @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  var client=Minecraft.getInstance();
  if(context==ItemDisplayContext.GROUND||context==ItemDisplayContext.FIXED||context==ItemDisplayContext.HEAD||context==ItemDisplayContext.NONE){client.getItemRenderer().render(stack,context,false,poses,buffers,light,overlay,client.getModelManager().getModel(ordinary));return;}
  poses.pushPose();try(var hand=GunHandTransforms.heldFull3D(poses,context)){
   if(context==ItemDisplayContext.GUI){poses.translate(0,1,.3125);poses.scale(1F/16,-1F/16,1F/16);MeteoriteSwordRenderer.sourceFlatIcon(poses,buffers,light,overlay,icon,GLINT,stack.hasFoil());return;}
   if(context.firstPerson()){
    LegacyItemTransforms.heldFull3D(poses,context);
    poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-90));
    poses.translate(.5,.5,0);poses.translate(-.5,-.5,0);poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-90));
    poses.translate(.5,.5,0);poses.scale(sourceFirstX*2,sourceFirstY*2,sourceFirstZ*2);poses.translate(-.5,-.5,.25);
   }else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
    poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-90));poses.translate(thirdX,thirdY,0);
    poses.mulPose(Axis.ZP.rotationDegrees(45));poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.ZP.rotationDegrees(-45));poses.scale(1.7F,1.7F,.85F);
   }else{poses.translate(.5,.5,.5);}
   var type=RenderType.entityCutoutNoCull(icon);
   // Exact source icon dimensions control the1/16thick ItemRenderer.renderItemIn2D edge strips.
   LootPileRenderer.extrude(1,0,0,1,pixelsX,pixelsY,.0625F,poses.last(),buffers.getBuffer(type),light,overlay,-1);
   if(buffers instanceof MultiBufferSource.BufferSource batch)batch.endBatch(type);
   if(stack.hasFoil())MeteoriteSwordRenderer.sourceSolidGlint(poses,buffers,overlay,GLINT);
  }finally{poses.popPose();}
 }
}
