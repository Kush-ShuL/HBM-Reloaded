package com.hbm.client.weapon.flamer;

import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.weapon.sedna.gun.flamer.FlamerContent;
import com.hbm.weapon.sedna.gun.GunInventory;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.client.animation.bus.HbmAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ItemRenderFlamer extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderFlamer(ResourceLocation texture) {
		this.texture = texture;
	}

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
				-1.5F * offset, -1.5F * offset, 2.75F * offset,
				0, -4.625 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE");
		
		LegacyGunGL.glTranslated(0, 2, -6);
		LegacyGunGL.glRotated(-equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2, 6);

		LegacyGunGL.glTranslated(0, 1, 0);
		LegacyGunGL.glRotated(rotate[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1, 0);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Gun");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Gun");
		if(hasShield(stack)) LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "HeatShield");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Tank");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Tank");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Gauge");
		LegacyGunGL.glTranslated(1.25, 1.25, 0);
		IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		LegacyGunGL.glRotated(-135 + (mag.getAmount(stack, new GunInventory(Minecraft.getInstance().player)) * 270D / mag.getCapacity(stack)), 0, 0, 1);
		LegacyGunGL.glTranslated(-1.25, -1.25, 0);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Gauge");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -3, 4);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-1, 1, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 1);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Gun");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Tank");
		LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "Gauge");
		if(hasShield(stack)) LegacyGunGL.mesh(ResourceLocation.parse("hbm:models/weapons/flamethrower.obj"), "HeatShield");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasShield(ItemStack stack) {
		return stack.getItem() == FlamerContent.GUNS.get("gun_flamer_daybreaker").get();
	}
}
