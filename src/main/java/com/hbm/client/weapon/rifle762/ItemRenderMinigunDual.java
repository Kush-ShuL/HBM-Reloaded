package com.hbm.client.weapon.rifle762;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.rifle762.Rifle762Guns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderMinigunDual extends LegacyGunRenderer {
	
	@Override public boolean isAkimbo(LivingEntity entity) { return true; }

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
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		float offset = 0.8F;
		
		for(int i = -1; i <= 1; i += 2) {
			int index = i == -1 ? 0 : 1;
			LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);

			LegacyGunGL.glPushMatrix();
			standardAimingTransform(stack, -2.75F * offset * i, -1.75F * offset, 2.5F * offset, 0, 0, 0);
			
			double scale = 0.375D;
			LegacyGunGL.glScaled(scale, scale, scale);

			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE", index);
			
			LegacyGunGL.glTranslated(0, 3, -6);
			LegacyGunGL.glRotated(equip[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -3, 6);
			
			LegacyGunGL.glTranslated(0, 0, recoil[2]);
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

			Rifle762Models.minigun.renderPart(index == 0 ? "GunDual" : "Gun");
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glRotated(rotate[2] * i, 0, 0, 1);
			Rifle762Models.minigun.renderPart("Barrels");
			LegacyGunGL.glPopMatrix();

			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			
			LegacyGunGL.glRotated(gun.shotRand * 90, 1, 0, 0);
			LegacyGunGL.glScaled(1.5, 1.5, 1.5);
			this.renderMuzzleFlash(gun.lastShot[index], 50, 7.5);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(-1, -3.5, 8);

	}

	@Override
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(2, -3.5, 8);
	}

	@Override
	public void setupInv(ItemStack stack) {
		LegacyGunGL.glScaled(1, 1, -1);
		LegacyGunGL.glTranslated(8, 8, 0);
		double scale = 0.875D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderEquipped(ItemStack stack, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);
		Rifle762Models.minigun.renderPart("Gun");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		LivingEntity ent = (LivingEntity) data[1];
		long shot;
		double shotRand = 0;
		if(ent == Minecraft.getInstance().player) {
			ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
			shot = gun.lastShot[1];
			shotRand = gun.shotRand;
		} else {
			shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
			if(shot < 0) return;
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 12.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);

		LegacyGunGL.glTranslated(0, 0.5, 0);
		LegacyGunGL.glRotated(shotRand * 90, 1, 0, 0);
		LegacyGunGL.glScaled(1.5, 1.5, 1.5);
		this.renderMuzzleFlash(shot, 50, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);
		Rifle762Models.minigun.renderPart("GunDual");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
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
		LegacyGunGL.glTranslated(0, 0, 12.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);

		LegacyGunGL.glTranslated(0, 0.5, 0);
		LegacyGunGL.glRotated(shotRand * 90, 1, 0, 0);
		LegacyGunGL.glScaled(1.5, 1.5, 1.5);
		this.renderMuzzleFlash(shot, 50, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);
		Rifle762Models.minigun.renderPart(index == 0 ? "GunDual" : "Gun");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderInv(ItemStack stack) {
		
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		Rifle762Models.minigun.renderPart("GunDual");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		Rifle762Models.minigun.renderPart("Gun");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.minigun_dual_tex);
		Rifle762Models.minigun.renderPart("Gun");
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasSilencer(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 201);
	}
	
	public boolean isSaturnite(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 215);
	}
}
