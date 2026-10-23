package com.hbm.client.weapon.energy;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.resources.ResourceLocation;
final class EnergyModels {
 static final Mesh tesla_cannon=new Mesh("weapons/tesla_cannon"),laser_pistol=new Mesh("weapons/laser_pistol"),lasrifle=new Mesh("weapons/lasrifle"),lasrifle_mods=new Mesh("weapons/lasrifle_mods"),yomiModel=new Mesh("trinkets/yomi");
 static final ResourceLocation tesla_cannon_tex=texture("weapons/tesla_cannon"),laser_pistol_tex=texture("weapons/laser_pistol"),laser_pistol_pew_pew_tex=texture("weapons/laser_pistol_pew_pew"),laser_pistol_morning_glory_tex=texture("weapons/laser_pistol_morning_glory"),lasrifle_tex=texture("weapons/lasrifle"),lasrifle_mods_tex=texture("weapons/lasrifle_mods"),yomiTex=texture("trinkets/yomi");
 static ResourceLocation texture(String path){return ResourceLocation.parse("hbm:textures/models/"+path+".png");}
 record Mesh(String path){void renderPart(String part){LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/"+path+".obj"),part);}void renderAll(){LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/"+path+".obj"),null);}}
}
