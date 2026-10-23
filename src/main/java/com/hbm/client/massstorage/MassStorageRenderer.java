package com.hbm.client.massstorage;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.massstorage.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

/** Source RenderMassStorage front-face matrix,8px item, quarter-scale green counter and12px fill bar. */
public final class MassStorageRenderer implements BlockEntityRenderer<MassStorageBlockEntity> {
    public MassStorageRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(MassStorageBlockEntity storage,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var type=storage.displayType;if(type.isEmpty())return;var client=Minecraft.getInstance();
        pose.pushPose();pose.translate(.5,.5,.5);pose.mulPose(Axis.ZP.rotationDegrees(180));
        int direction=storage.getBlockState().getValue(MassStorageBlock.META)/4;
        switch(direction){case 1->pose.mulPose(Axis.YP.rotationDegrees(180));case 2->pose.mulPose(Axis.YP.rotationDegrees(-90));case 3->pose.mulPose(Axis.YP.rotationDegrees(90));default->{}}
        pose.translate(-.5,-.5,-.5);pose.translate(0,0,-.005);pose.scale(1F/16,1F/16,-.0001F);
        pose.pushPose();pose.translate(4,2.5,0);pose.scale(.5F,.5F,1);pose.translate(8,8,0);pose.scale(16,-16,16);
        client.getItemRenderer().renderStatic(type,ItemDisplayContext.GUI,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,pose,buffers,storage.getLevel(),0);pose.popPose();
        var font=LegacyFonts.get();String text=count(storage.getStockpile(),client.options.forceUnicodeFont().get());int textX=32-font.width(text)/2;
        pose.pushPose();pose.scale(.25F,.25F,.25F);font.drawInBatch(text,textX+1,45,0x003f00,false,pose.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);pose.translate(0,0,1);font.drawInBatch(text,textX,44,0x00ff00,false,pose.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);pose.popPose();
        double fraction=(double)storage.getStockpile()/storage.getCapacity();int red=(int)((1-fraction)*255),green=(int)(fraction*255);float maxX=(float)(2+fraction*12);var out=buffers.getBuffer(Layers.BAR);var matrix=pose.last();
        out.addVertex(matrix,2,14,0).setColor(red,green,0,255);
        out.addVertex(matrix,maxX,14,0).setColor(red,green,0,255);
        out.addVertex(matrix,maxX,13.5F,0).setColor(red,green,0,255);
        out.addVertex(matrix,2,13.5F,0).setColor(red,green,0,255);pose.popPose();
    }
    private static String count(int count,boolean unicode){if(count>=100_000_000||count>=1_000_000&&unicode)return String.format("%.0fM",count/1_000_000F);if(count>=1_000_000)return String.format("%.1fM",count/1_000_000F);if(count>=100_000||count>=10_000&&unicode)return String.format("%.0fK",count/1000F);if(count>=10_000)return String.format("%.1fK",count/1000F);return Integer.toString(count);}
    @Override public int getViewDistance(){return 32;}
    private static final class Layers extends RenderType {
        private Layers(){super("hbm_mass_storage_unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
        private static final RenderType BAR=create("hbm_mass_storage_bar",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    }
}
