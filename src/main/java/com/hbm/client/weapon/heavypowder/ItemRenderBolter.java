package com.hbm.client.weapon.heavypowder;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.heavypowder.HeavyPowderGuns;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderBolter extends LegacyGunRenderer {

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
				-1.5F * offset, -2F * offset, 2.5F * offset,
				0, -10.5 / 8D, 1.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		LegacyGunGL.texture(HeavyPowderModels.bolter_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		LegacyGunGL.glRotated(180, 0, 1, 0);
		
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		LegacyGunGL.glRotated(recoil[0] * 5, 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, recoil[0]);

		double[] tilt = HbmAnimations.getRelevantTransformation("TILT");
		LegacyGunGL.glTranslated(0, tilt[0], 3);
		LegacyGunGL.glRotated(tilt[0] * 35, 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, -3);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		HeavyPowderModels.bolter.renderPart("Body");

		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 5);
		LegacyGunGL.glRotated(mag[0] * 60 * (mag[2] == 1 ? 2.5 : 1), -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, -5);
		HeavyPowderModels.bolter.renderPart("Mag");
		if(mag[2] != 1) HeavyPowderModels.bolter.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

        HeavyPowderDisplay.counter(stack);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.75, 1.25);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.25, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -12.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glRotated(180, 0, 1, 0);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(HeavyPowderModels.bolter_tex);
		HeavyPowderModels.bolter.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
