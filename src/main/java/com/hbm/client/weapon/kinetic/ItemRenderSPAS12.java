package com.hbm.client.weapon.kinetic;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderSPAS12 extends LegacyGunRenderer {

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
				-1.25F * offset, -1.75F * offset, -0.5F * offset,
				0, 0, 0);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(KineticModels.spas_12_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(180, 0, 1, 0);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		HbmAnimations.applyRelevantTransformation("MainBody");
		KineticModels.spas_12.renderPart("MainBody");
		
		LegacyGunGL.glPushMatrix();
		HbmAnimations.applyRelevantTransformation("PumpGrip");
		KineticModels.spas_12.renderPart("PumpGrip");
		LegacyGunGL.glPopMatrix();


		LegacyGunGL.glPushMatrix();
		LegacyGunGL.texture(KineticModels.casings_tex);

		HbmAnimations.applyRelevantTransformation("Shell");
		SpentCasing casing = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getCasing(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		int color0 = SpentCasing.COLOR_CASE_BRASS;
		int color1 = SpentCasing.COLOR_CASE_BRASS;
		
		if(casing != null) {
			int[] colors = casing.getColors();
			color0 = colors[0];
			color1 = colors[colors.length > 1 ? 1 : 0];
		}

        KineticModels.spas_12.renderPart("Shell",0xFF000000|color1);
        KineticModels.spas_12.renderPart("ShellFore",0xFF000000|color0);

		double smokeScale = 0.25;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, -11);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.75D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.5, -11);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.75, 0);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(4.25, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -10D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, -4.25);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);

		LegacyGunGL.glRotated(180, 0, 1, 0);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(KineticModels.spas_12_tex);
		KineticModels.spas_12.renderPart("MainBody");
		KineticModels.spas_12.renderPart("PumpGrip");
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
			LegacyGunGL.glTranslated(0, 1.5, -11);
			LegacyGunGL.glRotated(-90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
