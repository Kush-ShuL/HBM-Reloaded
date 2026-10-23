package com.hbm.client.weapon.stinger;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class StingerModels {
    static final ResourceLocation stinger_tex=ResourceLocation.parse("hbm:textures/models/weapons/stinger.png"),panzerschreck_tex=ResourceLocation.parse("hbm:textures/models/weapons/panzerschreck.png");
    static final Mesh stinger=new Mesh("hbm:models/weapons/stinger.obj"),panzerschreck=new Mesh("hbm:models/weapons/panzerschreck.obj");
    record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse(path),part);}void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse(path),null);}}
}
