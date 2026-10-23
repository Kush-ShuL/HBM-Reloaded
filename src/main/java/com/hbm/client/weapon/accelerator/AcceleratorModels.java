package com.hbm.client.weapon.accelerator;
import net.minecraft.resources.ResourceLocation;
final class AcceleratorModels {
 static final Mesh coilgun=new Mesh("hbm:models/weapons/coilgun.obj");
 static final ResourceLocation coilgun_tex=ResourceLocation.parse("hbm:textures/models/weapons/coilgun.png");
 static final ResourceLocation flaregun_tex=ResourceLocation.parse("hbm:textures/models/weapons/flaregun.png");
 static final Mesh n_i_4_n_i=new Mesh("hbm:models/weapons/n_i_4_n_i.obj");
 static final ResourceLocation n_i_4_n_i_greyscale_tex=ResourceLocation.parse("hbm:textures/models/weapons/n_i_4_n_i_greyscale.png");
 static final ResourceLocation n_i_4_n_i_tex=ResourceLocation.parse("hbm:textures/models/weapons/n_i_4_n_i.png");
 static final Mesh tau=new Mesh("hbm:models/weapons/tau.obj");
 static final ResourceLocation tau_tex=ResourceLocation.parse("hbm:textures/models/weapons/tau.png");
 record Mesh(String path){void renderPart(String part){AcceleratorDraw.mesh(ResourceLocation.parse(path),part);}void renderAll(){AcceleratorDraw.mesh(ResourceLocation.parse(path),null);}}
}
