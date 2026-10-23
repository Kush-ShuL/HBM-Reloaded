package com.hbm.client.weapon.rifle556;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class Rifle556Models {
 static final Mesh g3=new Mesh("hbm:models/weapons/g3.obj");
 static final ResourceLocation g3_attachments=ResourceLocation.parse("hbm:textures/models/weapons/g3_attachments.png");
 static final ResourceLocation g3_black_tex=ResourceLocation.parse("hbm:textures/models/weapons/g3_polymer_black.png");
 static final ResourceLocation g3_green_tex=ResourceLocation.parse("hbm:textures/models/weapons/g3_polymer_green.png");
 static final ResourceLocation g3_tex=ResourceLocation.parse("hbm:textures/models/weapons/g3.png");
 static final ResourceLocation g3_zebra_tex=ResourceLocation.parse("hbm:textures/models/weapons/g3_zebra.png");
 static final Mesh stg77=new Mesh("hbm:models/weapons/stg77.obj");
 static final ResourceLocation stg77_tex=ResourceLocation.parse("hbm:textures/models/weapons/stg77.png");
 record Mesh(String path){
  void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}
  void renderPart(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.renderOnly(model,frame.pose(),frame.buffers().getBuffer(RenderType.entityCutout(texture)),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,part);}
  void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}
 }
}
