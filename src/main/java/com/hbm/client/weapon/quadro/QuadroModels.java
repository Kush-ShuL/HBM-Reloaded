package com.hbm.client.weapon.quadro;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class QuadroModels {
    static final ResourceLocation quadro_tex=ResourceLocation.parse("hbm:textures/models/weapons/quadro.png"),quadro_rocket_tex=ResourceLocation.parse("hbm:textures/models/weapons/quadro_rocket.png"),rocket_tex=ResourceLocation.parse("hbm:textures/models/projectiles/rocket.png");
    static final Mesh quadro=new Mesh("hbm:models/weapons/quadro.obj");
    record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}}
}
