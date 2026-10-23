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


public class ItemRenderMaresleg extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderMaresleg(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

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
				-1.25F * offset, -1F * offset, 2F * offset,
				0, -3.875 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		boolean shortened = getShort(stack);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] lever = HbmAnimations.getRelevantTransformation("LEVER");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] flip = HbmAnimations.getRelevantTransformation("FLIP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] shell = HbmAnimations.getRelevantTransformation("SHELL");
		double[] flag = HbmAnimations.getRelevantTransformation("FLAG");

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
		LegacyGunGL.glTranslated(0, 1, shortened ? 3.75 : 8);
		LegacyGunGL.glRotated(turn[2], 0, 0, -1);
		LegacyGunGL.glRotated(flip[0], 1, 0, 0);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.25D);
		LegacyGunGL.glPopMatrix();

		LegendaryModels.maresleg.renderPart("Gun");
		if(!shortened) {
			LegendaryModels.maresleg.renderPart("Stock");
			LegendaryModels.maresleg.renderPart("Barrel");
		}

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.125, -2.875);
		LegacyGunGL.glRotated(lever[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.125, 2.875);
		LegendaryModels.maresleg.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell[0], shell[1] - 0.75, shell[2]);
		LegendaryModels.maresleg.renderPart("Shell");
		LegacyGunGL.glPopMatrix();
		
		if(flag[0] != 0) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, -0.5, 0);
			LegendaryModels.maresleg.renderPart("Shell");
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, shortened ? 3.75 : 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		
		if(getShort(stack)) {
			double scale = 2.5D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-1, 0, 0);
		} else {
			double scale = 1.4375D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(45, 0, 1, 0);
			LegacyGunGL.glTranslated(-0.5, 0.5, 0);
		}
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		LegendaryModels.maresleg.renderPart("Gun");
		LegendaryModels.maresleg.renderPart("Lever");
		boolean shortened = getShort(stack);
		if(!shortened) {
			LegendaryModels.maresleg.renderPart("Stock");
			LegendaryModels.maresleg.renderPart("Barrel");
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
			LegacyGunGL.glTranslated(0, 1, shortened ? 3.75 : 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean getShort(ItemStack stack) {
		return stack.getItem() == LegendaryGunsContent.BROKEN.get() || XWeaponModManager.hasUpgrade(stack, 0, 203);
	}
}
