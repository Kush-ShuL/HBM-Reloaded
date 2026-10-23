package com.hbm.radar;
import net.minecraft.world.entity.Entity;
/** Original radar command recipient, shared with actual launch pads. */
public interface IRadarCommandReceiver {boolean sendCommandPosition(int x,int y,int z);boolean sendCommandEntity(Entity target);}
