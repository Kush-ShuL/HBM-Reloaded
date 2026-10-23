// Original X5846 renderer matrices, hierarchy and all part calls retained.
package com.hbm.client.weapon.energy;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.energy.EnergyContent;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderTeslaCannon extends EnergyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.75F * offset, -0.5F * offset, 1.75F * offset,
				-1.3125F * offset, 0F * offset, -0.5F * offset);
	}
	
	protected static String label = "AUTO";
	
	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(EnergyModels.tesla_cannon_tex);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE");
		double[] count = HbmAnimations.getRelevantTransformation("COUNT");
		double[] yomi = HbmAnimations.getRelevantTransformation("YOMI");
		double[] squeeze = HbmAnimations.getRelevantTransformation("SQUEEZE");
		
		LegacyGunGL.glTranslated(0, -2, -2);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 2);

		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 2, 1, 0, 0);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		int amount = Math.max((int) count[0], gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, new GunInventory(Minecraft.getInstance().player)));
		
		EnergyModels.tesla_cannon.renderPart("Gun");
		EnergyModels.tesla_cannon.renderPart("Extension");
		
		double cogAngle = cycle[2];
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -1.625, 0);
		LegacyGunGL.glRotated(cogAngle, 0, 0, 1);
		LegacyGunGL.glTranslated(0, 1.625, 0);
		EnergyModels.tesla_cannon.renderPart("Cog");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(0, -1.625, 0);
		LegacyGunGL.glRotated(cogAngle, 0, 0, 1);
		LegacyGunGL.glTranslated(0, 1.625, 0);
		
		for(int i = 0; i < Math.min(amount, 8); i++) {
			EnergyModels.tesla_cannon.renderPart("Capacitor");
			
			if(i < 4) {
				LegacyGunGL.glTranslated(0, -1.625, 0);
				LegacyGunGL.glRotated(-22.5, 0, 0, 1);
				LegacyGunGL.glTranslated(0, 1.625, 0);
			} else {
				if(i == 4) {
					LegacyGunGL.glTranslated(0, -1.625, 0);
					LegacyGunGL.glRotated(-cogAngle, 0, 0, 1);
					LegacyGunGL.glTranslated(0, 1.625, 0);
					LegacyGunGL.glTranslated(-cogAngle * 0.5 / 22.5, 0, 0);
				}
				LegacyGunGL.glTranslated(0.5, 0, 0);
			}
		}
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(yomi[0], yomi[1], yomi[2]);
		LegacyGunGL.glRotated(135, 0, 1, 0);
		LegacyGunGL.glScaled(squeeze[0], squeeze[1], squeeze[2]);
		LegacyGunGL.texture(EnergyModels.yomiTex);
		EnergyModels.yomiModel.renderAll();
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 1.5, 1);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);

		LegacyGunGL.texture(EnergyModels.tesla_cannon_tex);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		EnergyModels.tesla_cannon.renderPart("Gun");
		EnergyModels.tesla_cannon.renderPart("Extension");
		EnergyModels.tesla_cannon.renderPart("Cog");
		
		LegacyGunGL.glPushMatrix();
		for(int i = 0; i < 10; i++) {
			EnergyModels.tesla_cannon.renderPart("Capacitor");
			
			if(i < 4) {
				LegacyGunGL.glTranslated(0, -1.625, 0);
				LegacyGunGL.glRotated(-22.5, 0, 0, 1);
				LegacyGunGL.glTranslated(0, 1.625, 0);
			} else {
				LegacyGunGL.glTranslated(0.5, 0, 0);
			}
		}
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
