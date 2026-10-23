package com.hbm.client.weapon.rifle556;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.rifle556.Rifle556Guns;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
public class ItemRenderG3 extends LegacyGunRenderer {
	
	public ResourceLocation texture;
	
	public ItemRenderG3(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		boolean isScoped = this.isScoped(stack);
		float offset = 0.8F;
		standardAimingTransform(stack,
				-1.25F * offset, -1F * offset, 2.75F * offset,
			0, isScoped ? (-5.53125 / 8D) : (-3.5625 / 8D), isScoped ? 1.46875 : 1.75);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		boolean isScoped = this.isScoped(stack);
		if(isScoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(getTexture(stack));
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] speen = HbmAnimations.getRelevantTransformation("SPEEN");
		double[] bolt = HbmAnimations.getRelevantTransformation("BOLT");
		double[] plug = HbmAnimations.getRelevantTransformation("PLUG");
		double[] handle = HbmAnimations.getRelevantTransformation("HANDLE");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		
		LegacyGunGL.glTranslated(0, -2, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 6);
		
		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		Rifle556Models.g3.renderPart("Rifle");
		if(hasStock(stack)) Rifle556Models.g3.renderPart("Stock");
		boolean silenced = hasSilencer(stack);
		if(!silenced) Rifle556Models.g3.renderPart("Flash_Hider");
		Rifle556Models.g3.renderPart("Trigger");
		
		LegacyGunGL.texture(getTexture(stack));
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		LegacyGunGL.glTranslated(0, -1.75, -0.5);
		LegacyGunGL.glRotated(speen[2], 0, 0, 1);
		LegacyGunGL.glRotated(speen[1], 0, 1, 0);
		LegacyGunGL.glTranslated(0, 1.75, 0.5);
		Rifle556Models.g3.renderPart("Magazine");
		if(bullet[0] == 0) Rifle556Models.g3.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, bolt[2]);
		Rifle556Models.g3.renderPart("Guide_And_Bolt");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.625, plug[2]);
		LegacyGunGL.glRotated(handle[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -0.625, 0);
		Rifle556Models.g3.renderPart("Plug");
		
		LegacyGunGL.glTranslated(0, 0.625, 5.25);
		LegacyGunGL.glRotated(22.5, 0, 0, 1);
		LegacyGunGL.glRotated(handle[1], 0, 1, 0);
		LegacyGunGL.glRotated(-22.5, 0, 0, 1);
		LegacyGunGL.glTranslated(0, -0.625, -5.25);
		Rifle556Models.g3.renderPart("Handle");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -0.875, -3.5);
		LegacyGunGL.glRotated(-30 * (1 - ItemGunBaseNT.getMode(stack, 0)), 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.875, 3.5);
		Rifle556Models.g3.renderPart("Selector");
		LegacyGunGL.glPopMatrix();
		
		if(silenced || isScoped) {
			LegacyGunGL.texture(Rifle556Models.g3_attachments);
			if(silenced) Rifle556Models.g3.renderPart("Silencer");
			if(isScoped) Rifle556Models.g3.renderPart("Scope");
		}

		if(!silenced) {
			double smokeScale = 0.75;
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, 13);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
			this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
			LegacyGunGL.glPopMatrix();
			
			LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 0, 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(-25 + gun.shotRand * 10, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(gun.lastShot[0], 75, 10);
			LegacyGunGL.glPopMatrix();
		}
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 2, 4);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		if(hasStock(stack)) {
			double scale = 0.875D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(hasSilencer(stack) ? 50 : 45, 0, 1, 0);
			LegacyGunGL.glTranslated(hasSilencer(stack) ? 0.75 : -0.5, 0.5, 0);
		} else {
			double scale = 1.125D;
			LegacyGunGL.glScaled(scale, scale, scale);
			LegacyGunGL.glRotated(25, 1, 0, 0);
			LegacyGunGL.glRotated(hasSilencer(stack) ? 55 : 45, 0, 1, 0); //preserves proportions whilst limiting size
			LegacyGunGL.glTranslated(2.5, 0.5, 0);
		}
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0.5, -0.5);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		boolean silenced = hasSilencer(stack);
		boolean isScoped = this.isScoped(stack);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(getTexture(stack));
		Rifle556Models.g3.renderPart("Rifle");
		if(hasStock(stack)) Rifle556Models.g3.renderPart("Stock");
		Rifle556Models.g3.renderPart("Magazine");
		if(!silenced)Rifle556Models.g3.renderPart("Flash_Hider");
		Rifle556Models.g3.renderPart("Guide_And_Bolt");
		Rifle556Models.g3.renderPart("Handle");
		Rifle556Models.g3.renderPart("Trigger");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, -0.875, -3.5);
		LegacyGunGL.glRotated(-30, 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0.875, 3.5);
		Rifle556Models.g3.renderPart("Selector");
		LegacyGunGL.glPopMatrix();
		
		if(silenced || isScoped) {
			LegacyGunGL.texture(Rifle556Models.g3_attachments);
			if(silenced) Rifle556Models.g3.renderPart("Silencer");
			if(isScoped) Rifle556Models.g3.renderPart("Scope");
		}
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		//third-person muzzle flashes
		if(type == ItemRenderType.EQUIPPED && !silenced) {
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
			LegacyGunGL.glTranslated(0, 0, 12);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(-25 + shotRand * 10, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(shot, 75, 10);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean hasStock(ItemStack stack) {
		return !XWeaponModManager.hasUpgrade(stack, 0, 205);
	}
	
	public boolean hasSilencer(ItemStack stack) {
		return stack.getItem() == Rifle556Guns.gun_g3_zebra || XWeaponModManager.hasUpgrade(stack, 0, 201);
	}
	
	public boolean isScoped(ItemStack stack) {
		return stack.getItem() == Rifle556Guns.gun_g3_zebra || XWeaponModManager.hasUpgrade(stack, 0, 202);
	}
	
	public ResourceLocation getTexture(ItemStack stack) {
		if(XWeaponModManager.hasUpgrade(stack, 0, 211)) return Rifle556Models.g3_green_tex;
		if(XWeaponModManager.hasUpgrade(stack, 0, 212)) return Rifle556Models.g3_black_tex;
		return texture;
	}
}
