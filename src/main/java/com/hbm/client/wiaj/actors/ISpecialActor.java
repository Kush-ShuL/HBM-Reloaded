package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.nbt.CompoundTag;
public interface ISpecialActor {
 void drawForegroundComponent(GuiGraphics graphics,int w,int h,int ticks,float interp);
 void drawBackgroundComponent(JarRenderContext render,WorldInAJar world,int ticks,float interp);
 void updateActor(JarScene scene);void setActorData(CompoundTag data);void setDataPoint(String tag,Object value);
}
