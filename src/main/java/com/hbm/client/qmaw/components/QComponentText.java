package com.hbm.client.qmaw.components;
import com.hbm.client.qmaw.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
public class QComponentText extends ManualElement {
 protected final String text;protected int color=0xffffff;
 public QComponentText(String text){this.text=text;}
 public QComponentText setColor(int color){this.color=color;return this;}
 @Override public int getWidth(){return Minecraft.getInstance().font.width(text);}
 @Override public int getHeight(){return Minecraft.getInstance().font.lineHeight;}
 @Override public void render(GuiGraphics graphics,boolean hovered,int x,int y,int mouseX,int mouseY,GuiQMAW parent){graphics.drawString(Minecraft.getInstance().font,text,x,y,color,false);}
 @Override public void onClick(GuiQMAW screen){}
}
