package com.hbm.client.reactor.icf;
import com.hbm.machine.reactor.icf.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** The source407 SmallBlockPronter cubes,11/32inset,alpha.75,constant up normal and no depthwrites. */
public final class IcfStructRenderer implements BlockEntityRenderer<IcfStructEntity>{
 private static final class Preview extends RenderType{private Preview(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}static final RenderType TYPE=create("hbm_icf_structure_preview",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,32768,false,true,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setCullState(CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));}
 public IcfStructRenderer(BlockEntityRendererProvider.Context context){}@Override public void render(IcfStructEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(tile.getBlockState().getValue(IcfStructBlock.META)){case 2->270;case 3->90;case 5->180;default->0;}));poses.translate(-.5,0,-.5);var out=buffers.getBuffer(Preview.TYPE);for(var cell:IcfStructureProfile.CELLS)draw(out,poses,cell,light,overlay);poses.popPose();}
 private static void draw(VertexConsumer out,PoseStack pose,IcfStructureProfile.Cell cell,int light,int overlay){float a=11F/32,b=1-a;float[][][] faces={{{b,b,b},{a,b,b},{a,a,b},{b,a,b}},{{b,b,a},{b,b,b},{b,a,b},{b,a,a}},{{a,b,a},{b,b,a},{b,a,a},{a,a,a}},{{a,b,b},{a,b,a},{a,a,a},{a,a,b}},{{b,b,a},{a,b,a},{a,b,b},{b,b,b}},{{a,a,a},{b,a,a},{b,a,b},{a,a,b}}};String name="icf_component"+IcfMaterialItem.COMPONENT[cell.material()];var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.parse("hbm:block/"+name));for(var face:faces)for(int i=0;i<4;i++){var p=face[i];out.addVertex(pose.last(),cell.width()+p[0],cell.height()+p[1],cell.length()+p[2]).setColor(255,255,255,191).setUv(i==0||i==3?sprite.getU1():sprite.getU0(),i<2?sprite.getV0():sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(pose.last(),0,1,0);}}
 @Override public boolean shouldRenderOffScreen(IcfStructEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(IcfStructEntity tile){var p=tile.getBlockPos();return new AABB(p.getX()-8,p.getY(),p.getZ()-8,p.getX()+9,p.getY()+6,p.getZ()+9);}
}
