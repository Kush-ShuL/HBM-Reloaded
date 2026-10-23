package com.hbm.client.weapon.missilelauncher;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Exact original ItemRenderMissileLauncher matrices and four animated OBJ parts. */
public class ItemRenderMissileLauncher extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.5F; }

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
		Player player = Minecraft.getInstance().player;
		LegacyGunGL.texture(MissileLauncherModels.missile_launcher_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] barrel = HbmAnimations.getRelevantTransformation("BARREL");
		double[] open = HbmAnimations.getRelevantTransformation("OPEN");
		double[] missile = HbmAnimations.getRelevantTransformation("MISSILE");
		
		LegacyGunGL.glTranslated(0, -2, -2);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 2);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		MissileLauncherModels.missile_launcher.renderPart("Launcher");
		
		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(0, 0.25, 1.6875);
		LegacyGunGL.glRotated(open[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -0.25, -1.6875);
		
		MissileLauncherModels.missile_launcher.renderPart("Front");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, barrel[2]);
		MissileLauncherModels.missile_launcher.renderPart("Barrel");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(missile[0], missile[1], missile[2]);
		MissileLauncherModels.missile_launcher.renderPart("Missile");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();
		
		if(gun.prevAimingProgress >= 1F && gun.aimingProgress >= 1F) {

			LegacyGunGL.glPushMatrix();
			Font font = com.hbm.client.gui.font.LegacyFonts.get();
			float f3 = 0.04F;
			LegacyGunGL.glTranslatef(0.9375F, 2.25F, -0.5625F + (font.width(label) / 2) * f3);
			LegacyGunGL.glScaled(f3, -f3, f3);
			LegacyGunGL.glRotated(90D, 0, 1, 0);
			float variance = 0.7F + player.getRandom().nextFloat() * 0.3F;
			MissileLauncherModelText.draw(label,new java.awt.Color(variance,0F,0F).getRGB());

			LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
			LegacyGunGL.glPopMatrix();
			

		}
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 6.75);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(gun.shotRand * 90, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.5, -2);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.5D;
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
		LegacyGunGL.glTranslated(0, -1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);

		LegacyGunGL.texture(MissileLauncherModels.missile_launcher_tex);
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		MissileLauncherModels.missile_launcher.renderPart("Launcher");
		MissileLauncherModels.missile_launcher.renderPart("Barrel");
		MissileLauncherModels.missile_launcher.renderPart("Front");
		if(gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null) > 0) MissileLauncherModels.missile_launcher.renderPart("Missile");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(type == ItemRenderType.EQUIPPED) {
			LivingEntity ent = (LivingEntity) data[1];
			long shot;
			double shotRand = 0;
			if(ent == Minecraft.getInstance().player) {
				shot = gun.lastShot[0];
				shotRand = gun.shotRand;
			} else {
				shot = LegacyGunRenderer.flashMap.getOrDefault(ent, (long) -1);
				if(shot < 0) return;
			}
			
			LegacyGunGL.glPushMatrix();
			LegacyGunGL.glTranslated(0, 1, 6.75);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(shotRand * 90, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
