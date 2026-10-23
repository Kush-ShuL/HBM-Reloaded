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
public class ItemRenderAtlas extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderAtlas(ResourceLocation texture) {
		this.texture = texture;
	}

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
				-1.0F * offset, -0.75F * offset, 1F * offset,
				0, -3.125 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.125D;
		LegacyGunGL.glScaled(scale, scale, scale);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] reloadMove = HbmAnimations.getRelevantTransformation("RELOAD_MOVE");
		double[] reloadRot = HbmAnimations.getRelevantTransformation("RELOAD_ROT");
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		
		LegacyGunGL.glTranslated(recoil[0], recoil[1], recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 10, 1, 0, 0);

		LegacyGunGL.glTranslated(0, 0, -7);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 7);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, 9.25);
		LegacyGunGL.glRotated(-recoil[2] * 10, 1, 0, 0);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glTranslated(reloadMove[0], reloadMove[1], reloadMove[2]);

		LegacyGunGL.glRotated(reloadRot[0], 1, 0, 0);
		LegacyGunGL.glRotated(reloadRot[2], 0, 0, 1);
		LegacyGunGL.glRotated(reloadRot[1], 0, 1, 0);
		PistolModels.bio_revolver.renderPart("Grip");
		
		LegacyGunGL.glPushMatrix(); /// FRONT PUSH ///
		LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("FRONT")[2], 1, 0, 0);
		PistolModels.bio_revolver.renderPart("Barrel");
		LegacyGunGL.glPushMatrix(); /// LATCH PUSH ///
		LegacyGunGL.glTranslated(0, 2.3125, -0.875);
		LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("LATCH")[2], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2.3125, 0.875);
		PistolModels.bio_revolver.renderPart("Latch");
		LegacyGunGL.glPopMatrix(); /// LATCH POP ///
		
		LegacyGunGL.glPushMatrix(); /// DRUM PUSH ///
		LegacyGunGL.glTranslated(0, 1, 0);
		LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("DRUM")[2] * 60, 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1, 0);
		LegacyGunGL.glTranslated(0, 0, HbmAnimations.getRelevantTransformation("DRUM_PUSH")[2]);
		PistolModels.bio_revolver.renderPart("Drum");
		LegacyGunGL.glPopMatrix(); /// DRUM POP ///
		
		LegacyGunGL.glPopMatrix(); /// FRONT POP ///
		
		LegacyGunGL.glPushMatrix(); /// HAMMER ///
		LegacyGunGL.glTranslated(0, 0, -4.5);
		LegacyGunGL.glRotated(-45 + 45 * HbmAnimations.getRelevantTransformation("HAMMER")[2], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4.5);
		PistolModels.bio_revolver.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, 9.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 1, 3);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.125D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 1.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		PistolModels.bio_revolver.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED) {
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
			LegacyGunGL.glTranslated(0, 1.5, 9.25);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
