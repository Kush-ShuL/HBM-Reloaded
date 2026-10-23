package com.hbm.client.bobmazondeps;

import com.hbm.client.model.*;
import com.hbm.item.bobmazondeps.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Unchanged source parts/order/text label, no added glass blending or see-through text. */
public final class SnowglobeRenderer implements BlockEntityRenderer<SnowglobeEntity> {
 private static final ResourceLocation MODEL=id("models/trinkets/snowglobe.obj");
 public SnowglobeRenderer(BlockEntityRendererProvider.Context context){}
 private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 @Override public void render(SnowglobeEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(-22.5F*tile.getBlockState().getValue(TrinketBlock.META)-90));draw(tile.type,poses,buffers,light,overlay);poses.popPose();}
 public static void draw(TrinketTypes.Snowglobe type,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.scale(.0625F,.0625F,.0625F);part(poses,buffers,light,overlay,"snowglobe","Socket");part(poses,buffers,light,overlay,"snowglobe_glass","Glass");String feature=switch(type){case NONE->null;case RIVETCITY->"RivetCity";case TENPENNYTOWER->"TenpennyTower";case LUCKY38->"Lucky38";case SIERRAMADRE->"SierraMadre";case PRYDWEN->"Prydwen";};if(feature!=null)part(poses,buffers,light,overlay,"snowglobe_features",feature);
  var font=Minecraft.getInstance().font;poses.translate(4.025,.5,0);poses.scale(.05F,-.05F,.05F);poses.translate(0,-font.lineHeight/2F,font.width(type.label)*.5D);poses.mulPose(Axis.YP.rotationDegrees(90));poses.translate(0,1,0);
  var used=new LinkedHashSet<RenderType>();MultiBufferSource labels=renderType->{var wrapped=LABELS.computeIfAbsent(renderType,Label::new);used.add(wrapped);return buffers.getBuffer(wrapped);};font.drawInBatch(type.label,0,0,0xffffffff,false,poses.last().pose(),labels,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);if(buffers instanceof MultiBufferSource.BufferSource source)for(var renderType:used)source.endBatch(renderType);
 }
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String texture,String part){var renderType=RenderType.entityCutoutNoCull(id("textures/models/trinkets/"+texture+".png"));LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(net.minecraft.client.Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(renderType),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(renderType);}
 private static final Map<RenderType,RenderType> LABELS=new IdentityHashMap<>();
 private static final class Label extends RenderType {
  Label(RenderType original){super("hbm_snowglobe_label",original.format(),original.mode(),original.bufferSize(),false,false,()->{original.setupRenderState();RenderSystem.depthMask(false);},()->{RenderSystem.depthMask(true);original.clearRenderState();});}
 }
}
