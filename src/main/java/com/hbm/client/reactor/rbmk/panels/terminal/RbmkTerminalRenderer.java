package com.hbm.client.reactor.rbmk.panels.terminal;
import com.hbm.client.reactor.rbmk.panels.RbmkPanelRender;
import com.hbm.machine.reactor.rbmk.panels.terminal.RbmkTerminalEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
public final class RbmkTerminalRenderer implements BlockEntityRenderer<RbmkTerminalEntity>{
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/terminal.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/terminal.png");
    public RbmkTerminalRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkTerminalEntity panel,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(panel.getBlockState().getValue(DummyableBlock.LEGACY_META)){case 2->90;case 4->180;case 3->270;default->0;}));poses.translate(.25,0,0);RbmkPanelRender.mesh(poses,buffers,light,overlay,MODEL,TEXTURE,null,-1,false);poses.translate(.0635,.125,.0625*5.5);var font=RbmkPanelRender.font();float scale=1F/250F;String prefix="> ",suffix="";int prefixWidth=font.width(prefix),maxWidth=172,color=panel.doesRepeat?0xff8000:0x00ff00;if(Minecraft.getInstance().screen instanceof RbmkTerminalScreen&&RbmkTerminalScreen.lastTerminal==panel&&System.currentTimeMillis()%1000<500)suffix="_";int suffixWidth=font.width(suffix);for(int i=0;i<18;i++){String label=i==0?RbmkTerminalScreen.workingLine(panel):panel.history[i-1];if(label==null)label="";var builder=new StringBuilder(40);if(i==0||!label.isEmpty())builder.append(prefix);int width=prefixWidth;for(int j=0;j<label.length();j++){char c=label.charAt(j);width+=font.width(String.valueOf(c));if(width<=maxWidth)builder.append(c);else break;}if(i==0&&font.width(builder.toString())+suffixWidth<=maxWidth)builder.append(suffix);poses.translate(0,10*scale,0);poses.pushPose();poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));RbmkPanelRender.text(builder.toString(),0,-font.lineHeight/2,color,poses,buffers,LightTexture.FULL_BRIGHT);poses.popPose();}poses.popPose();}
}
