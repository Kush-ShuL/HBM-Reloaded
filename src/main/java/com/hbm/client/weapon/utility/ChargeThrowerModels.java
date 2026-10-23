package com.hbm.client.weapon.utility;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class ChargeThrowerModels {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/charge_thrower.obj");
 static final ResourceLocation charge_thrower_tex=texture("charge_thrower"),charge_thrower_hook_tex=texture("charge_thrower_hook"),charge_thrower_mortar_tex=texture("charge_thrower_mortar");
 static final Mesh charge_thrower=new Mesh();
 static ResourceLocation texture(String name){return ResourceLocation.parse("hbm:textures/models/weapons/"+name+".png");}
 static final class Mesh {void renderPart(String part){LegacyGunGL.mesh(MODEL,part);}}
 private ChargeThrowerModels(){}
}
