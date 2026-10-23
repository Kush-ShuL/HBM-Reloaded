package com.hbm.client.rebar;
import com.hbm.block.rebar.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
/** Original opaque concrete_liquid cuboid0..progress/1000, only actual wet blocks count against the source250 limit. */
public final class RebarWetRenderer implements BlockEntityRenderer<RebarBlockEntity> {
 static int drawn;public RebarWetRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(RebarBlockEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){if(machine.progress<=0||drawn>=RebarConfig.limit())return;drawn++;float h=machine.progress/1000F;var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.parse("hbm:block/concrete_liquid"));var consumer=buffers.getBuffer(RenderType.solid());float[][][] faces={{{0,0,1},{0,0,0},{1,0,0},{1,0,1}},{{0,h,0},{0,h,1},{1,h,1},{1,h,0}},{{1,0,0},{0,0,0},{0,h,0},{1,h,0}},{{0,0,1},{1,0,1},{1,h,1},{0,h,1}},{{0,0,0},{0,0,1},{0,h,1},{0,h,0}},{{1,0,1},{1,0,0},{1,h,0},{1,h,1}}};float[][] normals={{0,-1,0},{0,1,0},{0,0,-1},{0,0,1},{-1,0,0},{1,0,0}};for(int face=0;face<6;face++){var normal=normals[face];float shade=face==0?.5F:face==1?1:face<4?.8F:.6F;for(var vertex:faces[face]){float u=face<2?vertex[0]:face==2?1-vertex[0]:face==3?vertex[0]:face==4?vertex[2]:1-vertex[2],v=face==0?1-vertex[2]:face==1?vertex[2]:1-vertex[1];consumer.addVertex(pose.last(),vertex[0],vertex[1],vertex[2]).setColor(shade,shade,shade,1).setUv(sprite.getU(u),sprite.getV(v)).setOverlay(overlay).setLight(light).setNormal(pose.last(),normal[0],normal[1],normal[2]);}}}
}
