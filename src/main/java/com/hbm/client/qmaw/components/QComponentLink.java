package com.hbm.client.qmaw.components;
import com.hbm.client.qmaw.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
public final class QComponentLink extends ManualElement {
 private final String link,text;private final ItemStack icon;private int color=0x0094ff,hoverColor=0xffd800;
 public QComponentLink(String link,String text){this.link=link;this.text=text;var page=QMAWLoader.qmaw.get(link);if(page==null){color=hoverColor=0xff7f7f;icon=ItemStack.EMPTY;}else icon=page.icon==null?ItemStack.EMPTY:page.icon;}
 public QComponentLink setColor(int color,int hover){this.color=color;hoverColor=hover;return this;}
 @Override public int getWidth(){return Minecraft.getInstance().font.width(text)+(icon.isEmpty()?0:18);}
 @Override public int getHeight(){return Math.max(Minecraft.getInstance().font.lineHeight,icon.isEmpty()?0:16);}
 @Override public void render(GuiGraphics graphics,boolean hovered,int x,int y,int mouseX,int mouseY,GuiQMAW parent){var font=Minecraft.getInstance().font;if(!icon.isEmpty()){graphics.renderItem(icon,x,y-1);graphics.renderItemDecorations(font,icon,x,y-1);x+=18;y+=(16-font.lineHeight)/2;}graphics.drawString(font,text,x,y,parent.qmawID.equals(link)?0xa0a0a0:hovered?hoverColor:color,false);}
 @Override public void onClick(GuiQMAW gui){if(gui.qmawID.equals(link))return;var page=QMAWLoader.qmaw.get(link);if(page!=null){GuiQMAW.clickSound();var next=new GuiQMAW(page);next.back.addAll(gui.back);next.back.add(gui.qmawID);Minecraft.getInstance().setScreen(next);}}
}
