package com.hbm.client.reactor.rbmk.panels.indicator;
import com.hbm.client.reactor.rbmk.panels.RbmkPanelRender;
import com.hbm.machine.reactor.rbmk.panels.indicator.RbmkIndicatorEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
public final class RbmkIndicatorRenderer implements BlockEntityRenderer<RbmkIndicatorEntity>{
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/indicator.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/indicator.png");
    public RbmkIndicatorRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkIndicatorEntity panel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(panel.getBlockState().getValue(DummyableBlock.LEGACY_META)){case 2->90;case 4->180;case 3->270;default->0;}));for(int i=0;i<6;i++){var indicator=panel.indicators[i];if(!indicator.active)continue;poses.pushPose();poses.translate(.25,i/2*-.3125+.3125,i%2*-.5+.25);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,"Base",-1,false);float multiplier=indicator.light?1F:.35F;int color=0xff000000|(int)((indicator.color>>16&255)*multiplier)<<16|(int)((indicator.color>>8&255)*multiplier)<<8|(int)((indicator.color&255)*multiplier);RbmkPanelRender.mesh(poses,buffers,indicator.light?LightTexture.FULL_BRIGHT:light,overlay,MODEL,TEXTURE,"Light",color,false);if(indicator.label!=null&&!indicator.label.isEmpty()){var font=RbmkPanelRender.font();poses.translate(.0725,.5,0);int width=font.width(indicator.label);float scale=Math.min(.0125F,.3F/Math.max(width,1));poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(indicator.label,-width/2,-font.lineHeight/2,0,poses,buffers,light);}poses.popPose();}poses.popPose();}
}
