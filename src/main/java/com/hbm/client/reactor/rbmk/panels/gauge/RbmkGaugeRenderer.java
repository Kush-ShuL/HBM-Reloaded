package com.hbm.client.reactor.rbmk.panels.gauge;
import com.hbm.client.reactor.rbmk.panels.RbmkPanelRender;
import com.hbm.machine.reactor.rbmk.panels.gauge.RbmkGaugeEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Original Gauge/Needle mesh, exact mirrored-bound range/80degree clamp and authored pivot/text transforms. */
public final class RbmkGaugeRenderer implements BlockEntityRenderer<RbmkGaugeEntity>{
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/gauge.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/gauge.png");
    public RbmkGaugeRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkGaugeEntity panel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(panel.getBlockState().getValue(DummyableBlock.LEGACY_META)){case 2->90;case 4->180;case 3->270;default->0;}));for(int i=0;i<4;i++){var unit=panel.gauges[i];if(!unit.active)continue;poses.pushPose();poses.translate(.25,i/2*-.5+.25,i%2*-.5+.25);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,"Gauge",-1,false);poses.pushPose();double value=unit.lastRenderValue+(unit.renderValue-unit.lastRenderValue)*partial;long lower=Math.min(unit.min,unit.max),upper=Math.max(unit.min,unit.max);if(lower==upper)upper++;long range=upper-lower;double angle=(value-lower)/range*50;if(unit.min>unit.max)angle=50-angle;angle=Mth.clamp(angle,0,80);poses.translate(0,.4375,-.125);poses.mulPose(Axis.XN.rotationDegrees((float)angle-85));poses.translate(0,-.4375,.125);RbmkPanelRender.mesh(poses,buffers,LightTexture.FULL_BRIGHT,overlay,MODEL,TEXTURE,"Needle",0xff000000|unit.color,true);poses.popPose();var font=RbmkPanelRender.font();int height=font.lineHeight;String low=Math.abs(unit.min)<=10000?""+unit.min:RbmkPanelRender.shortNumber(unit.min),high=Math.abs(unit.max)<=10000?""+unit.max:RbmkPanelRender.shortNumber(unit.max);for(int j=0;j<2;j++){poses.pushPose();poses.translate(0,.4375,-.125);poses.mulPose(Axis.XN.rotationDegrees(10+j*50));poses.translate(0,-.4375,.125);poses.translate(.032,.4375,.125);poses.scale(.0025F,-.0025F,.0025F);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(j==0?low:high,0,-height/2,0,poses,buffers,light);poses.popPose();}if(unit.label!=null&&!unit.label.isEmpty()){poses.translate(.01,.3125,0);int width=font.width(unit.label);float scale=Math.min(.0125F,.4F/Math.max(width,1));poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(unit.label,-width/2,-height/2,0x00ff00,poses,buffers,LightTexture.FULL_BRIGHT);}poses.popPose();}poses.popPose();}
}
