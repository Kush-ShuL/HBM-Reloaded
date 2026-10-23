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
public class ItemRenderStarFAkimbo extends LegacyGunRenderer {
	
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
			LegacyGunGL.texture(PistolModels.star_f_elite_tex);

			LegacyGunGL.glPushMatrix();
			standardAimingTransform(stack, -2F * offset * i, -1.75F * offset, 2.5F * offset, 0, -7.625 / 8D, 1);

			double scale = 0.25D;
			LegacyGunGL.glScaled(scale, scale, scale);

			double[] equip = HbmAnimations.getRelevantTransformation("EQUIP", index);
			double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL", index);
			double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER", index);
			double[] tilt = HbmAnimations.getRelevantTransformation("TILT", index);
			double[] turn = HbmAnimations.getRelevantTransformation("TURN", index);
			double[] mag = HbmAnimations.getRelevantTransformation("MAG", index);
			double[] bullet = HbmAnimations.getRelevantTransformation("BULLET", index);
			double[] slide = HbmAnimations.getRelevantTransformation("SLIDE", index);

			LegacyGunGL.glTranslated(0, -2, -8);
			LegacyGunGL.glRotated(equip[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, 2, 8);

			LegacyGunGL.glTranslated(0, 1, -3);
			LegacyGunGL.glRotated(turn[2] * i, 0, 0, 1);
			LegacyGunGL.glRotated(tilt[0], 1, 0, 0);
			LegacyGunGL.glTranslated(0, -1, 3);
			
			LegacyGunGL.glTranslated(0, 0, recoil[2]);

			LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
			PistolModels.star_f.renderPart("Gun");

			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.75, -4.25);
			LegacyGunGL.glRotated(60 * (hammer[0] - 1), 1, 0, 0);
			LegacyGunGL.glTranslated(0, -1.75, 4.25);
			PistolModels.star_f.renderPart("Hammer");
			LegacyGunGL.glPopMatrix();

			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, slide[2] * 2.3125);
			PistolModels.star_f.renderPart("Slide");
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
			PistolModels.star_f.renderPart("Mag");
			LegacyGunGL.glTranslated(bullet[0], bullet[1], bullet[2]);
			PistolModels.star_f.renderPart("Bullet");
			LegacyGunGL.glPopMatrix();
			
			if(hasSilencer(stack, index)) {
				LegacyGunGL.glPushMatrix();
				LegacyGunGL.glTranslated(0, 2.375, -0.25);
				LegacyGunGL.texture(PistolModels.uzi_tex);
				PistolModels.uzi.renderPart("Silencer");
				LegacyGunGL.glPopMatrix();
				
			} else {
				double smokeScale = 0.5;
				
				LegacyGunGL.glPushMatrix();
				LegacyGunGL.glTranslated(0, 3, 6.125);
				LegacyGunGL.glRotated(90, 0, 1, 0);
				LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
				this.renderSmokeNodes(gun.getConfig(stack, index).smokeNodes, 0.75D);
				LegacyGunGL.glPopMatrix();
				
				LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
				
				renderMuzzleFlash(gun.shotRand, gun.lastShot[index]);
			}
			
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, -0.25, 1.75);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupThirdPersonAkimbo(ItemStack stack) {
		super.setupThirdPersonAkimbo(stack);
		LegacyGunGL.glTranslated(0, -0.25, 1.75);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
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
		LegacyGunGL.glTranslated(0, -0.25, -5);
	}

	@Override
	public void renderEquipped(ItemStack stack, Object... data) {
		renderStandardGun(stack, 1);
		if(!hasSilencer(stack, 1)) renderThirdPersonFlash((LivingEntity) data[1], stack, 1);
	}

	@Override
	public void renderEquippedAkimbo(ItemStack stack, LivingEntity ent) {
		renderStandardGun(stack, 0);
		if(!hasSilencer(stack, 0)) renderThirdPersonFlash(ent, stack, 0);
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.star_f_elite_tex);
		PistolModels.star_f.renderPart("Gun");
		PistolModels.star_f.renderPart("Slide");
		PistolModels.star_f.renderPart("Mag");
		PistolModels.star_f.renderPart("Hammer");
		if(hasSilencer(stack, index)) {
			LegacyGunGL.glTranslated(0, 2.375, -0.25);
			LegacyGunGL.texture(PistolModels.uzi_tex);
			PistolModels.uzi.renderPart("Silencer");
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	@Override
	public void renderEntity(ItemStack stack) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		boolean anySilenced = hasSilencer(stack, 0) || hasSilencer(stack, 1);
		
		if(anySilenced) {
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		}
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-1, 1, 0);
		renderStandardGun(stack, 1);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1, 1, 0);
		renderStandardGun(stack, 0);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		boolean anySilenced = hasSilencer(stack, 0) || hasSilencer(stack, 1);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0.5, 0, 0);
		if(anySilenced) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -4);
		}
		renderStandardGun(stack, 1);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glTranslated(0, 0, 5);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(225, 0, 0, 1);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(-90, 1, 0, 0);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(-45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 0, 0);
		if(anySilenced) {
			double scale = 0.625D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glTranslated(0, 0, -4);
		}
		renderStandardGun(stack, 0);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasSilencer(ItemStack stack, int cfg) {
		return XWeaponModManager.hasUpgrade(stack, cfg, 201);
	}
	
	public void renderStandardGun(ItemStack stack, int index) {

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.star_f_elite_tex);
		PistolModels.star_f.renderPart("Gun");
		PistolModels.star_f.renderPart("Slide");
		PistolModels.star_f.renderPart("Mag");
		PistolModels.star_f.renderPart("Hammer");
		boolean silenced = hasSilencer(stack, index);
		if(silenced) {
			LegacyGunGL.glTranslated(0, 2.375, -0.25);
			LegacyGunGL.texture(PistolModels.uzi_tex);
			PistolModels.uzi.renderPart("Silencer");
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public void renderThirdPersonFlash(Entity ent, ItemStack stack, int config) {
		
		long shot;
		double shotRand = 0;
		if(ent == Minecraft.getInstance().player) {
			ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
			shot = gun.lastShot[config];
			shotRand = gun.shotRand;
		} else {
			shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
			if(shot < 0) return;
		}
		
		renderMuzzleFlash(shotRand, shot);
	}
	
	public void renderMuzzleFlash(double shotRand, long shot) {
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 3, 6.125);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
		this.renderMuzzleFlash(shot, 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}
}
