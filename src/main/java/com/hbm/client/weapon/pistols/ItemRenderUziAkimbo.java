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
public class ItemRenderUziAkimbo extends LegacyGunRenderer {
	
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
			LegacyGunGL.texture(isSaturnite(stack, index) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);

			LegacyGunGL.glPushMatrix();
			standardAimingTransform(stack, -2.25F * offset * i, -1.5F * offset, 2.5F * offset, 0, -4.375 / 8D, 1);
			
			double scale = 0.25D;
			LegacyGunGL.glScaled(scale, scale, scale);
	
			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			double[] stockFront = HbmAnimations.getRelevantTransformation("STOCKFRONT", index);
			double[] stockBack = HbmAnimations.getRelevantTransformation("STOCKBACK", index);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] lift = HbmAnimations.getRelevantTransformation("LIFT", index);
			double[] mag = HbmAnimations.getRelevantTransformation("MAG", index);
			double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", index);
			double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", index);
			double[] yeet = HbmAnimations.getRelevantTransformation("YEET", index);
			double[] speen = HbmAnimations.getRelevantTransformation("SPEEN", index);
	
			LegacyGunGL.glTranslated(yeet[0], yeet[1], yeet[2]);
			LegacyGunGL.glRotated(speen[0], 0, 0, i);
			
			LegacyGunGL.glTranslated(0, -2, -4);
			LegacyGunGL.glRotated(equip[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 2, 4);
			
			LegacyGunGL.glTranslated(0, 0, -6);
			LegacyGunGL.glRotated(lift[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 6);
			
			LegacyGunGL.glTranslated(0, 0, recoil[2]);
	
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
			PistolModels.uzi.renderPart(index == 0 ? "GunMirror" : "Gun");

			boolean silenced = hasSilencer(stack, index);
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
				this.renderSmokeNodes(gun.getConfig(stack, index).smokeNodes, 0.75D);
				LegacyGunGL.glPopMatrix();
				
				LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
				LegacyGunGL.glPushMatrix();
				LegacyGunGL.glTranslated(0, 0.75, 8.5);
				LegacyGunGL.glRotated(90, 0, 1, 0);
				LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
				this.renderMuzzleFlash(gun.lastShot[index], 75, 7.5);
				LegacyGunGL.glPopMatrix();
			}
			
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, 1, 1);
	}

	@Override
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPersonAkimbo(stack);
		LegacyGunGL.glTranslated(0, 1, 1);
	}

	@Override
	public void setupInv(ItemStack stack) {
		LegacyGunGL.glAlphaFunc(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		LegacyGunGL.glScaled(1, 1, -1);
		LegacyGunGL.glTranslated(8, 8, 0);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1, -4);
	}

	@Override
	public void renderEquipped(ItemStack stack, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isSaturnite(stack, 1) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		boolean silenced = hasSilencer(stack, 1);
		if(silenced) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(!silenced) {
			LivingEntity ent = (LivingEntity) data[1]; //Entity is second obj. passed 
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
			LegacyGunGL.glTranslated(0, 0.75, 8.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isSaturnite(stack, 0) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("GunMirror");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		boolean silenced = hasSilencer(stack, 0);
		if(silenced) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(!silenced) {
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

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(isSaturnite(stack, index) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart(index == 0 ? "GunMirror" : "Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(hasSilencer(stack, index)) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	@Override
	public void renderEntity(ItemStack stack) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		boolean silencer0 = hasSilencer(stack, 1);
		boolean silencer1 = hasSilencer(stack, 0);
		boolean anySilenced = silencer0 || silencer1;
		
		if(anySilenced) {
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-1, 1, 0);
		LegacyGunGL.texture(isSaturnite(stack, 1) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(silencer0) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1, 1, 0);
		LegacyGunGL.texture(isSaturnite(stack, 0) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("GunMirror");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(silencer1) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		boolean silencer0 = hasSilencer(stack, 1);
		boolean silencer1 = hasSilencer(stack, 0);
		boolean anySilenced = silencer0 || silencer1;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1, 0);
		if(anySilenced) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -4);
		}
		LegacyGunGL.texture(isSaturnite(stack, 1) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("Gun");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(silencer0) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glTranslated(0, 0, 5);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1, 0);
		if(anySilenced) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -4);
		}
		LegacyGunGL.texture(isSaturnite(stack, 0) ? PistolModels.uzi_saturnite_tex : PistolModels.uzi_tex);
		PistolModels.uzi.renderPart("GunMirror");
		PistolModels.uzi.renderPart("StockBack");
		PistolModels.uzi.renderPart("StockFront");
		PistolModels.uzi.renderPart("Slide");
		PistolModels.uzi.renderPart("Magazine");
		if(silencer1) PistolModels.uzi.renderPart("Silencer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasSilencer(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 201);
	}
	
	public boolean isSaturnite(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 215);
	}
}
