package com.hbm.client.cyclotron;
import com.hbm.client.model.*;
import com.hbm.machine.cyclotron.CyclotronEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Actual HFR Body+B1..B4,filled material changes and original all-plug rotating galactic sentence. */
public final class CyclotronRenderer implements BlockEntityRenderer<CyclotronEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/cyclotron.obj");
 private static ResourceLocation texture(String suffix){return ResourceLocation.parse("hbm:textures/models/machines/cyclotron"+suffix+".png");}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 public CyclotronRenderer(BlockEntityRendererProvider.Context context){}
 private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String part,String texture,boolean world,boolean flat){var type=world?RenderType.entityCutoutNoCull(texture(texture)):RenderType.entityCutout(texture(texture));LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(type),light,overlay,-1,flat?LegacyObjRenderer.Convention.HFR_FLAT:LegacyObjRenderer.Convention.HFR,part);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){part(pose,buffers,light,overlay,"Body","",false,false);String[] plugs={"_ashes","_book","_gavel","_coin"};for(int i=0;i<4;i++)part(pose,buffers,light,overlay,"B"+(i+1),plugs[i],false,false);}
 @Override public void render(CyclotronEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);part(pose,buffers,light,overlay,"Body","",true,false);boolean filled=true;String[] names={"_ashes","_book","_gavel","_coin"};for(int i=0;i<4;i++){boolean inserted=machine.getPlug(i);filled&=inserted;part(pose,buffers,light,overlay,"B"+(i+1),names[i]+(inserted?"_filled":""),true,true);}if(filled){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)(System.currentTimeMillis()*.025%360)));pose.translate(0,2,0);pose.mulPose(Axis.XP.rotationDegrees(180));float rot=0;var mc=Minecraft.getInstance();MultiBufferSource glyphs=type->buffers.getBuffer(CyclotronGlyphType.of(type));for(char c:"plures necat crapula quam gladius".toCharArray()){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rot));rot-=mc.font.width(String.valueOf(c))*2F;pose.translate(2.75,0,0);pose.mulPose(Axis.YP.rotationDegrees(-90));pose.scale(.1F,.1F,.1F);var text=Component.literal(String.valueOf(c)).withStyle(Style.EMPTY.withFont(ResourceLocation.parse("minecraft:alt")));mc.font.drawInBatch(text,0,0,0xFF600060,false,pose.last().pose(),glyphs,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);pose.popPose();}pose.popPose();}pose.popPose();}
 @Override public boolean shouldRenderOffScreen(CyclotronEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(CyclotronEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+4,p.getZ()+3);}
}
