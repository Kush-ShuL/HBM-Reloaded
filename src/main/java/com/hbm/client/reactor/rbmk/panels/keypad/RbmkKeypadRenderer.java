package com.hbm.client.reactor.rbmk.panels.keypad;
import com.hbm.client.reactor.rbmk.panels.RbmkPanelRender;
import com.hbm.machine.reactor.rbmk.panels.keypad.RbmkKeypadEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
public final class RbmkKeypadRenderer implements BlockEntityRenderer<RbmkKeypadEntity>{
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/button.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/keypad.png");
    public RbmkKeypadRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkKeypadEntity panel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(panel.getBlockState().getValue(DummyableBlock.LEGACY_META)){case 2->90;case 4->180;case 3->270;default->0;}));for(int i=0;i<4;i++){var key=panel.keys[i];if(!key.active)continue;boolean glow=key.isPressed;float multiplier=glow?1:.65F;poses.pushPose();poses.translate(.25,i/2*-.5+.25,i%2*-.5+.25);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,"Socket",-1,false);poses.pushPose();poses.translate(key.isPressed?-.03125:0,0,0);int color=0xff000000|(int)((key.color>>16&255)*multiplier)<<16|(int)((key.color>>8&255)*multiplier)<<8|(int)((key.color&255)*multiplier);RbmkPanelRender.mesh(poses,buffers,glow?LightTexture.FULL_BRIGHT:light,overlay,MODEL,TEXTURE,"Button",color,false);poses.popPose();if(key.label!=null&&!key.label.isEmpty()){var font=RbmkPanelRender.font();poses.translate(.01,.3125,0);int width=font.width(key.label);float scale=Math.min(.0125F,.4F/Math.max(width,1));poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(key.label,-width/2,-font.lineHeight/2,0x00ff00,poses,buffers,LightTexture.FULL_BRIGHT);}poses.popPose();}poses.popPose();}
}
