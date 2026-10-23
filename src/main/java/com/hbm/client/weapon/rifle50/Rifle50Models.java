package com.hbm.client.weapon.rifle50;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class Rifle50Models {
 static final Mesh amat=new Mesh("hbm:models/weapons/amat.obj");
 static final ResourceLocation amat_penance_tex=ResourceLocation.parse("hbm:textures/models/weapons/amat_penance.png");
 static final ResourceLocation amat_subtlety_tex=ResourceLocation.parse("hbm:textures/models/weapons/amat_subtlety.png");
 static final ResourceLocation amat_tex=ResourceLocation.parse("hbm:textures/models/weapons/amat.png");
 static final Mesh g3=new Mesh("hbm:models/weapons/g3.obj");
 static final ResourceLocation g3_attachments=ResourceLocation.parse("hbm:textures/models/weapons/g3_attachments.png");
 static final ResourceLocation greasegun_tex=ResourceLocation.parse("hbm:textures/models/weapons/greasegun.png");
 static final Mesh m2=new Mesh("hbm:models/weapons/m2_browning.obj");
 static final ResourceLocation m2_tex=ResourceLocation.parse("hbm:textures/models/weapons/m2_browning.png");
 record Mesh(String path){
  void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}
  void renderPart(String part,int color){var frame=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(frame.texture()[0],"Source gun model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));LegacyObjRenderer.renderOnly(model,frame.pose(),frame.buffers().getBuffer(RenderType.entityCutout(texture)),frame.light(),frame.overlay(),color,LegacyObjRenderer.Convention.HFR,part);}
  void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}
 }
}
