package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.nbt.CompoundTag;
public final class ActorTileEntity extends ActorBase {
 private final ITileActorRenderer renderer;public ActorTileEntity(ITileActorRenderer renderer){this.renderer=renderer;}public ActorTileEntity(ITileActorRenderer renderer,CompoundTag data){this(renderer);this.data=data;}
 @Override public void drawForegroundComponent(GuiGraphics graphics,int width,int height,int ticks,float interp){}
 @Override public void drawBackgroundComponent(JarRenderContext render,WorldInAJar world,int ticks,float interp){renderer.renderActor(render,world,ticks,interp,data);}
 @Override public void updateActor(JarScene scene){renderer.updateActor(scene.script.ticksElapsed,data);}
}
