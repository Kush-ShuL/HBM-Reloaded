package com.hbm.client.weapon.flaregun;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class FlaregunModels {
    static final ResourceLocation flaregun_tex=ResourceLocation.parse("hbm:textures/models/weapons/flaregun.png");
    static final Mesh flaregun=new Mesh("hbm:models/weapons/flaregun.obj");
    record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}}
}
