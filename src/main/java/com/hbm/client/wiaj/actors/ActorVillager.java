package com.hbm.client.wiaj.actors;
import com.hbm.client.wiaj.*;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.renderer.LightTexture;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.entity.npc.Villager;import net.minecraft.world.entity.EntityType;import com.mojang.math.Axis;
/** Original detached villager actor, never added to a real level or ticked as a game entity. */
public final class ActorVillager implements ISpecialActor {
 private final Villager villager=new Villager(EntityType.VILLAGER,Minecraft.getInstance().level);private CompoundTag data=new CompoundTag();private float limbSwingAmount,limbSwing;
 public ActorVillager(){}public ActorVillager(CompoundTag data){this.data=data;}
 @Override public void drawForegroundComponent(GuiGraphics graphics,int width,int height,int ticks,float interp){}
 @Override public void drawBackgroundComponent(JarRenderContext render,WorldInAJar world,int ticks,float interp){var pose=render.pose();pose.translate(data.getDouble("x"),data.getDouble("y"),data.getDouble("z"));pose.mulPose(Axis.YP.rotationDegrees((float)data.getDouble("yaw")));Minecraft.getInstance().getEntityRenderDispatcher().render(villager,0,0,0,0,interp,pose,render.buffers(),LightTexture.FULL_BRIGHT);}
 @Override public void updateActor(JarScene scene){limbSwingAmount+=(1-limbSwingAmount)*.4F;limbSwing+=limbSwingAmount;villager.walkAnimation.update(limbSwingAmount,.4F);}
 @Override public void setActorData(CompoundTag data){}@Override public void setDataPoint(String tag,Object value){}
}
