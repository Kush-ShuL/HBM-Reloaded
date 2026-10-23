package com.hbm.client.weapon.rifle762;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class Rifle762Models {
 static final Mesh carbine=new Mesh("hbm:models/weapons/carbine.obj");
 static final ResourceLocation carbine_bayonet_tex=ResourceLocation.parse("hbm:textures/models/weapons/carbine_bayonet.png");
 static final ResourceLocation carbine_scope_tex=ResourceLocation.parse("hbm:textures/models/weapons/carbine_scope.png");
 static final ResourceLocation carbine_tex=ResourceLocation.parse("hbm:textures/models/weapons/huntsman.png");
 static final Mesh mas36=new Mesh("hbm:models/weapons/mas36.obj");
 static final ResourceLocation mas36_tex=ResourceLocation.parse("hbm:textures/models/weapons/mas36.png");
 static final Mesh minigun=new Mesh("hbm:models/weapons/minigun.obj");
 static final ResourceLocation minigun_dual_tex=ResourceLocation.parse("hbm:textures/models/weapons/minigun_dual.png");
 static final ResourceLocation minigun_lacunae_tex=ResourceLocation.parse("hbm:textures/models/weapons/minigun_lacunae.png");
 static final ResourceLocation minigun_tex=ResourceLocation.parse("hbm:textures/models/weapons/minigun.png");
 record Mesh(String path){
  void renderPart(String part){draw(part,-1);}
  void draw(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.render(model,frame.pose(),Rifle762Clip.vertices(frame.buffers().getBuffer(RenderType.entityCutout(texture))),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,face->part==null||face.legacyPart().equalsIgnoreCase(part));}
  void renderAll(){draw(null,-1);}
 }
}
