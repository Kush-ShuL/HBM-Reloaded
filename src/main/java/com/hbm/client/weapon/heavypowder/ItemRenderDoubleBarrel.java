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
public class ItemRenderDoubleBarrel extends LegacyGunRenderer {
	
	protected ResourceLocation texture;
	
	public ItemRenderDoubleBarrel(ResourceLocation texture) {
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
				-1.25F * offset, -1F * offset, 2F * offset,
				0, -2 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] barrel = HbmAnimations.getRelevantTransformation("BARREL");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] shells = HbmAnimations.getRelevantTransformation("SHELLS");
		double[] shellFlip = HbmAnimations.getRelevantTransformation("SHELL_FLIP");
		double[] lever = HbmAnimations.getRelevantTransformation("LEVER");
		double[] buckle = HbmAnimations.getRelevantTransformation("BUCKLE");
		double[] no_ammo = HbmAnimations.getRelevantTransformation("NO_AMMO");

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glTranslated(recoil[0] * 3, recoil[1], recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 10, 1, 0, 0);

		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4);

		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(turn[1], 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 4);

		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(lift[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4);

		HeavyPowderModels.double_barrel.renderPart("Stock");
		
		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(0, -0.4375, -0.875);
		LegacyGunGL.glRotated(barrel[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.4375, 0.875);
		
		HeavyPowderModels.double_barrel.renderPart("BarrelShort");
		if(!isSawedOff(stack)) HeavyPowderModels.double_barrel.renderPart("Barrel");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0.75, 0, -0.6875);
		LegacyGunGL.glRotated(buckle[1], 0, 1, 0);
		LegacyGunGL.glTranslated(-0.75, 0, 0.6875);
		HeavyPowderModels.double_barrel.renderPart("Buckle");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-0.3125, 0.3125, 0);
		LegacyGunGL.glRotated(lever[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0.3125, -0.3125, 0);
		HeavyPowderModels.double_barrel.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		if(no_ammo[0] == 0) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(shells[0], shells[1], shells[2]);
			LegacyGunGL.glTranslated(0, 0, -1);
			LegacyGunGL.glRotated(shellFlip[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 1);
			HeavyPowderModels.double_barrel.renderPart("Shells");
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(2, 2, 2);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 1, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		if(isSawedOff(stack)) {
			double scale = 2D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-2, 0.5, 0);
		} else {
			double scale = 1.375D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(0, 0.5, 0);
		}
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
		LegacyGunGL.texture(texture);
		HeavyPowderModels.double_barrel.renderPart("Stock");
		HeavyPowderModels.double_barrel.renderPart("BarrelShort");
		if(!isSawedOff(stack)) HeavyPowderModels.double_barrel.renderPart("Barrel");
		HeavyPowderModels.double_barrel.renderPart("Buckle");
		HeavyPowderModels.double_barrel.renderPart("Lever");
		HeavyPowderModels.double_barrel.renderPart("Shells");
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
			LegacyGunGL.glScaled(2, 2, 2);
			this.renderMuzzleFlash(shot, 75, 5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isSawedOff(ItemStack stack) {
		return stack.getItem() == HeavyPowderGuns.gun_double_barrel_sacred_dragon || XWeaponModManager.hasUpgrade(stack, 0, 203);
	}
}
