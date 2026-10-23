package com.hbm.client.weapon.aberrator;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.aberrator.AberratorGuns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderEOTT extends LegacyGunRenderer {
	
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
		LegacyGunGL.glTranslated(0, 0, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		float offset = 0.8F;
		
		for(int i = -1; i <= 1; i += 2) {
			LegacyGunGL.texture(AberratorModels.eott_tex);
			int index = i == -1 ? 0 : 1;
			
			LegacyGunGL.glPushMatrix();
			standardAimingTransform(stack,
					-1.0F * offset * i, -1.25F * offset, 1.25F * offset,
					0, -5.25 / 8D, 0.125);
			
			double scale = 0.25D;
			LegacyGunGL.glScaled(scale, scale, scale);
	
			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			double[] rise = HbmAnimations.getRelevantTransformation("RISE", index);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", index);
			double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", index);
			double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", index);
			double[] roll = HbmAnimations.getRelevantTransformation("ROLL", index);
			double[] mag = HbmAnimations.getRelevantTransformation("MAG", index);
			double[] magroll = HbmAnimations.getRelevantTransformation("MAGROLL", index);
			double[] sight = HbmAnimations.getRelevantTransformation("SIGHT", index);
	
			LegacyGunGL.glTranslated(0, rise[1], 0);
			
			LegacyGunGL.glTranslated(0, 1, -2.25);
			LegacyGunGL.glRotated(equip[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -1, 2.25);
			
			LegacyGunGL.glTranslated(0, -1, -4);
			LegacyGunGL.glRotated(recoil[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 1, 4);
			
			LegacyGunGL.glTranslated(0, 1, 0);
			LegacyGunGL.glRotated(roll[2] * i, 0, 0, 1);
			LegacyGunGL.glTranslated(0, -1, 0);
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
			
			AberratorModels.aberrator.renderPart("Gun");
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2.4375, -1.9375);
			LegacyGunGL.glRotated(sight[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -2.4375, 1.9375);
			AberratorModels.aberrator.renderPart("Sight");
			LegacyGunGL.glPopMatrix();
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(mag[0] * i, mag[1], mag[2]);
			
			LegacyGunGL.glTranslated(0, 1, 0);
			LegacyGunGL.glRotated(magroll[2] * i, 0, 0, 1);
			LegacyGunGL.glTranslated(0, -1, 0);
			
			AberratorModels.aberrator.renderPart("Magazine");
			LegacyGunGL.glTranslated(bullet[0], bullet[1], bullet[2]);
			AberratorModels.aberrator.renderPart("Bullet");
			LegacyGunGL.glPopMatrix();
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, slide[2]);
			AberratorModels.aberrator.renderPart("Slide");
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.25, -3.625);
			LegacyGunGL.glRotated(-45 + hammer[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -1.25, 3.625);
			AberratorModels.aberrator.renderPart("Hammer");
			LegacyGunGL.glPopMatrix();
	
			double smokeScale = 0.5;
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2, 4);
			LegacyGunGL.glRotated(recoil[0], -1, 0, 0);
			LegacyGunGL.glRotated(roll[2] * i, 0, 0, -1);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
			this.renderSmokeNodes(gun.getConfig(stack, index).smokeNodes, 0.5D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2, 4);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(gun.lastShot[index], 75, 7.5);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2, -1.5);
			LegacyGunGL.glScaled(0.5, 0.5, 0.5);
			this.renderFireball(gun.lastShot[index]);
			LegacyGunGL.glPopMatrix();

			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, -1, 4);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}
	
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPersonAkimbo(stack);
		LegacyGunGL.glTranslated(0, -1, 4);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

	}

	@Override
	public void setupInv(ItemStack stack) {
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
		LegacyGunGL.glTranslated(0,-1, 0.5);
	}

	@Override
	public void renderInv(ItemStack stack) {
		
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.glTranslated(0, 1, 0);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-1, 0, 0);
		LegacyGunGL.texture(AberratorModels.eott_tex);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glTranslated(0, 0, 5);
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		LegacyGunGL.glTranslated(1, 0, 0);
		LegacyGunGL.texture(AberratorModels.eott_tex);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderEquipped(ItemStack stack, Object... data) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(AberratorModels.eott_tex);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
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
		LegacyGunGL.glTranslated(0, 2, 4);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(shot, 75, 7.5);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 2, -1.5);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderFireball(shot);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(AberratorModels.eott_tex);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
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
		LegacyGunGL.glTranslated(0, 2, 4);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(shot, 75, 7.5);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 2, -1.5);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderFireball(shot);
		LegacyGunGL.glPopMatrix();
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
		LegacyGunGL.texture(AberratorModels.eott_tex);
		
		LegacyGunGL.glTranslated(-1, 1, 0);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1, 1, 0);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
        try(var alpha=AberratorModels.alphaZero()){

		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glAlphaFunc(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(AberratorModels.eott_tex);
		AberratorModels.aberrator.renderPart("Gun");
		AberratorModels.aberrator.renderPart("Hammer");
		AberratorModels.aberrator.renderPart("Magazine");
		AberratorModels.aberrator.renderPart("Slide");
		AberratorModels.aberrator.renderPart("Sight");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
        }
}
	
    public static void renderFireball(long lastShot){AberratorVisuals.fireball(lastShot);}
}
