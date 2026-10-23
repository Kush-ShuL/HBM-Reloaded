package com.hbm.client.qmaw;
import net.minecraft.client.gui.GuiGraphics;
public abstract class ManualElement {
 public abstract int getWidth();public abstract int getHeight();
 public abstract void render(GuiGraphics graphics,boolean hovered,int x,int y,int mouseX,int mouseY,GuiQMAW parent);
 public abstract void onClick(GuiQMAW screen);
}
