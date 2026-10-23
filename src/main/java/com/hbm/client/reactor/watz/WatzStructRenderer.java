package com.hbm.client.reactor.watz;

import com.hbm.machine.reactor.watz.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Actual SmallBlockPronter geometry/UV: 5/16side tiny cubes at every original requiredcomponent position. */
public final class WatzStructRenderer implements BlockEntityRenderer<WatzStructEntity> {
    public WatzStructRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(WatzStructEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var out=buffers.getBuffer(WatzPreviewType.PREVIEW);for(var part:WatzLayout.COMPONENTS)draw(out,pose,part,light,overlay);}
    private static void draw(VertexConsumer out,PoseStack pose,WatzLayout.Part part,int light,int overlay){float a=11F/32,b=1-a;float[][][] faces={{{b,b,b},{a,b,b},{a,a,b},{b,a,b}},{{b,b,a},{b,b,b},{b,a,b},{b,a,a}},{{a,b,a},{b,b,a},{b,a,a},{a,a,a}},{{a,b,b},{a,b,a},{a,a,a},{a,a,b}},{{b,b,a},{a,b,a},{a,b,b},{b,b,b}},{{a,a,a},{b,a,a},{b,a,b},{a,a,b}}};for(int f=0;f<6;f++){String name=part.kind()==2?"watz_casing_bolted":(part.kind()==0?"watz_element_":"watz_cooler_")+(f>=4?"top":"side");var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.parse("hbm:block/"+name));for(int i=0;i<4;i++){var p=faces[f][i];out.addVertex(pose.last(),part.x()+p[0],part.y()+p[1],part.z()+p[2]).setColor(255,255,255,191).setUv(i==0||i==3?sprite.getU1():sprite.getU0(),i<2?sprite.getV0():sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(pose.last(),0,1,0);}}}
    @Override public boolean shouldRenderOffScreen(WatzStructEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(WatzStructEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+3,pos.getZ()+4);}
}
