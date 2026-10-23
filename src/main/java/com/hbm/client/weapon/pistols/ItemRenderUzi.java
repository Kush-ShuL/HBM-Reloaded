package com.hbm.client.weapon.pistols;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderUzi extends LegacyGunRenderer {

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
				-1.75F * offset, -1.5F * offset, 2.5F * offset,
				0, -4.375 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(isSaturnite(stack) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		double scale = 0.25D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] stockFront = HbmAnimations.getRelevantTransformation("STOCKFRONT");
		double[] stockBack = HbmAnimations.getRelevantTransformation("STOCKBACK");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		double[] slide = HbmAnimations.getRelevantTransformation("SLIDE");
		double[] yeet = HbmAnimations.getRelevantTransformation("YEET");
		double[] speen = HbmAnimations.getRelevantTransformation("SPEEN");

		LegacyGunGL.glTranslated(yeet[0], yeet[1], yeet[2]);
		LegacyGunGL.glRotated(speen[0], 0, 0, 1);
		
		LegacyGunGL.glTranslated(0, -2, -4);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 4);
		
		LegacyGunGL.glTranslated(0, 0, -6);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 6);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		PistolModels.uzi.renderPart("Gun");
		
		boolean silenced = hasSilencer(stack, 0);
		if(silenced) PistolModels.uzi.renderPart("Silencer");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.3125D, -5.75);
		LegacyGunGL.glRotated(180 - stockFront[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.3125D, 5.75);
		PistolModels.uzi.renderPart("StockFront");
		
		LegacyGunGL.glTranslated(0, -0.3125D, -3);
		LegacyGunGL.glRotated(-200 - stockBack[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.3125D, 3);
		PistolModels.uzi.renderPart("StockBack");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, slide[2]);
		PistolModels.uzi.renderPart("Slide");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		PistolModels.uzi.renderPart("Magazine");
		if(bullet[0] == 1) PistolModels.uzi.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();

		if(!silenced) {
			double smokeScale = 0.5;
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.75, 8.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
			this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.75D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.75, 8.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
			this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, 1, 1);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1, -4);
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isSaturnite(stack) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(hasSilencer(stack, index)) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		boolean silenced = hasSilencer(stack, 0);
		
		if(silenced && type == ItemRenderType.INVENTORY) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -4);
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isSaturnite(stack) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(silenced) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED && !silenced) {
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
			LegacyGunGL.glTranslated(0, 0.75, 8.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean hasSilencer(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 201);
	}
	
	public boolean isSaturnite(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 215);
	}
}
