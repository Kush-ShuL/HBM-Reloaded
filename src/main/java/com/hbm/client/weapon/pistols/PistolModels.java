package com.hbm.client.weapon.pistols;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class PistolModels {
 static final Mesh am180=new Mesh("hbm:models/weapons/am180.obj");
 static final ResourceLocation am180_tex=ResourceLocation.parse("hbm:textures/models/weapons/am180.png");
 static final Mesh bio_revolver=new Mesh("hbm:models/weapons/bio_revolver.obj");
 static final ResourceLocation bio_revolver_atlas_tex=ResourceLocation.parse("hbm:textures/models/weapons/bio_revolver_atlas.png");
 static final ResourceLocation bio_revolver_tex=ResourceLocation.parse("hbm:textures/models/weapons/bio_revolver.png");
 static final ResourceLocation dani_celestial_tex=ResourceLocation.parse("hbm:textures/models/weapons/dani_celestial.png");
 static final ResourceLocation dani_lunar_tex=ResourceLocation.parse("hbm:textures/models/weapons/dani_lunar.png");
 static final ResourceLocation debug_gun_tex=ResourceLocation.parse("hbm:textures/models/weapons/debug_gun.png");
 static final Mesh greasegun=new Mesh("hbm:models/weapons/greasegun.obj");
 static final ResourceLocation greasegun_clean_tex=ResourceLocation.parse("hbm:textures/models/weapons/greasegun_clean.png");
 static final ResourceLocation greasegun_tex=ResourceLocation.parse("hbm:textures/models/weapons/greasegun.png");
 static final Mesh hangman=new Mesh("hbm:models/weapons/hangman.obj");
 static final ResourceLocation hangman_tex=ResourceLocation.parse("hbm:textures/models/weapons/hangman.png");
 static final ResourceLocation heavy_revolver_protege_tex=ResourceLocation.parse("hbm:textures/models/weapons/protege.png");
 static final ResourceLocation heavy_revolver_tex=ResourceLocation.parse("hbm:textures/models/weapons/heavy_revolver.png");
 static final Mesh henry=new Mesh("hbm:models/weapons/henry.obj");
 static final ResourceLocation henry_lincoln_tex=ResourceLocation.parse("hbm:textures/models/weapons/henry_lincoln.png");
 static final ResourceLocation henry_tex=ResourceLocation.parse("hbm:textures/models/weapons/henry.png");
 static final Mesh lilmac=new Mesh("hbm:models/weapons/lilmac.obj");
 static final Mesh mike_hawk=new Mesh("hbm:models/weapons/mike_hawk.obj");
 static final ResourceLocation mike_hawk_tex=ResourceLocation.parse("hbm:textures/models/weapons/lag.png");
 static final Mesh star_f=new Mesh("hbm:models/weapons/star_f.obj");
 static final ResourceLocation star_f_elite_tex=ResourceLocation.parse("hbm:textures/models/weapons/star_f_elite.png");
 static final ResourceLocation star_f_tex=ResourceLocation.parse("hbm:textures/models/weapons/star_f.png");
 static final Mesh torpedo=new Mesh("hbm:models/weapons/torpedo.obj");
 static final ResourceLocation torpedo_tex=ResourceLocation.parse("hbm:textures/models/weapons/torpedo.png");
 static final Mesh uzi=new Mesh("hbm:models/weapons/uzi.obj");
 static final ResourceLocation uzi_saturnite_tex=ResourceLocation.parse("hbm:textures/models/weapons/uzi_saturnite.png");
 static final ResourceLocation uzi_tex=ResourceLocation.parse("hbm:textures/models/weapons/uzi.png");
 record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}}
}
