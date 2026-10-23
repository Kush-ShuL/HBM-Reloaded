package com.hbm.client.weapon.rifle556;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.rifle556.Rifle556Guns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderSTG77 extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 0.5F : -0.25F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.5F * offset, -1F * offset, 2.5F * offset,
			0, -5.75 / 8D, 2);
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.66F);
	}

	@Override
	protected float getBaseFOV(ItemStack stack) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return 70F - aimingProgress * 65;
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		if(ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(Rifle556Models.stg77_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] bolt = HbmAnimations.getRelevantTransformation("BOLT");
		double[] handle = HbmAnimations.getRelevantTransformation("HANDLE");
		double[] safety = HbmAnimations.getRelevantTransformation("SAFETY");

		double[] inspectGun = HbmAnimations.getRelevantTransformation("INSPECT_GUN");
		double[] inspectBarrel = HbmAnimations.getRelevantTransformation("INSPECT_BARREL");
		double[] inspectMove = HbmAnimations.getRelevantTransformation("INSPECT_MOVE");
		double[] inspectLever = HbmAnimations.getRelevantTransformation("INSPECT_LEVER");
		
		LegacyGunGL.glTranslated(0, -1, -4);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 4);
		
		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();

		//LegacyGunGL.glRotated(-70, 0, 0, 1);
		//LegacyGunGL.glRotated(15, 1, 0, 0);
		LegacyGunGL.glRotated(inspectGun[2], 0, 0, 1);
		LegacyGunGL.glRotated(inspectGun[0], 1, 0, 0);
		
		HbmAnimations.applyRelevantTransformation("Gun");
		Rifle556Models.stg77.renderPart("Gun");

		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Magazine");
		Rifle556Models.stg77.renderPart("Magazine");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(inspectLever[2], 0, 0, 1);
		HbmAnimations.applyRelevantTransformation("Lever");
		Rifle556Models.stg77.renderPart("Lever");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, bolt[2]);
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Breech");
		Rifle556Models.stg77.renderPart("Breech");
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glTranslated(0.125, 0, 0);
		LegacyGunGL.glRotated(handle[2], 0, 0, 1);
		LegacyGunGL.glTranslated(-0.125, 0, 0);
		HbmAnimations.applyRelevantTransformation("Handle");
		Rifle556Models.stg77.renderPart("Handle");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(safety[0], 0, 0);
		HbmAnimations.applyRelevantTransformation("Safety");
		Rifle556Models.stg77.renderPart("Safety");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		//LegacyGunGL.glTranslated(2, 0.75, 0);
		//LegacyGunGL.glRotated(15, 1, 0, 0);
		//LegacyGunGL.glRotated(0, 0, 0, 1);
		
		LegacyGunGL.glTranslated(inspectMove[0], inspectMove[1], inspectMove[2]);
		LegacyGunGL.glRotated(inspectBarrel[0], 1, 0, 0);
		LegacyGunGL.glRotated(inspectBarrel[2], 0, 0, 1);
		HbmAnimations.applyRelevantTransformation("Gun");
		HbmAnimations.applyRelevantTransformation("Barrel");
		Rifle556Models.stg77.renderPart("Barrel");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.75;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 7.5);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(0.25, 0.25, 0.25);
		LegacyGunGL.glRotated(-5 + gun.shotRand * 10, 1, 0, 0);
		this.renderGapFlash(gun.lastShot[0]);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 1, 2);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle556Models.stg77_tex);
		Rifle556Models.stg77.renderPart("Gun");
		Rifle556Models.stg77.renderPart("Barrel");
		Rifle556Models.stg77.renderPart("Lever");
		Rifle556Models.stg77.renderPart("Magazine");
		Rifle556Models.stg77.renderPart("Safety");
		Rifle556Models.stg77.renderPart("Handle");
		Rifle556Models.stg77.renderPart("Breech");
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
			LegacyGunGL.glTranslated(0, 0, 7.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(0.25, 0.25, 0.25);
			LegacyGunGL.glRotated(-5 + shotRand * 10, 1, 0, 0);
			this.renderGapFlash(shot);
			LegacyGunGL.glPopMatrix();
		}
	}
}
