package com.hbm.client.weapon.grenade40;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class Grenade40Models {
 static final ResourceLocation casings_tex=ResourceLocation.parse("hbm:textures/particle/casings.png");
 static final Mesh congolake=new Mesh("hbm:models/weapons/congolake.obj");
 static final ResourceLocation congolake_tex=ResourceLocation.parse("hbm:textures/models/weapons/congolake.png");
 static final ResourceLocation grenade_tex=ResourceLocation.parse("hbm:textures/models/projectiles/grenade.png");
 static final Mesh mk108=new Mesh("hbm:models/weapons/mk108.obj");
 static final ResourceLocation mk108_tex=ResourceLocation.parse("hbm:textures/models/weapons/mk108.png");
 static final Mesh projectiles=new Mesh("hbm:models/projectiles/projectiles.obj");
 record Mesh(String path){
  void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}
  void renderPart(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.renderOnly(model,frame.pose(),frame.buffers().getBuffer(RenderType.entityCutout(texture)),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,part);}
  void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}
 }
}
