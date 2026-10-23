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
public class ItemRenderHenry extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderHenry(ResourceLocation texture) {
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
				-1.25F * offset, -1F * offset, 1.75F * offset,
				0, -5 / 8D, 1);

		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		double r = -2.5 * aimingProgress;
		LegacyGunGL.glRotated(r, 1, 0, 0);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] sight = HbmAnimations.getRelevantTransformation("SIGHT");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] hammer = HbmAnimations.getRelevantTransformation("HAMMER");
		double[] lever = HbmAnimations.getRelevantTransformation("LEVER");
		double[] turn = HbmAnimations.getRelevantTransformation("TURN");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] twist = HbmAnimations.getRelevantTransformation("TWIST");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		double[] yeet = HbmAnimations.getRelevantTransformation("YEET");
		double[] roll = HbmAnimations.getRelevantTransformation("ROLL");

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		LegacyGunGL.glTranslated(recoil[0] * 2, recoil[1], recoil[2]);
		LegacyGunGL.glRotated(recoil[2] * 5, 1, 0, 0);
		LegacyGunGL.glRotated(turn[2], 0, 0, 1);
		
		LegacyGunGL.glTranslated(yeet[0], yeet[1], yeet[2]);
		
		LegacyGunGL.glTranslated(0, 1, 0);
		LegacyGunGL.glRotated(roll[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1, 0);

		LegacyGunGL.glTranslated(0, -4, 4);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 4, -4);

		LegacyGunGL.glTranslated(0, 2, -4);
		LegacyGunGL.glRotated(equip[0], -1, 0, 0);
		LegacyGunGL.glTranslated(0, -2, 4);
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 8);
		LegacyGunGL.glRotated(turn[2], 0, 0, -1);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.25D);
		LegacyGunGL.glPopMatrix();
		
		PistolModels.henry.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.25, -0.1875);
		LegacyGunGL.glRotated(sight[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1.25, 0.1875);
		PistolModels.henry.renderPart("Sight");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.625, -3);
		LegacyGunGL.glRotated(-30 + hammer[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.625, 3);
		PistolModels.henry.renderPart("Hammer");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.25, -2.3125);
		LegacyGunGL.glRotated(lever[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.25, 2.3125);
		PistolModels.henry.renderPart("Lever");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 0);
		LegacyGunGL.glRotated(twist[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1, 0);
		PistolModels.henry.renderPart("Front");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(bullet[0], bullet[1], bullet[2] - 1);
		PistolModels.henry.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.25, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
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
		LegacyGunGL.texture(texture);
		PistolModels.henry.renderAll();
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
			LegacyGunGL.glTranslated(0, 1, 8);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			this.renderMuzzleFlash(shot, 75, 5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
