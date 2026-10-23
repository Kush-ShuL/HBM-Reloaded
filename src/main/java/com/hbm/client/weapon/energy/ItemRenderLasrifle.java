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
public class ItemRenderLasrifle extends EnergyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (hasScope(stack) ? 0.75F : 0.66F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		
		if(hasScope(stack)) {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 2.5F * offset,
				0, -7.375 / 8D, 0.75);
		} else {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 2.5F * offset,
				0, -5.25 / 8D, 1);
		}
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		if(hasScope(stack) && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(EnergyModels.lasrifle_tex);
		double scale = 0.3125D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] lever = HbmAnimations.getRelevantTransformation("LEVER");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		
		LegacyGunGL.glTranslated(0, -1, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 6);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		EnergyModels.lasrifle.renderPart("Gun");
		EnergyModels.lasrifle.renderPart("Stock");
		if(hasScope(stack)) EnergyModels.lasrifle.renderPart("Scope");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -0.375, 2.375);
		LegacyGunGL.glRotated(lever[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.375, -2.375);
		EnergyModels.lasrifle.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		EnergyModels.lasrifle.renderPart("Battery");
		LegacyGunGL.glPopMatrix();
		
		if(!hasShotgun(stack)) EnergyModels.lasrifle.renderPart("Barrel");
		LegacyGunGL.texture(EnergyModels.lasrifle_mods_tex);
		if(hasShotgun(stack)) EnergyModels.lasrifle_mods.renderPart("BarrelShotgun");
		if(hasCapacitor(stack)) EnergyModels.lasrifle_mods.renderPart("UnderBarrel");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, 12);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		renderLaserFlash(gun.lastShot[0], 150, 1.5D, 0xff0000);
		LegacyGunGL.glTranslated(0, 0, -0.25);
		renderLaserFlash(gun.lastShot[0], 150, 0.75D, 0xff8000);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0, 4);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.03125D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0.75, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1, -1);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(EnergyModels.lasrifle_tex);
		EnergyModels.lasrifle.renderPart("Gun");
		EnergyModels.lasrifle.renderPart("Stock");
		if(hasScope(stack)) EnergyModels.lasrifle.renderPart("Scope");
		EnergyModels.lasrifle.renderPart("Lever");
		EnergyModels.lasrifle.renderPart("Battery");
		if(!hasShotgun(stack)) EnergyModels.lasrifle.renderPart("Barrel");
		LegacyGunGL.texture(EnergyModels.lasrifle_mods_tex);
		if(hasShotgun(stack)) EnergyModels.lasrifle_mods.renderPart("BarrelShotgun");
		if(hasCapacitor(stack)) EnergyModels.lasrifle_mods.renderPart("UnderBarrel");
		
		if(type == ItemRenderType.EQUIPPED) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			if(ent == Minecraft.getInstance().player) {
				ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
				shot = gun.lastShot[0];
			} else {
				shot = flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.5, 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			renderLaserFlash(shot, 150, 1.5D, 0xff0000);
			LegacyGunGL.glTranslated(0, 0, -0.25);
			renderLaserFlash(shot, 150, 0.75D, 0xff8000);
			LegacyGunGL.glPopMatrix();
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasScope(ItemStack stack) {
		return !XWeaponModManager.hasUpgrade(stack, 0, 218);
	}
	
	public boolean hasShotgun(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 216);
	}
	
	public boolean hasCapacitor(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 217);
	}
}
