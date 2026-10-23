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
public class ItemRenderStarF extends LegacyGunRenderer {

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
				-1.75F * offset, -1.75F * offset, 2.5F * offset,
				0, -7.625 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(PistolModels.star_f_tex);
		double scale = 0.25D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER");
		double[] tilt = HbmAnimations.getRelevantTransformation("TILT");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		double[] slide = HbmAnimations.getRelevantTransformation("SLIDE");

		LegacyGunGL.glTranslated(0, -2, -8);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 8);

		LegacyGunGL.glTranslated(0, 1, -3);
		LegacyGunGL.glRotated(turn[2], 0, 0, 1);
		LegacyGunGL.glRotated(tilt[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1, 3);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		PistolModels.star_f.renderPart("Gun");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.75, -4.25);
		LegacyGunGL.glRotated(60 * (hammer[0] - 1), 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.75, 4.25);
		PistolModels.star_f.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, slide[2] * 2.3125);
		PistolModels.star_f.renderPart("Slide");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		PistolModels.star_f.renderPart("Mag");
		LegacyGunGL.glTranslated(bullet[0], bullet[1], bullet[2]);
		PistolModels.star_f.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();
		
		if(hasSilencer(stack)) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2.375, -0.25);
			LegacyGunGL.texture(PistolModels.uzi_tex);
			PistolModels.uzi.renderPart("Silencer");
			LegacyGunGL.glPopMatrix();
			
		} else {
			double smokeScale = 0.5;
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 3, 6.125);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
			this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.75D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 3, 6.125);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
			this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, -0.25, 1.75);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-1, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.25, -5);
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		renderStandardGun(stack);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		boolean silenced = hasSilencer(stack);
		
		if(silenced && type == ItemRenderType.INVENTORY) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -6);
		}
		
		renderStandardGun(stack);
		
		if(type == ItemRenderType.EQUIPPED && !silenced) {
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
			LegacyGunGL.glTranslated(0, 3, 6.25);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean hasSilencer(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 201);
	}
	
	public void renderStandardGun(ItemStack stack) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.star_f_tex);
		PistolModels.star_f.renderPart("Gun");
		PistolModels.star_f.renderPart("Slide");
		PistolModels.star_f.renderPart("Mag");
		PistolModels.star_f.renderPart("Hammer");
		boolean silenced = hasSilencer(stack);
		if(silenced) {
			LegacyGunGL.glTranslated(0, 2.375, -0.25);
			LegacyGunGL.texture(PistolModels.uzi_tex);
			PistolModels.uzi.renderPart("Silencer");
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
