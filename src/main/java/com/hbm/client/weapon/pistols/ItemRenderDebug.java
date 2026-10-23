package com.hbm.client.weapon.pistols;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderDebug extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 1);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.0F * offset, -0.75F * offset, 1F * offset,
				0, -3.875 / 8D, 0);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		double scale = 0.125D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);

		double[] equipSpin = HbmAnimations.getRelevantTransformation("ROTATE");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] reloadLift = HbmAnimations.getRelevantTransformation("RELOAD_LIFT");
		double[] reloadJolt = HbmAnimations.getRelevantTransformation("RELOAD_JOLT");
		double[] reloadTilt = HbmAnimations.getRelevantTransformation("RELAOD_TILT");
		double[] cylinderFlip = HbmAnimations.getRelevantTransformation("RELOAD_CYLINDER");
		double[] reloadBullets = HbmAnimations.getRelevantTransformation("RELOAD_BULLETS");
		
		LegacyGunGL.glRotated(equipSpin[0], 0, 0, 1);

		standardAimingTransform(stack, 0, 0, recoil[2], -recoil[2], 0, 0);
		LegacyGunGL.glRotated(recoil[2] * 10, 0, 0, 1);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-9, 2.5, 0);
		LegacyGunGL.glRotated(recoil[2] * -10, 0, 0, 1);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glRotated(reloadLift[0], 0, 0, 1);
		LegacyGunGL.glTranslated(reloadJolt[0], 0, 0);
		LegacyGunGL.glRotated(reloadTilt[0], 1, 0, 0);
		
		LegacyGunGL.texture(PistolModels.debug_gun_tex);
		PistolModels.lilmac.renderPart("Gun");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(cylinderFlip[0], 1, 0, 0);
		PistolModels.lilmac.renderPart("Pivot");
		LegacyGunGL.glTranslated(0, 1.75, 0);
		LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("DRUM")[2] * -60, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.75, 0);
		PistolModels.lilmac.renderPart("Cylinder");
		LegacyGunGL.glTranslated(reloadBullets[0], reloadBullets[1], reloadBullets[2]);
		if(HbmAnimations.getRelevantTransformation("RELOAD_BULLETS_CON")[0] != 1)
		PistolModels.lilmac.renderPart("Bullets");
		PistolModels.lilmac.renderPart("Casings");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix(); /// HAMMER ///
		LegacyGunGL.glTranslated(4, 1.25, 0);
		LegacyGunGL.glRotated(-30 + 30 * HbmAnimations.getRelevantTransformation("HAMMER")[2], 0, 0, 1);
		LegacyGunGL.glTranslated(-4, -1.25, 0);
		PistolModels.lilmac.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0.125, 2.5, 0);
		this.renderGapFlash(gun.lastShot[0]);
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-9.5, 2.5, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		//this.renderMuzzleFlash(gun.lastShot);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		LegacyGunGL.glTranslated(0, 1, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {

		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glAlphaFunc(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.debug_gun_tex);
		PistolModels.lilmac.renderPart("Gun");
		PistolModels.lilmac.renderPart("Cylinder");
		PistolModels.lilmac.renderPart("Bullets");
		PistolModels.lilmac.renderPart("Casings");
		PistolModels.lilmac.renderPart("Pivot");
		PistolModels.lilmac.renderPart("Hammer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			if(ent == Minecraft.getInstance().player) {
				ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
				shot = gun.lastShot[0];
			} else {
				shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0.125, 2.5, 0);
			this.renderGapFlash(shot);
			LegacyGunGL.glPopMatrix();
		}
	}
}
