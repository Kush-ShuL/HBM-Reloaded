package com.hbm.client.weapon.drill;
import com.hbm.client.weapon.legendary.*;
import net.minecraft.resources.ResourceLocation;



import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mags.IMagazine;

import com.hbm.client.animation.bus.HbmAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class ItemRenderDrill extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 0F : -0.5F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.25F * offset, -1.75F * offset, 1.75F * offset,
				-1F * offset, -1.75F * offset, 1.25F * offset);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(ResourceLocation.parse("hbm:textures/models/weapons/drill.png"));
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		double gauge = (double) mag.getAmount(stack, null) / (double) mag.getCapacity(stack);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] deploy = HbmAnimations.getRelevantTransformation("DEPLOY");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] spin = HbmAnimations.getRelevantTransformation("SPIN");

		/*float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		LegacyGunGL.glRotated(15 * (1 - aimingProgress), 0, 1, 0);
		LegacyGunGL.glRotated(-10 * (1 - aimingProgress), 1, 0, 0);*/
		
		LegacyGunGL.glRotated(15 * (1 - deploy[0] * 0.5), 0, 1, 0);
		LegacyGunGL.glRotated(-10 * (1 - deploy[0] * 0.5), 1, 0, 0);
		
		LegacyGunGL.glTranslated(0, 2, -6);
		LegacyGunGL.glRotated(equip[0] * -45, 0, 1, 0);
		LegacyGunGL.glRotated(equip[0] * -20, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2, 6);
		
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		
		LegacyGunGL.glTranslated(0, 0, deploy[0]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"Base");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1, 2.0625, -1.75);
		LegacyGunGL.glRotated(45, 1, 0, 0);
		LegacyGunGL.glRotated(-135 + gauge * 270, 0, 0, 1);
		LegacyGunGL.glRotated(-45, 1, 0, 0);
		LegacyGunGL.glTranslated(-1, -2.0625, 1.75);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"Gauge");
		LegacyGunGL.glPopMatrix();

		double rot = spin[0];
		double rot2 = rot * 5;

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, Math.sin(rot2 * Math.PI / 180) * 0.125 - 0.125, 0);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"Piston1");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, Math.sin(rot2 * Math.PI / 180 + Math.PI * 2D / 3D) * 0.125 - 0.125, 0);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"Piston2");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, Math.sin(rot2 * Math.PI / 180 + Math.PI * 4D / 3D) * 0.125 - 0.125, 0);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"Piston3");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(rot, 0, 0, -1);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"DrillBack");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(rot, 0, 0, 1);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),"DrillFront");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(1, -2, 6);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(ResourceLocation.parse("hbm:textures/models/weapons/drill.png"));
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/drill.obj"),null);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
