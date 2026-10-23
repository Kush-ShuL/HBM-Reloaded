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
public class ItemRenderCarbine extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		
		if(this.isScoped(stack)) {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 0.875F * offset,
					0, -8 / 8D, 0.25);
		} else {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 0.875F * offset,
					0, -6.25 / 8D, 0.25);
		}
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		boolean isScoped = isScoped(stack);
		if(isScoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(Rifle762Models.carbine_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] slide = HbmAnimations.getRelevantTransformation("SLIDE");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		double[] rel = HbmAnimations.getRelevantTransformation("REL");
		double[] stab = HbmAnimations.getRelevantTransformation("STAB");
		
		LegacyGunGL.glTranslated(0, -1, -2);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 2);
		
		LegacyGunGL.glTranslated(0, 0, -2);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 2);

		LegacyGunGL.glTranslated(stab[0], stab[1], stab[2]);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		Rifle762Models.carbine.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, slide[2]);
		Rifle762Models.carbine.renderPart("Slide");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		Rifle762Models.carbine.renderPart("Magazine");
		LegacyGunGL.glTranslated(rel[0], rel[1], rel[2]);
		if(bullet[0] != 1) Rifle762Models.carbine.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();
		
		if(!isScoped(stack)) {
			Rifle762Models.carbine.renderPart("IronSight");
		} else {
			LegacyGunGL.texture(Rifle762Models.carbine_scope_tex);
			Rifle762Models.carbine.renderPart("Scope");
		}
		
		if(hasBayonet(stack)) {
			LegacyGunGL.texture(Rifle762Models.carbine_bayonet_tex);
			Rifle762Models.carbine.renderPart("Bayonet");
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.25D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0, 2);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		if(hasBayonet(stack)) {
			double scale = 1.1875D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(1.5, 0, 0);
		} else {
			double scale = 1.375D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-0.5, 0, 0);
		}
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, -1.75);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.carbine_tex);
		Rifle762Models.carbine.renderPart("Gun");
		Rifle762Models.carbine.renderPart("Slide");
		Rifle762Models.carbine.renderPart("Magazine");
		if(!isScoped(stack)) {
			Rifle762Models.carbine.renderPart("IronSight");
		} else {
			LegacyGunGL.texture(Rifle762Models.carbine_scope_tex);
			Rifle762Models.carbine.renderPart("Scope");
		}
		if(hasBayonet(stack)) {
			LegacyGunGL.texture(Rifle762Models.carbine_bayonet_tex);
			Rifle762Models.carbine.renderPart("Bayonet");
		}
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
			LegacyGunGL.glTranslated(0, 1, 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.5, 0.5, 0.5);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isScoped(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 202);
	}
	
	public boolean hasBayonet(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 219);
	}
}
