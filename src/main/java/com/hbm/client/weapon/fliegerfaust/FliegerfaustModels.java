package com.hbm.client.weapon.fliegerfaust;

import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;

final class FliegerfaustModels {
    static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/weapons/fliegerfaust.png");
    static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/weapons/fliegerfaust.obj");
    static final ResourceLocation PROJECTILES = ResourceLocation.parse("hbm:models/projectiles/projectiles.obj");
    static final ResourceLocation GRENADE_TEXTURE = ResourceLocation.parse("hbm:textures/models/projectiles/grenade.png");

    static void part(String part) { LegacyGunGL.mesh(MODEL, part); }
    private FliegerfaustModels() { }
}
