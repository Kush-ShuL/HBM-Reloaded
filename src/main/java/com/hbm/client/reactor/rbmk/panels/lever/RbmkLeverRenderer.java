package com.hbm.client.reactor.rbmk.panels.lever;
import com.hbm.client.reactor.rbmk.panels.RbmkPanelRender;
import com.hbm.machine.reactor.rbmk.panels.lever.RbmkLeverEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
/** Original Base/Lever parts,180degree physical flip and frame-interpolated3tick network approach. */
public final class RbmkLeverRenderer implements BlockEntityRenderer<RbmkLeverEntity>{
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/lever.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/lever.png");
    public RbmkLeverRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkLeverEntity panel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(panel.getBlockState().getValue(DummyableBlock.LEGACY_META)){case 2->90;case 4->180;case 3->270;default->0;}));for(int i=0;i<2;i++){var lever=panel.levers[i];if(!lever.active)continue;poses.pushPose();poses.translate(.25,0,i*-.5+.25);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,"Base",-1,false);poses.pushPose();float progress=lever.prevFlipProgress+(lever.flipProgress-lever.prevFlipProgress)*partial;poses.translate(.125,.5625,0);poses.mulPose(Axis.ZP.rotationDegrees(-180*progress));poses.translate(-.125,-.5625,0);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,"Lever",-1,false);poses.popPose();if(lever.label!=null&&!lever.label.isEmpty()){var font=RbmkPanelRender.font();poses.translate(.01,.0625,0);int width=font.width(lever.label);float scale=Math.min(.0125F,.4F/Math.max(width,1));poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(lever.label,-width/2,-font.lineHeight/2,0x00ff00,poses,buffers,LightTexture.FULL_BRIGHT);}poses.popPose();}poses.popPose();}
}
