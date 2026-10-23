// Original X5846 renderer matrices, hierarchy and all part calls retained.
package com.hbm.client.weapon.energy;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.energy.EnergyContent;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderLaserPistol extends EnergyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderLaserPistol(ResourceLocation texture) {
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
				-1.75F * offset, -2F * offset, 2.75F * offset,
				0, -10 / 8D, 1.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(texture);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);
		
		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] latch = HbmAnimations.getRelevantTransformation("LATCH");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] jolt = HbmAnimations.getRelevantTransformation("JOLT");
		double[] battery = HbmAnimations.getRelevantTransformation("BATTERY");
		double[] swirl = HbmAnimations.getRelevantTransformation("SWIRL");
		
		LegacyGunGL.glTranslated(0, -1, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 6);
		
		LegacyGunGL.glTranslated(0, 2, -2);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -2, 2);
		
		LegacyGunGL.glTranslated(0, -1, -1);
		LegacyGunGL.glRotated(swirl[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 1);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		LegacyGunGL.glTranslated(jolt[0], jolt[1], jolt[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		EnergyModels.laser_pistol.renderPart("Gun");
		if(hasCapacitors(stack)) EnergyModels.laser_pistol.renderPart("Capacitors");
		if(hasTape(stack)) EnergyModels.laser_pistol.renderPart("Tape");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(1.125, 0, -1.9125);
		LegacyGunGL.glRotated(latch[1], 0, 1, 0);
		LegacyGunGL.glTranslated(-1.125, 0, 1.9125);
		EnergyModels.laser_pistol.renderPart("Latch");
		LegacyGunGL.glTranslated(battery[0], battery[1], battery[2]);
		EnergyModels.laser_pistol.renderPart("Battery");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 2, 4.75);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		renderLaserFlash(gun.lastShot[0], 150, 1.5D, hasEmerald(stack) ? 0x008000 : 0xff0000);
		LegacyGunGL.glTranslated(0, 0, -0.25);
		renderLaserFlash(gun.lastShot[0], 150, 0.75D, hasEmerald(stack) ? 0x80ff00 : 0xff8000);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.5, 1);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -10D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		EnergyModels.laser_pistol.renderPart("Gun");
		EnergyModels.laser_pistol.renderPart("Latch");
		if(hasCapacitors(stack)) EnergyModels.laser_pistol.renderPart("Capacitors");
		if(hasTape(stack)) EnergyModels.laser_pistol.renderPart("Tape");
		
		if(type == ItemRenderType.EQUIPPED) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			if(ent == Minecraft.getInstance().player) {
				ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
				shot = gun.lastShot[0];
			} else {
				shot = flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 2, 4.75);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			renderLaserFlash(shot, 150, 1.5D, hasEmerald(stack) ? 0x008000 : 0xff0000);
			LegacyGunGL.glTranslated(0, 0, -0.25);
			renderLaserFlash(shot, 150, 0.75D, hasEmerald(stack) ? 0x80ff00 : 0xff8000);
			LegacyGunGL.glPopMatrix();
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public boolean hasCapacitors(ItemStack stack) {
		return stack.getItem() == EnergyContent.GUNS.get("gun_laser_pistol_pew_pew").get();
	}
	
	public boolean hasTape(ItemStack stack) {
		return stack.getItem() == EnergyContent.GUNS.get("gun_laser_pistol_pew_pew").get();
	}
	
	public boolean hasEmerald(ItemStack stack) {
		return stack.getItem() == EnergyContent.GUNS.get("gun_laser_pistol_morning_glory").get();
	}
}
