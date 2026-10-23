package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.*;import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.nbt.CompoundTag;
/** Source stack-text tooltip actor, distinct from the framed fancy panel. */
public final class ActorBasicPanel implements ISpecialActor {
 private final int x,y;private final List<Object[]> lines;
 public ActorBasicPanel(int x,int y,Object[]... lines){this.x=x;this.y=y;this.lines=List.of(lines);}
 @Override public void drawForegroundComponent(GuiGraphics graphics,int width,int height,int ticks,float interp){JarStackText.draw(graphics,lines,x+width/2,y+height/2,width,height);}
 @Override public void drawBackgroundComponent(JarRenderContext render,WorldInAJar world,int ticks,float interp){}@Override public void updateActor(JarScene scene){}@Override public void setActorData(CompoundTag data){}@Override public void setDataPoint(String tag,Object value){}
}
