package com.hbm.client.weapon.utility;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.utility.ChargeThrowerBullets;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.world.item.ItemStack;
/** Original ItemRenderChargeThrower matrices, named mesh parts and animation buses. */
public class ItemRenderChargeThrower extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 0F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);

		float offset = 0.8F;
		float zoom = 0.5F;
		
		if(isScoped(stack)) standardAimingTransform(stack,
				-1.5F * offset, -1.25F * offset, 3.5F * offset,
				-0.15625, -6.5 / 8D, 1.6875);
		else standardAimingTransform(stack,
				-1.5F * offset, -1.25F * offset, 3.5F * offset,
				-1.5F * zoom, -1.25F * zoom, 3.5F * zoom);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		boolean usingScope = this.isScoped(stack) && gun.aimingProgress == 1 && gun.prevAimingProgress == 1;
		MagazineFullReload mag = (MagazineFullReload) gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(usingScope) {
			double scale = 3.5D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(-0.5, -1.5, -4);
		} else {
			double scale = 0.5D;
			LegacyGunGL.glScaled(scale, scale, scale);
		}

		boolean reloading = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("AMMO") != null;
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] raise = HbmAnimations.getRelevantTransformation("RAISE");
		double[] ammo = HbmAnimations.getRelevantTransformation("AMMO");
		double[] twist = HbmAnimations.getRelevantTransformation("TWIST");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] roll = HbmAnimations.getRelevantTransformation("ROLL");

		LegacyGunGL.glTranslated(0, 0, -7);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 7);
		
		LegacyGunGL.glTranslated(0, -7, 4);
		LegacyGunGL.glRotated(raise[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 7, -4);

		LegacyGunGL.glTranslated(recoil[0], recoil[1], recoil[2]);

		LegacyGunGL.glTranslated(0, 0, -2);
		LegacyGunGL.glRotated(turn[1], 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 2);
		LegacyGunGL.glTranslated(0, -1, 0);
		LegacyGunGL.glRotated(roll[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, 1, 0);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_tex);
		ChargeThrowerModels.charge_thrower.renderPart("Gun");
		if(isScoped(stack) && !usingScope) ChargeThrowerModels.charge_thrower.renderPart("Scope");
		
		if(mag.getAmount(stack, null) > 0 || reloading) {

			LegacyGunGL.glTranslated(ammo[0], ammo[1], ammo[2]);
			LegacyGunGL.glRotated(twist[2], 0, 0, 1);
			
			if(mag.getType(stack, null) == ChargeThrowerBullets.HOOK) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_hook_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Hook");
			}
			if(mag.getType(stack, null) == ChargeThrowerBullets.MORTAR) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_mortar_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Mortar");
			}
			if(mag.getType(stack, null) == ChargeThrowerBullets.HEAVY_MORTAR) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_mortar_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Mortar");
				ChargeThrowerModels.charge_thrower.renderPart("Oomph");
			}
			//LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_rocket_tex);
			//ChargeThrowerModels.charge_thrower.renderPart("Rocket");
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0.75, 1, 4);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, -0.625);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, -1);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_tex);
		ChargeThrowerModels.charge_thrower.renderPart("Gun");
		if(isScoped(stack)) ChargeThrowerModels.charge_thrower.renderPart("Scope");

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		MagazineFullReload mag = (MagazineFullReload) gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);

		if(mag.getAmount(stack, null) > 0) {
			
			if(mag.getType(stack, null) == ChargeThrowerBullets.HOOK) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_hook_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Hook");
			}
			if(mag.getType(stack, null) == ChargeThrowerBullets.MORTAR) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_mortar_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Mortar");
			}
			if(mag.getType(stack, null) == ChargeThrowerBullets.HEAVY_MORTAR) {
				LegacyGunGL.texture(ChargeThrowerModels.charge_thrower_mortar_tex);
				ChargeThrowerModels.charge_thrower.renderPart("Mortar");
				ChargeThrowerModels.charge_thrower.renderPart("Oomph");
			}
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean isScoped(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 202);
	}
}
