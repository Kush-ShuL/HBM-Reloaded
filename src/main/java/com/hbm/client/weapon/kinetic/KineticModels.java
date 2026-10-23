package com.hbm.client.weapon.kinetic;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class KineticModels {
 static final ResourceLocation casings_tex=ResourceLocation.parse("hbm:textures/particle/casings.png");
 static final Mesh liberator=new Mesh("hbm:models/weapons/liberator.obj");
 static final ResourceLocation liberator_tex=ResourceLocation.parse("hbm:textures/models/weapons/liberator.png");
 static final Mesh maresleg=new Mesh("hbm:models/weapons/maresleg.obj");
 static final ResourceLocation maresleg_tex=ResourceLocation.parse("hbm:textures/models/weapons/maresleg.png");
 static final Mesh shredder=new Mesh("hbm:models/weapons/shredder.obj");
 static final ResourceLocation shredder_orig_tex=ResourceLocation.parse("hbm:textures/models/weapons/shredder_orig.png");
 static final ResourceLocation shredder_tex=ResourceLocation.parse("hbm:textures/models/weapons/shredder.png");
 static final Mesh spas_12=new Mesh("hbm:models/weapons/spas-12.obj");
 static final ResourceLocation spas_12_tex=ResourceLocation.parse("hbm:textures/models/weapons/spas-12.png");
 record Mesh(String path){
  void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}
  void renderPart(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.renderOnly(model,frame.pose(),frame.buffers().getBuffer(RenderType.entityCutout(texture)),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,part);}
  void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}
 }
}
