package com.hbm.client.weapon.rifle50;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.rifle50.Rifle50Guns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderAmat extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderAmat(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.8F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		
		standardAimingTransform(stack,
			-1F * offset, -1F * offset, 3.25F * offset,
			0, -4.875 / 8D, 1.875);
	}
	
	@Override
	public void renderFirstPerson(ItemStack stack) {
		boolean isScoped = isScoped(stack);
		if(isScoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		boolean deployed = HbmAnimations.getRelevantAnim(0) == null || HbmAnimations.getRelevantAnim(0).animation.getBus("BIPOD") == null;
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] bipod = HbmAnimations.getRelevantTransformation("BIPOD");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] boltTurn = HbmAnimations.getRelevantTransformation("BOLT_TURN");
		double[] boltPull = HbmAnimations.getRelevantTransformation("BOLT_PULL");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] scopeThrow = HbmAnimations.getRelevantTransformation("SCOPE_THROW");
		double[] scopeSpin = HbmAnimations.getRelevantTransformation("SCOPE_SPIN");

		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glTranslated(0, -3, -8);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 3, 8);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		Rifle50Models.amat.renderPart("Gun");
		
		if(isScoped(stack)) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(scopeThrow[0], scopeThrow[1], scopeThrow[2]);
			LegacyGunGL.glTranslated(0, 1.5, -4.5);
			LegacyGunGL.glRotated(scopeSpin[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -1.5, 4.5);
			Rifle50Models.amat.renderPart("Scope");
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.625, 0);
		LegacyGunGL.glRotated(boltTurn[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -0.625, 0);
		LegacyGunGL.glTranslated(0, 0, boltPull[2]);
		Rifle50Models.amat.renderPart("Bolt");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		Rifle50Models.amat.renderPart("Magazine");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0.3125, -0.625, -1);
		LegacyGunGL.glRotated(deployed ? 25 : bipod[1], 0, 0, 1);
		LegacyGunGL.glTranslated(-0.3125, 0.625, 1);
		Rifle50Models.amat.renderPart("BipodHingeLeft");
		LegacyGunGL.glTranslated(0.3125, -0.625, -1);
		LegacyGunGL.glRotated(deployed ? 80 : bipod[0], 1, 0, 0);
		LegacyGunGL.glTranslated(-0.3125, 0.625, 1);
		Rifle50Models.amat.renderPart("BipodLeft");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-0.3125, -0.625, -1);
		LegacyGunGL.glRotated(deployed ? -25 : -bipod[1], 0, 0, 1);
		LegacyGunGL.glTranslated(0.3125, 0.625, 1);
		Rifle50Models.amat.renderPart("BipodHingeRight");
		LegacyGunGL.glTranslated(-0.3125, -0.625, -1);
		LegacyGunGL.glRotated(deployed ? 80 : bipod[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0.3125, 0.625, 1);
		Rifle50Models.amat.renderPart("BipodRight");
		LegacyGunGL.glPopMatrix();
		
		if(isSilenced(stack)) {
			LegacyGunGL.glTranslated(0, 0.625, -4.3125);
			LegacyGunGL.glScaled(1.25, 1.25, 1.25);
			LegacyGunGL.texture(Rifle50Models.g3_attachments);
			Rifle50Models.g3.renderPart("Silencer");
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		} else {
			Rifle50Models.amat.renderPart("MuzzleBrake");

			double smokeScale = 0.5;
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.625, 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
			this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 1D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.5, 11);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderGapFlash(gun.lastShot[0]);
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.5, 6.75);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		if(isSilenced(stack)) {
		double scale = 0.8175D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-0.5, 0.5, -1);
		} else {
			double scale = 0.9375D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-0.5, 0.5, 0);
		}
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.25, -1.5);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		Rifle50Models.amat.renderPart("Gun");
		Rifle50Models.amat.renderPart("Bolt");
		Rifle50Models.amat.renderPart("Magazine");
		Rifle50Models.amat.renderPart("BipodLeft");
		Rifle50Models.amat.renderPart("BipodHingeLeft");
		Rifle50Models.amat.renderPart("BipodRight");
		Rifle50Models.amat.renderPart("BipodHingeRight");
		if(isScoped(stack)) Rifle50Models.amat.renderPart("Scope");
		
		boolean silenced = isSilenced(stack);
		if(silenced) {
			LegacyGunGL.glTranslated(0, 0.625, -4.3125);
			LegacyGunGL.glScaled(1.25, 1.25, 1.25);
			LegacyGunGL.texture(Rifle50Models.g3_attachments);
			Rifle50Models.g3.renderPart("Silencer");
		} else {
			Rifle50Models.amat.renderPart("MuzzleBrake");
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED && !silenced) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			if(ent == Minecraft.getInstance().player) {
				ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
				shot = gun.lastShot[0];
			} else {
				shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.5, 11);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderGapFlash(shot);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isScoped(ItemStack stack) {
		return true;
	}
	
	public boolean isSilenced(ItemStack stack) {
		return stack.getItem() == Rifle50Guns.gun_amat_penance || XWeaponModManager.hasUpgrade(stack, 0, 201);
	}
}
