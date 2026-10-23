package com.hbm.client.weapon.chemthrower;

import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import net.minecraft.resources.ResourceLocation;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mags.IMagazine;


import com.hbm.client.animation.bus.HbmAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class ItemRenderChemthrower extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-2.5F * offset, -2.5F * offset, 2.5F * offset,
				0, -4.375 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(ResourceLocation.parse("hbm:textures/models/weapons/chemthrower.png"));
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		
		LegacyGunGL.glTranslated(0, -2, -4);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 4);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Gun");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Hose");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Nozzle");
		
		LegacyGunGL.glTranslated(0, 0.875, 1.75);
		IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		double d = (double) mag.getAmount(stack, new com.hbm.weapon.sedna.gun.GunInventory(Minecraft.getInstance().player)) / (double) mag.getCapacity(stack);
		LegacyGunGL.glRotated(135 - d * 270, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.875, -1.75);
		
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Gauge");
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -2.5, 0.5);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0.875, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -10D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, -0.5);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);

		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(ResourceLocation.parse("hbm:textures/models/weapons/chemthrower.png"));
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Gun");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Hose");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Nozzle");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/chemthrower.obj"), "Gauge");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
