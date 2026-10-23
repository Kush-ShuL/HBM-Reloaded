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
public class ItemRenderMAS36 extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * (isScoped(stack) ? 0.66F : 0.33F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		
		if(isScoped(stack)) {
			standardAimingTransform(stack,
				-1.5F * offset, -1.25F * offset, 1.75F * offset,
				-0.2, -5.875 / 8D, 1.125);
		} else {
			standardAimingTransform(stack,
					-1.5F * offset, -1.25F * offset, 1.75F * offset,
					0, -4.6825 / 8D, 0.75);
		}
	}
	
	

	@Override
	public void renderFirstPerson(ItemStack stack) {
		boolean isScoped = isScoped(stack);
		if(isScoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(Rifle762Models.mas36_tex);
		double scale = 0.375D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] stock = HbmAnimations.getRelevantTransformation("STOCK");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] boltTurn = HbmAnimations.getRelevantTransformation("BOLT_TURN");
		double[] boltPull = HbmAnimations.getRelevantTransformation("BOLT_PULL");
		double[] bullet = HbmAnimations.getRelevantTransformation("BULLET");
		double[] showClip = HbmAnimations.getRelevantTransformation("SHOW_CLIP");
		double[] clip = HbmAnimations.getRelevantTransformation("CLIP");
		double[] bullets = HbmAnimations.getRelevantTransformation("BULLETS");
		double[] stab = HbmAnimations.getRelevantTransformation("STAB");
		
		LegacyGunGL.glTranslated(0, -3, -3);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 3, 3);

		LegacyGunGL.glTranslated(stab[0], stab[1], stab[2]);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		Rifle762Models.mas36.renderPart("Gun");
		if(hasBayonet(stack))  Rifle762Models.mas36.renderPart("Bayonet");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.3125, -2.125);
		LegacyGunGL.glRotated(stock[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.3125, 2.125);
		Rifle762Models.mas36.renderPart("Stock");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0.0625 * 18.5, 0);
		LegacyGunGL.glRotated(boltTurn[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, 0.0625 * -18.5, 0);
		LegacyGunGL.glTranslated(0, 0, boltPull[2]);
		Rifle762Models.mas36.renderPart("Bolt");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(bullet[0], bullet[1], bullet[2]);
		Rifle762Models.mas36.renderPart("Bullet");
		LegacyGunGL.glPopMatrix();
		
		if(isScoped) Rifle762Models.mas36.renderPart("Scope");

		if(showClip[0] != 0) {
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(clip[0], clip[1], clip[2]);
			Rifle762Models.mas36.renderPart("Clip");
			LegacyGunGL.glPopMatrix();
			LegacyGunGL.glPushMatrix();
			if(bullets[0] == 0) Rifle762Clip.enable();
			Rifle762Clip.plane(0,1,0,-0.5F);
			
			
			LegacyGunGL.glTranslated(bullets[0], bullets[1], bullets[2]);
			Rifle762Models.mas36.renderPart("Bullets");
			Rifle762Clip.disable();
			LegacyGunGL.glPopMatrix();
		}

		double smokeScale = 0.25;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.125, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 1D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 8);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.5, 3);
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
		LegacyGunGL.glTranslated(0, -0.25, -2.5);
	}

	@Override
	public void renderModTable(ItemStack stack, int index) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.mas36_tex);
		Rifle762Models.mas36.renderPart("Gun");
		Rifle762Models.mas36.renderPart("Stock");
		Rifle762Models.mas36.renderPart("Bolt");
		if(isScoped(stack)) Rifle762Models.mas36.renderPart("Scope");
		if(hasBayonet(stack)) Rifle762Models.mas36.renderPart("Bayonet");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Rifle762Models.mas36_tex);
		Rifle762Models.mas36.renderPart("Gun");
		Rifle762Models.mas36.renderPart("Stock");
		Rifle762Models.mas36.renderPart("Bolt");
		if(isScoped(stack)) Rifle762Models.mas36.renderPart("Scope");
		if(type != ItemRenderType.EQUIPPED) LegacyGunGL.glTranslated(0, -1, -6);
		if(hasBayonet(stack)) Rifle762Models.mas36.renderPart("Bayonet");
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
			LegacyGunGL.glScaled(0.5, 0.5, 0.5);
			this.renderMuzzleFlash(shot, 75, 10);
			LegacyGunGL.glPopMatrix();
		}
	}
	
	public boolean isScoped(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 202);
	}
	
	public boolean hasBayonet(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack, 0, 213);
	}
}
