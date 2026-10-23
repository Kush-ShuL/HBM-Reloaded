// Mechanically retained original X5846 renderer; explicit PoseStack/mesh context replaces fixed-function GL.
package com.hbm.client.weapon.legendary;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunsContent;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;


public class ItemRenderHeavyRevolver extends LegacyGunRenderer {
	
	protected ResourceLocation texture;
	
	public ItemRenderHeavyRevolver(ResourceLocation texture) {
		this.texture = texture;
	}
	
	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 1);

		boolean isScoped = this.isScoped(stack);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.0F * offset, -0.75F * offset, 1F * offset,
				0, isScoped ? (-4.75 / 8D) : (-3.875 / 8D), isScoped ? -0.25 : 0);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		boolean isScoped = this.isScoped(stack);
		if(isScoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		double scale = 0.125D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		

		double[] equipSpin = HbmAnimations.getRelevantTransformation("ROTATE");
		double[] spin = HbmAnimations.getRelevantTransformation("SPIN");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] reloadLift = HbmAnimations.getRelevantTransformation("RELOAD_LIFT");
		double[] reloadJolt = HbmAnimations.getRelevantTransformation("RELOAD_JOLT");
		double[] reloadTilt = HbmAnimations.getRelevantTransformation("RELAOD_TILT");
		double[] cylinderFlip = HbmAnimations.getRelevantTransformation("RELOAD_CYLINDER");
		double[] reloadBullets = HbmAnimations.getRelevantTransformation("RELOAD_BULLETS");

		LegacyGunGL.glRotated(spin[0], 0, 0, 1);
		
		LegacyGunGL.glTranslated(6, -3, 0);
		LegacyGunGL.glRotated(equipSpin[0], 0, 0, 1);
		LegacyGunGL.glTranslated(-6, 3, 0);

		standardAimingTransform(stack, 0, 0, recoil[2], -recoil[2], 0, 0);
		LegacyGunGL.glRotated(recoil[2] * 10, 0, 0, 1);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-9, 2.5, 0);
		LegacyGunGL.glRotated(recoil[2] * -10, 0, 0, 1);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glRotated(reloadLift[0], 0, 0, 1);
		LegacyGunGL.glTranslated(reloadJolt[0], 0, 0);
		LegacyGunGL.glRotated(reloadTilt[0], 1, 0, 0);
		
		LegacyGunGL.texture(texture);
		LegendaryModels.lilmac.renderPart("Gun");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(cylinderFlip[0], 1, 0, 0);
		LegendaryModels.lilmac.renderPart("Pivot");
		LegacyGunGL.glTranslated(0, 1.75, 0);
		LegacyGunGL.glRotated(HbmAnimations.getRelevantTransformation("DRUM")[2] * -60, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.75, 0);
		LegendaryModels.lilmac.renderPart("Cylinder");
		LegacyGunGL.glTranslated(reloadBullets[0], reloadBullets[1], reloadBullets[2]);
		if(HbmAnimations.getRelevantTransformation("RELOAD_BULLETS_CON")[0] != 1)
		LegendaryModels.lilmac.renderPart("Bullets");
		LegendaryModels.lilmac.renderPart("Casings");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix(); /// HAMMER ///
		LegacyGunGL.glTranslated(4, 1.25, 0);
		LegacyGunGL.glRotated(-30 + 30 * HbmAnimations.getRelevantTransformation("HAMMER")[2], 0, 0, 1);
		LegacyGunGL.glTranslated(-4, -1.25, 0);
		LegendaryModels.lilmac.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();
		
		if(isScoped) {
			LegacyGunGL.texture(LegendaryModels.lilmac_scope_tex);
			LegendaryModels.lilmac.renderPart("Scope");
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0.125, 2.5, 0);
		this.renderGapFlash(gun.lastShot[0]);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		LegacyGunGL.glTranslated(0, 1, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		if(isScoped(stack)) {
			double scale = 1.125D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(0, -0.5, 0);
		} else {
			double scale = 1.25D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
		}
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {

		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glAlphaFunc(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		LegendaryModels.lilmac.renderPart("Gun");
		LegendaryModels.lilmac.renderPart("Cylinder");
		LegendaryModels.lilmac.renderPart("Bullets");
		LegendaryModels.lilmac.renderPart("Casings");
		LegendaryModels.lilmac.renderPart("Pivot");
		LegendaryModels.lilmac.renderPart("Hammer");
		if(isScoped(stack)) {
			LegacyGunGL.texture(LegendaryModels.lilmac_scope_tex);
			LegendaryModels.lilmac.renderPart("Scope");
		}
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
			LegacyGunGL.glTranslated(0.125, 2.5, 0);
			this.renderGapFlash(shot);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isScoped(ItemStack stack) {
		return stack.getItem() == LegendaryGunsContent.LILMAC.get() || XWeaponModManager.hasUpgrade(stack, 0, 202);
	}
}
