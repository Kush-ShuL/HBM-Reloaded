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
public class ItemRenderHangman extends LegacyGunRenderer {

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
				-1.5F * offset, -0.875F * offset, 1.75F * offset,
				0, -1.5 / 8D, 1.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(PistolModels.hangman_tex);
		float offset = 0.8F;

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] roll = HbmAnimations.getRelevantTransformation("ROLL");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] smack = HbmAnimations.getRelevantTransformation("SMACK");
		double[] lid = HbmAnimations.getRelevantTransformation("LID");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] bullets = HbmAnimations.getRelevantTransformation("BULLETS");
		
		LegacyGunGL.glTranslated(1.5F * offset, 0, -1);
		LegacyGunGL.glRotated(turn[1], 0, 1, 0);
		LegacyGunGL.glTranslated(-1.5F * offset, 0, 1);
		
		LegacyGunGL.glRotated(roll[2], 0, 0, 1);
		LegacyGunGL.glTranslated(smack[0], smack[1], smack[2]);
		
		double scale = 0.125D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		LegacyGunGL.glTranslated(0, -4, -10);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 4, 10);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		PistolModels.hangman.renderPart("Rifle");
		PistolModels.hangman.renderPart("Internals");
		
		LegacyGunGL.glPushMatrix();
		//i give the fuck up
		LegacyGunGL.glTranslated(-2.1875, -1.75, 0);
		LegacyGunGL.glRotated(lid[2], 0, 0, 1);
		LegacyGunGL.glTranslated(2.1875, 1.75, 0);
		PistolModels.hangman.renderPart("Lid");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		PistolModels.hangman.renderPart("Magazine");
		if(bullets[0] == 0) PistolModels.hangman.renderPart("Bullets");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 1.5;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 29);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 29);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(2, 2, 2);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 4.25, 11);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 2.5, 0);
	}

	@Override
	public void setupEntity(ItemStack stack) {
		double scale = 0.0625D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.hangman_tex);
		PistolModels.hangman.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			double shotRand = 0;
			if(ent == Minecraft.getInstance().player) {
				ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
				shot = gun.lastShot[0];
				shotRand = gun.shotRand;
			} else {
				shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, 29);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(2, 2, 2);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
