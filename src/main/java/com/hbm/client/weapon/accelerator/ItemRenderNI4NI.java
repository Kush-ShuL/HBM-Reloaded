package com.hbm.client.weapon.accelerator;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.accelerator.ItemGunNI4NI;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
/** Source renderer matrices/part hierarchy retained; color/lighting states drive actual modern meshes. */
public class ItemRenderNI4NI extends com.hbm.client.weapon.energy.EnergyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 1);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.0F * offset, -1F * offset, 1F * offset,
				0, -5 / 8D, 0.125);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
        AcceleratorDraw.reset();

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		int[] color = ItemGunNI4NI.getColors(stack);
		int dark = 0xffffff;
		int light = 0xffffff;
		int grip = 0xffffff;
		if(color != null) {
			LegacyGunGL.texture(AcceleratorModels.n_i_4_n_i_greyscale_tex);
			dark = color[0];
			light = color[1];
			grip = color[2];
		} else {
			LegacyGunGL.texture(AcceleratorModels.n_i_4_n_i_tex);
		}
		
		double scale = 0.3125D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] drum = HbmAnimations.getRelevantTransformation("DRUM");
		
		LegacyGunGL.glTranslated(0, 0, -2.25);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 2.25);
		
		LegacyGunGL.glTranslated(0, -1, -6);
		LegacyGunGL.glRotated(recoil[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 6);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glPushMatrix();

		AcceleratorDraw.color(AcceleratorDraw.fr(dark), AcceleratorDraw.fg(dark), AcceleratorDraw.fb(dark));
		AcceleratorModels.n_i_4_n_i.renderPart("FrameDark");
		
		AcceleratorDraw.color(AcceleratorDraw.fr(grip), AcceleratorDraw.fg(grip), AcceleratorDraw.fb(grip));
		AcceleratorModels.n_i_4_n_i.renderPart("Grip");
		
		AcceleratorDraw.color(AcceleratorDraw.fr(light), AcceleratorDraw.fg(light), AcceleratorDraw.fb(light));
		AcceleratorModels.n_i_4_n_i.renderPart("FrameLight");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.1875D, 0);
		LegacyGunGL.glRotated(drum[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1.1875D, 0);
		AcceleratorModels.n_i_4_n_i.renderPart("Cylinder");
		AcceleratorDraw.fullbright(true);
		AcceleratorDraw.color(1F, 1F, 1F);
		AcceleratorModels.n_i_4_n_i.renderPart("CylinderHighlights");
		AcceleratorDraw.fullbright(false);
		LegacyGunGL.glPopMatrix();
		
		AcceleratorDraw.fullbright(true);
		AcceleratorModels.n_i_4_n_i.renderPart("Barrel");
		
		AcceleratorDraw.textured(false);
		int coinCount = ItemGunNI4NI.getCoinCount(stack);
		if(coinCount > 3) { AcceleratorDraw.color(coinCount > 7 ? 1F : 0F, 1F, 0F); AcceleratorModels.n_i_4_n_i.renderPart("Coin1"); }
		if(coinCount > 2) { AcceleratorDraw.color(coinCount > 6 ? 1F : 0F, 1F, 0F); AcceleratorModels.n_i_4_n_i.renderPart("Coin2"); }
		if(coinCount > 1) { AcceleratorDraw.color(coinCount > 5 ? 1F : 0F, 1F, 0F); AcceleratorModels.n_i_4_n_i.renderPart("Coin3"); }
		if(coinCount > 0) { AcceleratorDraw.color(coinCount > 4 ? 1F : 0F, 1F, 0F); AcceleratorModels.n_i_4_n_i.renderPart("Coin4"); }
		
		AcceleratorDraw.textured(true);
		
		AcceleratorDraw.fullbright(false);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.75, 4);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.125, 0.125, 0.125);
		this.renderLaserFlash(gun.lastShot[0], 75, 7.5, 0xFFFFFF);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, 0.25, 3);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -15D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
        AcceleratorDraw.reset();

		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		AcceleratorDraw.alpha(LegacyGunGL.GL_GREATER, 0F);
		LegacyGunGL.glEnable(LegacyGunGL.GL_ALPHA_TEST);
		
		int[] color = ItemGunNI4NI.getColors(stack);
		int dark = 0xffffff;
		int light = 0xffffff;
		int grip = 0xffffff;
		if(color != null) {
			LegacyGunGL.texture(AcceleratorModels.n_i_4_n_i_greyscale_tex);
			dark = color[0];
			light = color[1];
			grip = color[2];
		} else {
			LegacyGunGL.texture(AcceleratorModels.n_i_4_n_i_tex);
		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		AcceleratorDraw.color(AcceleratorDraw.fr(light), AcceleratorDraw.fg(light), AcceleratorDraw.fb(light));
		AcceleratorModels.n_i_4_n_i.renderPart("FrameLight");
		AcceleratorModels.n_i_4_n_i.renderPart("Cylinder");
		AcceleratorDraw.color(AcceleratorDraw.fr(grip), AcceleratorDraw.fg(grip), AcceleratorDraw.fb(grip));
		AcceleratorModels.n_i_4_n_i.renderPart("Grip");
		AcceleratorDraw.color(AcceleratorDraw.fr(dark), AcceleratorDraw.fg(dark), AcceleratorDraw.fb(dark));
		AcceleratorModels.n_i_4_n_i.renderPart("FrameDark");
		AcceleratorDraw.color(1F, 1F, 1F);
		AcceleratorDraw.fullbright(true);
		AcceleratorModels.n_i_4_n_i.renderPart("CylinderHighlights");
		AcceleratorModels.n_i_4_n_i.renderPart("Barrel");
		AcceleratorDraw.textured(false);
		AcceleratorDraw.color(0F, 1F, 0F);
		AcceleratorModels.n_i_4_n_i.renderPart("Coin1");
		AcceleratorModels.n_i_4_n_i.renderPart("Coin2");
		AcceleratorModels.n_i_4_n_i.renderPart("Coin3");
		AcceleratorModels.n_i_4_n_i.renderPart("Coin4");
		AcceleratorDraw.color(1F, 1F, 1F);
		AcceleratorDraw.textured(true);
		AcceleratorDraw.fullbright(false);
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
				shot = com.hbm.client.weapon.energy.EnergyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0.75, 4);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.125, 0.125, 0.125);
			this.renderLaserFlash(shot, 75, 7.5, 0xFFFFFF);
			LegacyGunGL.glPopMatrix();
		}
	}
}
