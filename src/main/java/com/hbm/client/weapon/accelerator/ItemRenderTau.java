package com.hbm.client.weapon.accelerator;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.accelerator.ItemGunNI4NI;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
/** Source renderer matrices/part hierarchy retained; color/lighting states drive actual modern meshes. */
public class ItemRenderTau extends com.hbm.client.weapon.energy.EnergyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.75F * offset, -1.75F * offset, 3.5F * offset,
				-1.75F * offset, -1.75F * offset, 3.5F * offset);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
        AcceleratorDraw.reset();
		
		LegacyGunGL.texture(AcceleratorModels.tau_tex);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE");
		
		LegacyGunGL.glTranslated(0, -1, -4);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 4);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glTranslated(0, 0, -2);
		LegacyGunGL.glRotated(recoil[2] * 5, 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 2);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		AcceleratorDraw.cull(false);

		AcceleratorModels.tau.renderPart("Body");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -0.25, 0);
		LegacyGunGL.glRotated(rotate[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, 0.25, 0);
		AcceleratorModels.tau.renderPart("Rotor");
		LegacyGunGL.glPopMatrix();

		AcceleratorDraw.cull(true);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 1, 2);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.25, 0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -10D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
        AcceleratorDraw.reset();
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		AcceleratorDraw.cull(false);
		LegacyGunGL.texture(AcceleratorModels.tau_tex);
		AcceleratorModels.tau.renderAll();
		AcceleratorDraw.cull(true);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
