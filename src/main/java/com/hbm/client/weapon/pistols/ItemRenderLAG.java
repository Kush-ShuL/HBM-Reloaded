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
public class ItemRenderLAG extends LegacyGunRenderer {

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
				-1.5F * offset, -1F * offset, 1.5F * offset,
				0, -3.375 / 8D, 0.5);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(PistolModels.mike_hawk_tex);
		double scale = 0.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		//double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER");
		double[] addTrans = HbmAnimations.getRelevantTransformation("ADD_TRANS");
		double[] addRot = HbmAnimations.getRelevantTransformation("ADD_ROT");
		//Animation anim = HbmAnimations.getRelevantAnim(0);
		
		LegacyGunGL.glTranslated(4, -4, 0);
		LegacyGunGL.glRotated(-equip[0], 0, 0, 1);
		LegacyGunGL.glTranslated(-4, 4, 0);

		LegacyGunGL.glTranslated(addTrans[0], addTrans[1], addTrans[2]);
		LegacyGunGL.glRotated(addRot[2], 0, 0, 1);
		LegacyGunGL.glRotated(addRot[1], 0, 1, 0);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Grip");
		PistolModels.mike_hawk.renderPart("Grip");
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Slide");
		
		/*if(anim != null) {
			BusAnimationSequence slideSeq = anim.animation.getBus("Hammer");
			if(slideSeq != null) LegacyGunGL.glTranslated(0, 0.75, 0);
		}*/

		PistolModels.mike_hawk.renderPart("Slide");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(3.125, 0.125, 0);
		LegacyGunGL.glRotated(-25, 0, 0, 1);
		LegacyGunGL.glTranslated(-3.125, -0.125, 0);
		HbmAnimations.applyRelevantTransformation("Hammer");
		PistolModels.mike_hawk.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();
		
		if(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null) > 0) {
			LegacyGunGL.glPushMatrix();
			HbmAnimations.applyRelevantTransformation("Bullet");
			PistolModels.mike_hawk.renderPart("Bullet");
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Magazine");
		PistolModels.mike_hawk.renderPart("Magazine");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.5;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-10.25, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-10.25, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();
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
		LegacyGunGL.glTranslated(2.5, 1, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0.5, -2);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.mike_hawk_tex);
		PistolModels.mike_hawk.renderPart("Grip");
		PistolModels.mike_hawk.renderPart("Slide");
		PistolModels.mike_hawk.renderPart("Hammer");
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
			LegacyGunGL.glTranslated(-10.25, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
