package com.hbm.client.weapon.fatman;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class FatmanModels {
 static final ResourceLocation fatman_tex=ResourceLocation.parse("hbm:textures/models/weapons/fatman.png"),fatman_mininuke_tex=ResourceLocation.parse("hbm:textures/models/weapons/fatman_mininuke.png"),fatman_balefire_tex=ResourceLocation.parse("hbm:textures/models/weapons/fatman_balefire.png");
 static final Mesh fatman=new Mesh("hbm:models/weapons/fatman.obj");
 record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}}
 private FatmanModels(){}
}
