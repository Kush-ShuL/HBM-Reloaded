package com.hbm.client.weapon.legendary;
import net.minecraft.resources.ResourceLocation;
/** Original ResourceManager aliases, retaining the actual mesh and skin for every legendary gun. */
final class LegendaryModels {
    static final Mesh lilmac=new Mesh("lilmac"),maresleg=new Mesh("maresleg"),sexy=new Mesh("sexy"),whiskey=new Mesh("whiskey");
    static final ResourceLocation lilmac_scope_tex=texture("lilmac_scope"),whiskey_tex=texture("whiskey");
    static ResourceLocation texture(String name){return ResourceLocation.parse("hbm:textures/models/weapons/"+name+".png");}
    record Mesh(String name){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/"+name+".obj"),part);}void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/"+name+".obj"),null);}}
    private LegendaryModels(){}
}
