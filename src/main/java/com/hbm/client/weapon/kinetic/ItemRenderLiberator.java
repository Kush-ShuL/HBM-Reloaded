package com.hbm.client.weapon.kinetic;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderLiberator extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

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
				-1.5F * offset, -1.25F * offset, 1.25F * offset,
				0, -4.625 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(KineticModels.liberator_tex);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] latch = HbmAnimations.getRelevantTransformation("LATCH");
		double[] brk = HbmAnimations.getRelevantTransformation("BREAK");
		double[] shell1 = HbmAnimations.getRelevantTransformation("SHELL1");
		double[] shell2 = HbmAnimations.getRelevantTransformation("SHELL2");
		double[] shell3 = HbmAnimations.getRelevantTransformation("SHELL3");
		double[] shell4 = HbmAnimations.getRelevantTransformation("SHELL4");
		
		LegacyGunGL.glTranslated(0, -1, -3);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 3);
		
		LegacyGunGL.glTranslated(0, -3, -3);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 3, 3);

		LegacyGunGL.glTranslated(recoil[0] * 2, recoil[1], recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 10, 1, 0, 0);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		KineticModels.liberator.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(0, -0.5, 0.75);
		LegacyGunGL.glRotated(brk[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.5, -0.75);
		KineticModels.liberator.renderPart("Barrel");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell1[0], shell1[1], shell1[2]);
		KineticModels.liberator.renderPart("Shell1");
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell2[0], shell2[1], shell2[2]);
		KineticModels.liberator.renderPart("Shell2");
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell3[0], shell3[1], shell3[2]);
		KineticModels.liberator.renderPart("Shell3");
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell4[0], shell4[1], shell4[2]);
		KineticModels.liberator.renderPart("Shell4");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.15625, 0.75);
		LegacyGunGL.glRotated(latch[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.15625, -0.75);
		KineticModels.liberator.renderPart("Latch");
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.375;
		
		GunConfig cfg = gun.getConfig(stack, 0);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.25, 7.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		LegacyGunGL.glTranslated(0, 0, 0.25 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		LegacyGunGL.glTranslated(0, 0, -0.5 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		LegacyGunGL.glTranslated(0, 0.5 / smokeScale, 0);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		LegacyGunGL.glTranslated(0, 0, 0.5 / smokeScale);
		this.renderSmokeNodes(cfg.smokeNodes, 1D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.5, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(1.5, 1.5, 1.5);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, 1, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(KineticModels.liberator_tex);
		KineticModels.liberator.renderAll();
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
			LegacyGunGL.glTranslated(0, 0.5, 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(1.5, 1.5, 1.5);
			this.renderMuzzleFlash(shot, 75, 5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
