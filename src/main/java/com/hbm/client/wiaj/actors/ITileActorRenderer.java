package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.WorldInAJar;import net.minecraft.nbt.CompoundTag;
public interface ITileActorRenderer {void renderActor(JarRenderContext render,WorldInAJar world,int ticks,float interp,CompoundTag data);void updateActor(int ticks,CompoundTag data);}
