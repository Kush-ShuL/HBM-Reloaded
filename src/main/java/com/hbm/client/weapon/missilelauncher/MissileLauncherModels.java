package com.hbm.client.weapon.missilelauncher;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class MissileLauncherModels {
 static final ResourceLocation missile_launcher_tex=ResourceLocation.parse("hbm:textures/models/weapons/missile_launcher.png");
 static final Mesh missile_launcher=new Mesh("hbm:models/weapons/missile_launcher.obj");
 record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}}
}
