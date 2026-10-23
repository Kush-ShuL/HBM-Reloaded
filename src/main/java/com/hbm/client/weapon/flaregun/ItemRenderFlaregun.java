package com.hbm.client.weapon.flaregun;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.world.item.ItemStack;
/** Every original Flaregun OBJ transform, moving part and smoke ribbon. */
public class ItemRenderFlaregun extends LegacyGunRenderer {

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
				-1.25F * offset, -1.5F * offset, 2F * offset,
				0, -5.5 / 8D, 0.5);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(FlaregunModels.flaregun_tex);
		double scale = 0.125D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER");
		double[] open = HbmAnimations.getRelevantTransformation("OPEN");
		double[] shell = HbmAnimations.getRelevantTransformation("SHELL");
		double[] flip = HbmAnimations.getRelevantTransformation("FLIP");
		
		LegacyGunGL.glTranslated(recoil[0], recoil[1], recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 10, 1, 0, 0);
		LegacyGunGL.glRotated(flip[0], 1, 0, 0);

		LegacyGunGL.glTranslated(0, 0, -8);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 8);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		FlaregunModels.flaregun.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.8125, -4);
		LegacyGunGL.glRotated(hammer[0] - 15, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.8125, 4);
		FlaregunModels.flaregun.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 2.156, 1.78);
		LegacyGunGL.glRotated(open[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2.156, -1.78);
		FlaregunModels.flaregun.renderPart("Barrel");
		LegacyGunGL.glTranslated(shell[0], shell[1], shell[2]);
		FlaregunModels.flaregun.renderPart("Flare");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.5;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 4, 9);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 2.5D);
		LegacyGunGL.glTranslated(0, 0, 0.1);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 2D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.5, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void setupEntity(ItemStack stack) {
		super.setupEntity(stack);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		// Native entity-cutout renderer retains the original lit model.
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(FlaregunModels.flaregun_tex);
		FlaregunModels.flaregun.renderAll();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
}
