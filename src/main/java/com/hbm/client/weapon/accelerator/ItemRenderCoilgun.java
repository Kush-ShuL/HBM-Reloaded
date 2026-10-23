package com.hbm.client.weapon.accelerator;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.accelerator.ItemGunNI4NI;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
/** Source renderer matrices/part hierarchy retained; color/lighting states drive actual modern meshes. */
public class ItemRenderCoilgun extends com.hbm.client.weapon.energy.EnergyGunRenderer {

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
				-1.25F * offset, -1.5F * offset, 2.5F * offset,
				0, -7.5 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
        AcceleratorDraw.reset();

		LegacyGunGL.texture(AcceleratorModels.flaregun_tex);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		LegacyGunGL.glRotated(-90, 0, 1, 0);

		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		LegacyGunGL.glTranslated(-1.5 - recoil[0] * 0.5, 0, 0);
		LegacyGunGL.glRotated(recoil[0] * 45, 0, 0, 1);
		LegacyGunGL.glTranslated(1.5, 0, 0);

		double[] reload = HbmAnimations.getRelevantTransformation("RELOAD");
		LegacyGunGL.glTranslated(-2.5, 0, 0);
		LegacyGunGL.glRotated(reload[0] * -45, 0, 0, 1);
		LegacyGunGL.glTranslated(2.5, 0, 0);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(AcceleratorModels.coilgun_tex);
		AcceleratorModels.coilgun.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 3D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 1.25);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 4D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.25, -0.25, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -20D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.25, 0.5);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
        AcceleratorDraw.reset();
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(AcceleratorModels.coilgun_tex);
		AcceleratorModels.coilgun.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
