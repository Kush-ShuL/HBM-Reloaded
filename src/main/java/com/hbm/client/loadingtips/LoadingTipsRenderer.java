package com.hbm.client.loadingtips;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Original 32-pixel dirt tiling, centered labels, 100x2 bar and ten-pixel yellow tip rows. */
public final class LoadingTipsRenderer {
    private static final ResourceLocation BACKGROUND=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/loading_screen/background.png");

    public static void background(GuiGraphics graphics,int width,int height) {
        float[] previousColor=RenderSystem.getShaderColor().clone();
        float shade=64F/255F;
        graphics.setColor(shade,shade,shade,1);
        try {
            graphics.blit(BACKGROUND,0,0,0,0F,0F,width,height,32,32);
        } finally {
            graphics.setColor(previousColor[0],previousColor[1],previousColor[2],previousColor[3]);
        }
    }

    public static void content(GuiGraphics graphics,Font font,int width,int height,
            Component header,Component stage,int progress) {
        if(progress>=0) {
            int left=width/2-50,top=height/2+16;
            graphics.fill(left,top,left+100,top+2,0xff808080);
            graphics.fill(left,top,left+progress,top+2,0xff80ff80);
        }
        if(header!=null)graphics.drawString(font,header,(width-font.width(header))/2,height/2-20,0xffffff,true);
        if(stage!=null)graphics.drawString(font,stage,(width-font.width(stage))/2,height/2+4,0xffffff,true);
        String[] lines=LoadingTipSource.text().split("\\$");
        for(int i=0;i<lines.length;i++) {
            graphics.drawString(font,ChatFormatting.YELLOW+lines[i],(width-font.width(lines[i]))/2,height/2-64+i*10,0xffffff,true);
        }
    }

    private LoadingTipsRenderer(){}
}
