package com.hbm.client.weapon.panzerschreck;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class PanzerschreckModels {
    static final ResourceLocation panzerschreck_tex=ResourceLocation.parse("hbm:textures/models/weapons/panzerschreck.png");
    static final Mesh panzerschreck=new Mesh("hbm:models/weapons/panzerschreck.obj");
    record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}}
}
