package com.hbm.client.weapon.fatman;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.fatman.FatmanBullets;
import net.minecraft.world.item.ItemStack;
/** Complete original mesh hierarchy, all six buses, pivots and first/third/inventory/mod-table matrices. */
public class ItemRenderFatMan extends LegacyGunRenderer {

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
				-1.5F * offset, -1.25F * offset, 0.5F * offset,
				-1F * offset, -1.25F * offset, 0F * offset);
	}
	
	protected static String label = "AUTO";
	
	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(FatmanModels.fatman_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		boolean isLoaded = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null) > 0;

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lid = HbmAnimations.getRelevantTransformation("LID");
		double[] nuke = HbmAnimations.getRelevantTransformation("NUKE");
		double[] piston = HbmAnimations.getRelevantTransformation("PISTON");
		double[] handle = HbmAnimations.getRelevantTransformation("HANDLE");
		double[] gauge = HbmAnimations.getRelevantTransformation("GAUGE");
		
		LegacyGunGL.glTranslated(0, 1, -2);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1, 2);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		FatmanModels.fatman.renderPart("Launcher");

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, handle[2]);
		FatmanModels.fatman.renderPart("Handle");
		
		LegacyGunGL.glTranslated(0.4375, -0.875, 0);
		LegacyGunGL.glRotated(gauge[2], 0, 0, 1);
		LegacyGunGL.glTranslated(-0.4375, 0.875, 0);
		FatmanModels.fatman.renderPart("Gauge");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0.25, 0.125, 0);
		LegacyGunGL.glRotated(lid[2], 0, 0, 1);
		LegacyGunGL.glTranslated(-0.25, -0.125, 0);
		FatmanModels.fatman.renderPart("Lid");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, piston[2]);
		if(!isLoaded && piston[2] == 0) LegacyGunGL.glTranslated(0, 0, 3);
		FatmanModels.fatman.renderPart("Piston");
		LegacyGunGL.glPopMatrix();

		if(isLoaded || nuke[0] != 0 || nuke[1] != 0 || nuke[2] != 0) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(nuke[0], nuke[1], nuke[2]);
			renderNuke(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getType(stack, null));
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(-0.5, 0.5, -3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
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

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		boolean isLoaded = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null) > 0;
		LegacyGunGL.texture(FatmanModels.fatman_tex);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		FatmanModels.fatman.renderPart("Launcher");
		FatmanModels.fatman.renderPart("Handle");
		FatmanModels.fatman.renderPart("Gauge");
		FatmanModels.fatman.renderPart("Lid");
		if(!isLoaded) LegacyGunGL.glTranslated(0, 0, 3);
		FatmanModels.fatman.renderPart("Piston");
		if(isLoaded) renderNuke(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getType(stack, null));
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public void renderNuke(Object type) {
		if(type == FatmanBullets.nuke_balefire) {
			renderBalefire(interp);
		} else {
			LegacyGunGL.texture(FatmanModels.fatman_mininuke_tex);
			FatmanModels.fatman.renderPart("MiniNuke");
		}
	}
	
	public static void renderBalefire(float interp) {var frame=LegacyGunGL.frame();FatmanGlint.render(frame.pose(),frame.light(),frame.overlay(),interp);}
}
