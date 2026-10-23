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
public class ItemRenderAm180 extends LegacyGunRenderer {

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
				-1F * offset, -1F * offset, 1F * offset,
				0, -4.1875 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(PistolModels.am180_tex);
		double scale = 0.1875D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		boolean silenced = this.hasSilencer(stack);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] magazine = HbmAnimations.getRelevantTransformation("MAG");
		double[] magTurn = HbmAnimations.getRelevantTransformation("MAGTURN");
		double[] magSpin = HbmAnimations.getRelevantTransformation("MAGSPIN");
		double[] bolt = HbmAnimations.getRelevantTransformation("BOLT");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		
		LegacyGunGL.glTranslated(0, -2, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 6);

		LegacyGunGL.glRotated(turn[2], 0, 0, 1);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		HbmAnimations.applyRelevantTransformation("Gun");
		PistolModels.am180.renderPart("Gun");
		if(silenced) PistolModels.am180.renderPart("Silencer");

		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("Trigger");
		PistolModels.am180.renderPart("Trigger");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, bolt[2]);
		HbmAnimations.applyRelevantTransformation("Bolt");
		PistolModels.am180.renderPart("Bolt");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(magazine[0], magazine[1], magazine[2]);

		LegacyGunGL.glTranslated(0, 2.0625, 3.75);
		LegacyGunGL.glRotated(magTurn[0], 1, 0, 0);
		LegacyGunGL.glRotated(magTurn[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -2.0625, -3.75);

		LegacyGunGL.glTranslated(0, 2.3125, 1.5);
		LegacyGunGL.glRotated(magSpin[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2.3125, -1.5);

		HbmAnimations.applyRelevantTransformation("Mag");
		
		LegacyGunGL.glPushMatrix();
		int mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		LegacyGunGL.glTranslated(0, 0, 1.5);
		LegacyGunGL.glRotated(mag / 59D * 360D, 0, -1, 0);
		LegacyGunGL.glTranslated(0, 0, -1.5);
		PistolModels.am180.renderPart("Mag");
		LegacyGunGL.glPopMatrix();
		
		PistolModels.am180.renderPart("MagPlate");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.875, silenced ? 17 : 13);
		LegacyGunGL.glRotated(turn[2], 0, 0, -1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.25D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.875, silenced ? 16.75 : 12);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		double flashScale = silenced ? 0.5 : 0.75;
		LegacyGunGL.glScaled(flashScale, flashScale, flashScale);
		this.renderMuzzleFlash(gun.lastShot[0], silenced ? 75 : 50, silenced ? 5 : 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.5, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(1.5, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, -2);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(PistolModels.am180_tex);
		PistolModels.am180.renderPart("Gun");
		if(this.hasSilencer(stack)) PistolModels.am180.renderPart("Silencer");
		PistolModels.am180.renderPart("Trigger");
		PistolModels.am180.renderPart("Bolt");
		PistolModels.am180.renderPart("Mag");
		PistolModels.am180.renderPart("MagPlate");
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
			boolean silenced = this.hasSilencer(stack);
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1.875, silenced ? 16.75 : 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			double flashScale = silenced ? 0.5 : 0.75;
			LegacyGunGL.glScaled(flashScale, flashScale, flashScale);
			this.renderMuzzleFlash(shot, silenced ? 75 : 50, silenced ? 5 : 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean hasSilencer(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 201);
	}
}
