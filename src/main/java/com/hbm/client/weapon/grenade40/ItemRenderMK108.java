package com.hbm.client.weapon.grenade40;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
public class ItemRenderMK108 extends LegacyGunRenderer {

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
				-1F * offset, -1.5F * offset, 2.5F * offset,
				-0.75F, -0.75F, 1.5F);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(Grenade40Models.mk108_tex);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		boolean doesYeet = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("GRENH1") != null;
		boolean doesCycle = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("CYCLE") != null;
		boolean reloading = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("BELT") != null;
		boolean useShellCount = HbmAnimations.getRelevantAnim(0) != null && HbmAnimations.getRelevantAnim(0).animation.getBus("SHELLS") != null;
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE");
		double[] barrel = HbmAnimations.getRelevantTransformation("BARREL");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] lid = HbmAnimations.getRelevantTransformation("LID");
		double[] belt = HbmAnimations.getRelevantTransformation("BELT");
		double[] drum = HbmAnimations.getRelevantTransformation("DRUM");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] shellCount = HbmAnimations.getRelevantTransformation("SHELLS");
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		if(doesYeet) {
			double[][] horizontal = new double[][] {
				HbmAnimations.getRelevantTransformation("GRENH1"),
				HbmAnimations.getRelevantTransformation("GRENH2"),
				HbmAnimations.getRelevantTransformation("GRENH3"),
			};
			double[][] vertical = new double[][] {
				HbmAnimations.getRelevantTransformation("GRENV1"),
				HbmAnimations.getRelevantTransformation("GRENV2"),
				HbmAnimations.getRelevantTransformation("GRENV3"),
			};
			double[][] spin = new double[][] {
				HbmAnimations.getRelevantTransformation("GRENS1"),
				HbmAnimations.getRelevantTransformation("GRENS2"),
				HbmAnimations.getRelevantTransformation("GRENS3"),
			};
			
			for(int i = 0; i < 3; i++) {
				if(horizontal[i][0] <= -4) continue;
				LegacyGunGL.glPushMatrix();
				LegacyGunGL.glTranslated(horizontal[i][0], vertical[i][1], 0);
				LegacyGunGL.glTranslated(0, 0, -2.3125);
				LegacyGunGL.glRotated(-90, 1, 0, 0);
				LegacyGunGL.glRotated(-spin[i][0], 0, 1, 0);
				LegacyGunGL.glTranslated(0, 0, 2.3125);
				Grenade40Models.mk108.renderPart("Grenade");
				LegacyGunGL.glPopMatrix();
			}
		}
		
		LegacyGunGL.glTranslated(0, -1, -8);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 8);
		
		LegacyGunGL.glTranslated(0, 1, -4);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1, 4);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		Grenade40Models.mk108.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, barrel[2] * 2);
		Grenade40Models.mk108.renderPart("Barrel");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.6875, -1);
		LegacyGunGL.glRotated(lid[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.6875, 1);
		Grenade40Models.mk108.renderPart("Lid");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();

		LegacyGunGL.glTranslated(drum[0], drum[1], drum[2]);
		Grenade40Models.mk108.renderPart("Drum");
		
		double p = 0.0625D;
		double x = p * 22;
		double y = p * -46;
		double angle = 0;
		BeltVector vec = new BeltVector(0, 0.53125, 0);

		double[] anglesLoaded = new double[]   {0,   0,  -5,   0,   -5,  60,  45,  -10,   0};
		double[] anglesUnloaded = new double[] {0, -30, -60, -45, -45,   0,   0,   0,   0};
		double[][] shells = new double[anglesLoaded.length][3];
		double reloadProgress = !reloading ? 1D : belt[0];
		double cycleProgress = !doesCycle ? 1 : cycle[0];
		
		for(int i = 0; i < anglesLoaded.length; i++) {
			shells[i][0] = x;
			shells[i][1] = y;
			shells[i][2] = angle - 90;
			double delta = Grenade40Math.interp(anglesUnloaded[i], anglesLoaded[i], reloadProgress);
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
		LegacyGunGL.glTranslated(0, 0, 8.125);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 50, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.0D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(1, -2.5, 4);
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
		LegacyGunGL.glTranslated(0, 0.5, -0.25);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Grenade40Models.mk108_tex);
		Grenade40Models.mk108.renderPart("Gun");
		Grenade40Models.mk108.renderPart("Barrel");
		Grenade40Models.mk108.renderPart("Lid");
		Grenade40Models.mk108.renderPart("Drum");
		
		LegacyGunGL.glPushMatrix();
		
		double p = 0.0625D;
		double x = p * 22;
		double y = p * -46;
		double angle = 0;
		BeltVector vec = new BeltVector(0, 0.53125, 0);

		double[] anglesLoaded = new double[] { 0, 0, -5, 0, -5, 60, 45, -10, 0 };
		double[][] shells = new double[anglesLoaded.length][3];
		
		for(int i = 0; i < anglesLoaded.length; i++) {
			shells[i][0] = x;
			shells[i][1] = y;
			shells[i][2] = angle - 90;
			double delta = anglesLoaded[i];
			angle += delta;
			vec.rotateAroundZDeg(-delta);
			x += vec.xCoord;
			y += vec.yCoord;
		}
		
		// draw belt, interp used for cycling (shells will transform towards the position/rotation of the next shell)
		for(int i = 0; i < shells.length - 1; i++) {
			double[] prevShell = shells[i];
			double[] nextShell = shells[i + 1];
			renderShell(prevShell[0], nextShell[0], prevShell[1], nextShell[1], prevShell[2], nextShell[2], true, 0F);
		}
		LegacyGunGL.glPopMatrix();
		
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
			LegacyGunGL.glTranslated(0, 0, 8.125);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 50, 5);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public static void renderShell(double x0, double x1, double y0, double y1, double rot0, double rot1, boolean shell, double interp) {
		renderShell(Grenade40Math.interp(x0, x1, interp), Grenade40Math.interp(y0, y1, interp), Grenade40Math.interp(rot0, rot1, interp), shell);
	}
	
	public static void renderShell(double x, double y, double rot, boolean shell) {
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(x, y, 0);
		LegacyGunGL.glRotated(rot, 0, 0, 1);
		Grenade40Models.mk108.renderPart("Belt");
		if(shell) Grenade40Models.mk108.renderPart("Grenade");
		LegacyGunGL.glPopMatrix();
	}
}
