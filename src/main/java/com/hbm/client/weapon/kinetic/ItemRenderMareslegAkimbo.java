package com.hbm.client.weapon.kinetic;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderMareslegAkimbo extends LegacyGunRenderer {
	
	@Override public boolean isAkimbo(LivingEntity entity) { return true; }

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		float offset = 0.8F;
		
		for(int i = -1; i <= 1; i += 2) {
			
			LegacyGunGL.texture(KineticModels.maresleg_tex);
			LegacyGunGL.glPushMatrix();
			
			int index = i == -1 ? 0 : 1;
			
			standardAimingTransform(stack, -1.5F * offset * i, -1F * offset, 2F * offset, 0, -3.875 / 8D, 1);
			
			double scale = 0.375D;
			LegacyGunGL.glScaled(scale, scale, scale);
	
			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] lever = HbmAnimations.getRelevantTransformation("LEVER", index);
			double[] turn = HbmAnimations.getRelevantTransformation("TURN", index);
			double[] flip = HbmAnimations.getRelevantTransformation("FLIP", index);
			double[] lift = HbmAnimations.getRelevantTransformation("LIFT", index);
			double[] shell = HbmAnimations.getRelevantTransformation("SHELL", index);
			double[] flag = HbmAnimations.getRelevantTransformation("FLAG", index);
	
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
			
			LegacyGunGL.glTranslated(recoil[0] * 2, recoil[1], recoil[2]);
			LegacyGunGL.glRotated(recoil[2] * 5, 1, 0, 0);
			LegacyGunGL.glRotated(turn[2], 0, 0, 1);
	
			LegacyGunGL.glTranslated(0, 0, -4);
			LegacyGunGL.glRotated(lift[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 4);
	
			LegacyGunGL.glTranslated(0, 0, -4);
			LegacyGunGL.glRotated(equip[0], -1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 4);
	
			LegacyGunGL.glTranslated(0, 0, -2);
			LegacyGunGL.glRotated(flip[0], -1, 0, 0);
			LegacyGunGL.glTranslated(0, 0, 2);
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1, 3.75);
			LegacyGunGL.glRotated(turn[2], 0, 0, -1);
			LegacyGunGL.glRotated(flip[0], 1, 0, 0);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			this.renderSmokeNodes(gun.getConfig(stack, index).smokeNodes, 0.25D);
			LegacyGunGL.glPopMatrix();
	
			KineticModels.maresleg.renderPart("Gun");
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.125, -2.875);
			LegacyGunGL.glRotated(lever[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -0.125, 2.875);
			KineticModels.maresleg.renderPart("Lever");
			LegacyGunGL.glPopMatrix();
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(shell[0], shell[1] - 0.75, shell[2]);
			KineticModels.maresleg.renderPart("Shell");
			LegacyGunGL.glPopMatrix();
			
			if(flag[0] != 0) {
				LegacyGunGL.glPushMatrix();
				LegacyGunGL.glTranslated(0, -0.5, 0);
				KineticModels.maresleg.renderPart("Shell");
				LegacyGunGL.glPopMatrix();
			}
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1, 3.75);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
			this.renderMuzzleFlash(gun.lastShot[index], 75, 5);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 3);
	}

	@Override
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPersonAkimbo(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 3);
	}

	@Override
	public void setupInv(ItemStack stack) {
		LegacyGunGL.glAlphaFunc(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		LegacyGunGL.glScaled(1, 1, -1);
		LegacyGunGL.glTranslated(8, 8, 0);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -12.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 1);
	}

	@Override
	public void renderInv(ItemStack stack) {
		
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.texture(KineticModels.maresleg_tex);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-1, 0, 0);
		KineticModels.maresleg.renderPart("Gun");
		KineticModels.maresleg.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glTranslated(0, 0, 5);
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		LegacyGunGL.glTranslated(1, 0, 0);
		KineticModels.maresleg.renderPart("Gun");
		KineticModels.maresleg.renderPart("Lever");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		renderOther(stack, ItemRenderType.INVENTORY);
	}
	
	@Override
	public void renderEntity(ItemStack stack) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.texture(KineticModels.maresleg_tex);
		
		LegacyGunGL.glTranslated(-1, 1, 0);
		KineticModels.maresleg.renderPart("Gun");
		KineticModels.maresleg.renderPart("Lever");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1, 1, 0);
		KineticModels.maresleg.renderPart("Gun");
		KineticModels.maresleg.renderPart("Lever");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	@Override
	public void renderEquipped(ItemStack stack, Object... data) {
		renderOther(stack, ItemRenderType.EQUIPPED);
		//grumble grumble
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
		LegacyGunGL.glTranslated(0, 1, 3.75);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
		this.renderMuzzleFlash(shot, 75, 5);
		LegacyGunGL.glPopMatrix();
	}
	
	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {
		renderOther(stack, ItemRenderType.EQUIPPED);
		
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
		LegacyGunGL.glTranslated(0, 1, 3.75);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
		this.renderMuzzleFlash(shot, 75, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(KineticModels.maresleg_tex);
		KineticModels.maresleg.renderPart("Gun");
		KineticModels.maresleg.renderPart("Lever");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
