package com.hbm.client.weapon.rifle762;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.rifle762.Rifle762Guns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderMinigun extends com.hbm.client.weapon.energy.EnergyGunRenderer {
	
	protected ResourceLocation texture;
	
	public ItemRenderMinigun(ResourceLocation texture) {
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
				-1.75F * offset, -1.75F * offset, 3.5F * offset,
			0, -6.25 / 8D, 1);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] rotate = HbmAnimations.getRelevantTransformation("ROTATE");
		
		LegacyGunGL.glTranslated(0, 3, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -3, 6);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		Rifle762Models.minigun.renderPart("Gun");
		Rifle762Models.minigun.renderPart("Grip");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(rotate[2], 0, 0, 1);
		Rifle762Models.minigun.renderPart("Barrels");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.5;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-2, 1.25, -3.5);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 12);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		
		if(stack.getItem() == Rifle762Guns.gun_minigun_lacunae) {
			renderLaserFlash(gun.lastShot[0], 50, 1D, 0xff00ff);
			LegacyGunGL.glTranslated(0, 0, -0.25);
			renderLaserFlash(gun.lastShot[0], 50, 0.5D, 0xff0080);
		} else {
			LegacyGunGL.glTranslated(0, 0.5, 0);
			LegacyGunGL.glRotated(gun.shotRand * 90, 1, 0, 0);
			LegacyGunGL.glScaled(1.5, 1.5, 1.5);
			this.renderMuzzleFlash(gun.lastShot[0], 50, 7.5);
		}
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(1, -3.5, 8);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 0.875D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-0.25, 0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -6.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		Rifle762Models.minigun.renderPart("Gun");
		Rifle762Models.minigun.renderPart("Grip");
		Rifle762Models.minigun.renderPart("Barrels");
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
			LegacyGunGL.glTranslated(0, 0, 12.25);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			
			if(stack.getItem() == Rifle762Guns.gun_minigun_lacunae) {
				renderLaserFlash(shot, 50, 1D, 0xff00ff);
				LegacyGunGL.glTranslated(0, 0, -0.25);
				renderLaserFlash(shot, 50, 0.5D, 0xff0080);
			} else {
				LegacyGunGL.glTranslated(0, 0.5, 0);
				LegacyGunGL.glRotated(shotRand * 90, 1, 0, 0);
				LegacyGunGL.glScaled(1.5, 1.5, 1.5);
				this.renderMuzzleFlash(shot, 50, 7.55);
			}
			LegacyGunGL.glPopMatrix();
		}
	}
}
