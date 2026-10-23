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
public class ItemRenderGreasegun extends LegacyGunRenderer {

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
				-1.5F * offset, -1F * offset, 1.75F * offset,
				0, -2.625 / 8D, 1.125);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(isRefurbished(stack) ? PistolModels.greasegun_clean_tex : PistolModels.greasegun_tex);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] stock = HbmAnimations.getRelevantTransformation("STOCK");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] flap = HbmAnimations.getRelevantTransformation("FLAP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] handle = HbmAnimations.getRelevantTransformation("HANDLE");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		
		LegacyGunGL.glTranslated(0, -3, -3);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 3, 3);
		
		LegacyGunGL.glTranslated(0, -3, -3);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 3, 3);

		if(gun.aimingProgress < 1F) LegacyGunGL.glRotated(turn[2], 0, 0, 1);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		PistolModels.greasegun.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, -4 - stock[2]);
		PistolModels.greasegun.renderPart("Stock");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		PistolModels.greasegun.renderPart("Magazine");
		if(bullet[0] != 1) PistolModels.greasegun.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -1.4375, -0.125);
		LegacyGunGL.glRotated(handle[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1.4375, 0.125);
		PistolModels.greasegun.renderPart("Handle");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.53125, 0);
		LegacyGunGL.glRotated(flap[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -0.5125, 0);
		PistolModels.greasegun.renderPart("Flap");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.25;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-0.25, 0, 1.5);
		LegacyGunGL.glRotated(turn[2], 0, 0, -1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 1D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glRotated(turn[2], 0, 0, -1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 1D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
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
		LegacyGunGL.glTranslated(-0.5, 2, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 2, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isRefurbished(stack) ? PistolModels.greasegun_clean_tex : PistolModels.greasegun_tex);
		PistolModels.greasegun.renderAll();
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
			LegacyGunGL.glTranslated(0, 0, 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.5, 0.5, 0.5);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isRefurbished(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 206);
	}
}
