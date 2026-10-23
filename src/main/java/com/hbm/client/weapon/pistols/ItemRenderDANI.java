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
public class ItemRenderDANI extends LegacyGunRenderer {
	
	@Override public boolean isAkimbo(LivingEntity entity) { return true; }

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

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
			LegacyGunGL.texture(index == 0 ? PistolModels.dani_celestial_tex : PistolModels.dani_lunar_tex);
			
			LegacyGunGL.glPushMatrix();
			
			standardAimingTransform(stack,
					-1.5F * offset * i, -0.75F * offset, 1F * offset,
					0, -3.125 / 8D, 0.25);
			
			double scale = 0.125D;
			LegacyGunGL.glScaled(scale, scale, scale);
	
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] reloadMove = HbmAnimations.getRelevantTransformation("RELOAD_MOVE", index);
			double[] reloadRot = HbmAnimations.getRelevantTransformation("RELOAD_ROT", index);
			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			
			LegacyGunGL.glTranslated(recoil[0], recoil[1], recoil[2]);
			LegacyGunGL.glRotated(recoil[2] * 10, 1, 0, 0);
	
			LegacyGunGL.glTranslated(0, -2, -2);
			LegacyGunGL.glRotated(equip[0], -1, 0, 0);
			LegacyGunGL.glTranslated(0, 2, 2);
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.5, 9.25);
			LegacyGunGL.glRotated(-recoil[2] * 10, 1, 0, 0);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			this.renderSmokeNodes(gun.getConfig(stack, index).smokeNodes, 0.5D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glTranslated(reloadMove[0], reloadMove[1], reloadMove[2]);
	
			LegacyGunGL.glRotated(reloadRot[0], 1, 0, 0);
			LegacyGunGL.glRotated(reloadRot[2] * i, 0, 0, 1);
			LegacyGunGL.glRotated(reloadRot[1] * i, 0, 1, 0);
			PistolModels.bio_revolver.renderPart("Grip");
			
			LegacyGunGL.glPushMatrix(); /// FRONT PUSH ///
			LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("FRONT", index)[2], 1, 0, 0);
			PistolModels.bio_revolver.renderPart("Barrel");
			LegacyGunGL.glPushMatrix(); /// LATCH PUSH ///
			LegacyGunGL.glTranslated(0, 2.3125, -0.875);
			LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("LATCH", index)[2], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -2.3125, 0.875);
			PistolModels.bio_revolver.renderPart("Latch");
			LegacyGunGL.glPopMatrix(); /// LATCH POP ///
			
			LegacyGunGL.glPushMatrix(); /// DRUM PUSH ///
			LegacyGunGL.glTranslated(0, 1, 0);
			LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("DRUM", index)[2] * 60, 0, 0, 1);
			LegacyGunGL.glTranslated(0, -1, 0);
			LegacyGunGL.glTranslated(0, 0, HbmAnimations.getRelevantTransformation("DRUM_PUSH", index)[2]);
			PistolModels.bio_revolver.renderPart("Drum");
			LegacyGunGL.glPopMatrix(); /// DRUM POP ///
			
			LegacyGunGL.glPopMatrix(); /// FRONT POP ///
			
			LegacyGunGL.glPushMatrix(); /// HAMMER ///
			LegacyGunGL.glTranslated(0, 0, -4.5);
			LegacyGunGL.glRotated(-45 + 45 * HbmAnimations.getRelevantTransformation("HAMMER", index)[2], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 4.5);
			PistolModels.bio_revolver.renderPart("Hammer");
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.5, 9.25);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			this.renderMuzzleFlash(gun.lastShot[index], 75, 7.5);
			LegacyGunGL.glPopMatrix();

			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, 1, 3);
	}

	@Override
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPersonAkimbo(stack);
		LegacyGunGL.glTranslated(0, 1, 3);
	}

	@Override
	public void setupInv(ItemStack stack) {
		LegacyGunGL.glScaled(1, 1, -1);
		LegacyGunGL.glTranslated(8, 6, 0);
		double scale = 1.125D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1.5, 0);
	}

	@Override
	public void renderInv(ItemStack stack) {
		
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(2, 0, 0);
		LegacyGunGL.texture(PistolModels.dani_celestial_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glTranslated(0, 0, 5);
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		LegacyGunGL.glTranslated(-2, 0, 0);
		LegacyGunGL.texture(PistolModels.dani_lunar_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderEquipped(ItemStack stack, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.dani_lunar_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		//Stopgap: at the moment, the flashMap is gun agnostic and does not care about index.
		LivingEntity ent = (LivingEntity) data[1]; //Entity is second obj. passed 
		long shot;
		if(ent == Minecraft.getInstance().player) {
			ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
			shot = gun.lastShot[1];
		} else {
			shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
			if(shot < 0) return;
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, 9.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderMuzzleFlash(shot, 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.dani_celestial_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		//Player is first & only object passed
		long shot;
		if(ent == Minecraft.getInstance().player) {
			ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
			shot = gun.lastShot[0];
		} else {
			shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
			if(shot < 0) return;
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, 9.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderMuzzleFlash(shot, 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(index == 1 ? PistolModels.dani_celestial_tex : PistolModels.dani_lunar_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	@Override
	public void renderEntity(ItemStack stack) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(-2, 1, 0);
		LegacyGunGL.texture(PistolModels.dani_lunar_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(2, 1, 0);
		LegacyGunGL.texture(PistolModels.dani_celestial_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.dani_celestial_tex);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
