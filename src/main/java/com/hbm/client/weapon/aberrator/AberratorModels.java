package com.hbm.client.weapon.aberrator;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class AberratorModels {
 static final Mesh aberrator=new Mesh("hbm:models/weapons/aberrator.obj");
 static final ResourceLocation aberrator_tex=ResourceLocation.parse("hbm:textures/models/weapons/aberrator.png");
 static final ResourceLocation eott_tex=ResourceLocation.parse("hbm:textures/models/weapons/eott.png"); private static final ThreadLocal<Boolean> ALPHA_ZERO=ThreadLocal.withInitial(()->false);
 static AlphaScope alphaZero(){return new AlphaScope();}
 static final class AlphaScope implements AutoCloseable{private final boolean previous=ALPHA_ZERO.get();AlphaScope(){ALPHA_ZERO.set(true);}@Override public void close(){if(previous)ALPHA_ZERO.set(true);else ALPHA_ZERO.remove();}}
 record Mesh(String path){
  void renderPart(String part){if(!ALPHA_ZERO.get()){LegacyGunGL.mesh(ResourceLocation.parse(path),part);return;}draw(part,-1);}
  void renderPart(String part,int color){draw(part,color);}
  void renderAll(){if(!ALPHA_ZERO.get()){LegacyGunGL.mesh(ResourceLocation.parse(path),null);return;}draw(null,-1);}
  void draw(String part,int color){var f=LegacyGunGL.frame();var texture=java.util.Objects.requireNonNull(f.texture()[0],"Source model texture");var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse(path));var vertices=f.buffers().getBuffer(ALPHA_ZERO.get()?AberratorRenderTypes.alphaZero(texture):RenderType.entityCutout(texture));LegacyObjRenderer.render(model,f.pose(),vertices,f.light(),f.overlay(),color,LegacyObjRenderer.Convention.HFR,face->part==null||face.legacyPart().equalsIgnoreCase(part));}
 }
}
