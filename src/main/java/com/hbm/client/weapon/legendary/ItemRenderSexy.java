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



public class ItemRenderSexy extends LegacyGunRenderer {
	
	protected ResourceLocation texture;
	
	public ItemRenderSexy(ResourceLocation texture) {
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
				-1F * offset, -0.75F * offset, 3F * offset,
				-0.5F, -0.5F, 2F);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		// i'm not going overboard with the animation
		boolean doesCycle = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("CYCLE") != null;
		boolean reloading = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("BELT") != null;
		boolean useShellCount = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("SHELLS") != null;
		boolean girldinner = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("BOTTLE") != null;
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lower = HbmAnimations.getRelevantTransformation("LOWER");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE");
		double[] barrel = HbmAnimations.getRelevantTransformation("BARREL");
		double[] hood = HbmAnimations.getRelevantTransformation("HOOD");
		double[] lever = HbmAnimations.getRelevantTransformation("LEVER");
		double[] belt = HbmAnimations.getRelevantTransformation("BELT");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] magRot = HbmAnimations.getRelevantTransformation("MAGROT");
		double[] shellCount = HbmAnimations.getRelevantTransformation("SHELLS");
		double[] bottle = HbmAnimations.getRelevantTransformation("BOTTLE");
		double[] sippy = HbmAnimations.getRelevantTransformation("SIP");
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		if(girldinner) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(bottle[0], bottle[1], bottle[2]);
			LegacyGunGL.glTranslated(0, 2, 0);
			LegacyGunGL.glRotated(sippy[0], 1, 0, 0);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(-15, 1, 0, 0);
			LegacyGunGL.glTranslated(0, -2, 0);
			LegacyGunGL.glScaled(1.5, 1.5, 1.5);
			LegacyGunGL.texture(LegendaryModels.whiskey_tex);
			LegendaryModels.whiskey.renderAll();
			LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.texture(texture);
		
		LegacyGunGL.glTranslated(0, -1, -8);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 8);
		
		LegacyGunGL.glTranslated(0, 0, -6);
		LegacyGunGL.glRotated(lower[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 6);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegendaryModels.sexy.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, barrel[2]);
		LegendaryModels.sexy.renderPart("Barrel");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, -0.375);
		LegacyGunGL.glScaled(1, 1, 1 + 0.457247371D * barrel[2]);
		LegacyGunGL.glTranslated(0, 0, 0.375);
		LegendaryModels.sexy.renderPart("RecoilSpring");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.4375, -2.875);
		LegacyGunGL.glRotated(hood[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.4375, 2.875);
		LegendaryModels.sexy.renderPart("Hood");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.46875, -6.875);
		LegacyGunGL.glRotated(lever[2] * 60, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.46875, 6.875);
		LegendaryModels.sexy.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, -6.75);
		LegacyGunGL.glScaled(1, 1, 1 - lever[2] * 0.25);
		LegacyGunGL.glTranslated(0, 0, 6.75);
		LegendaryModels.sexy.renderPart("LockSpring");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		LegacyGunGL.glTranslated(0, -1, 0);
		LegacyGunGL.glRotated(magRot[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, 1, 0);
		LegendaryModels.sexy.renderPart("Magazine");
		
		double p = 0.0625D;
		double x = p * 17;
		double y = p * -26;
		double angle = 0;
		BeltVector vec = new BeltVector(0, 0.4375, 0); // reusable, just like how toilet paper was reusable during corona

		// basically what all this does is take an array of angles and just strings together shells with the appropriate
		// position and angle calculated out of the next angle, taking all previous transformations into account.
		// has a second array which is the "open" position that the animation can smoothly interpolate through
		double[] anglesLoaded = new double[]   {0,   0,  20,  20,  50, 60, 70};
		double[] anglesUnloaded = new double[] {0, -10, -50, -60, -60,  0,  0};
		double reloadProgress = !reloading ? 1D : belt[0];
		double cycleProgress = !doesCycle ? 1 : cycle[0];
		
		double[][] shells = new double[anglesLoaded.length][3];
		
		// generate belt, interp used for the reload animation
		for(int i = 0; i < anglesLoaded.length; i++) {
			shells[i][0] = x;
			shells[i][1] = y;
			shells[i][2] = angle - 90;
			double delta = LegacyGunRenderer.lerp(anglesUnloaded[i], anglesLoaded[i], reloadProgress);
			angle += delta;
			vec.rotateAroundZDeg(-delta);
			x += vec.xCoord;
			y += vec.yCoord;
		}
		
		int shellAmount = useShellCount ? (int) shellCount[0] : gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null);
		
		// draw belt, interp used for cycling (shells will transform towards the position/rotation of the next shell)
		for(int i = 0; i < shells.length - 1; i++) {
			double[] prevShell = shells[i];
			double[] nextShell = shells[i + 1];
			renderShell(prevShell[0], nextShell[0], prevShell[1], nextShell[1], prevShell[2], nextShell[2], shells.length - i < shellAmount + 2, cycleProgress);
		}
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(1, 1, 6);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0.5, 0.25);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -9.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		LegendaryModels.sexy.renderPart("Gun");
		LegendaryModels.sexy.renderPart("Barrel");
		LegendaryModels.sexy.renderPart("RecoilSpring");
		LegendaryModels.sexy.renderPart("Hood");
		LegendaryModels.sexy.renderPart("Lever");
		LegendaryModels.sexy.renderPart("LockSpring");
		LegendaryModels.sexy.renderPart("Magazine");
		
		double p = 0.0625D;
		renderShell(p *  0, p *  -6,  90, true);
		renderShell(p *  5, p *   1,  30, true);
		renderShell(p * 12, p *  -1, -30, true);
		renderShell(p * 17, p *  -6, -60, true);
		renderShell(p * 17, p * -13, -90, true);
		renderShell(p * 17, p * -20, -90, true);
		
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
			LegacyGunGL.glTranslated(0, 0, 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 150, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public static void renderShell(double x0, double x1, double y0, double y1, double rot0, double rot1, boolean shell, double interp) {
		renderShell(LegacyGunRenderer.lerp(x0, x1, interp), LegacyGunRenderer.lerp(y0, y1, interp), LegacyGunRenderer.lerp(rot0, rot1, interp), shell);
	}
	
	public static void renderShell(double x, double y, double rot, boolean shell) {
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(x, 0.375 + y, 0);
		LegacyGunGL.glRotated(rot, 0, 0, 1);
		LegacyGunGL.glTranslated(0, -0.375, 0);
		LegendaryModels.sexy.renderPart("Belt");
		if(shell) LegendaryModels.sexy.renderPart("Shell");
		LegacyGunGL.glPopMatrix();
	}
}
