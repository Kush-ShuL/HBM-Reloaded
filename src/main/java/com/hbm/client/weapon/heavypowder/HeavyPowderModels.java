package com.hbm.client.weapon.heavypowder;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class HeavyPowderModels {
 static final Mesh bolter=new Mesh("hbm:models/weapons/bolter.obj");
 static final ResourceLocation bolter_tex=ResourceLocation.parse("hbm:textures/models/weapons/bolter.png");
 static final Mesh double_barrel=new Mesh("hbm:models/weapons/sacred_dragon.obj");
 static final ResourceLocation double_barrel_sacred_dragon_tex=ResourceLocation.parse("hbm:textures/models/weapons/double_barrel_sacred_dragon.png");
 static final ResourceLocation double_barrel_tex=ResourceLocation.parse("hbm:textures/models/weapons/double_barrel.png");
 static final ResourceLocation heretic_tex=ResourceLocation.parse("hbm:textures/models/weapons/sexy_heretic.png");
 record Mesh(String path){
  void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}
  void renderPart(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.renderOnly(model,frame.pose(),frame.buffers().getBuffer(RenderType.entityCutout(texture)),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,part);}
  void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}
 }
}
