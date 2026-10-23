package com.hbm.client.weapon.quadro;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.client.animation.bus.HbmAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Original complete OBJ part hierarchy and every source renderer matrix. */
public class ItemRenderQuadro extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-2.5F * offset, -3.5F * offset, 2.5F * offset,
				-1.5F * offset, -3F * offset, 2.5F * offset);
	}
	
	protected static String label = ">> <<";

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Player player = Minecraft.getInstance().player;
		double scale = 1.75D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] reloadPush = HbmAnimations.getRelevantTransformation("RELOAD_PUSH");
		double[] reloadRotate = HbmAnimations.getRelevantTransformation("RELOAD_ROTATE");
		
		LegacyGunGL.glTranslated(0, -1, -1);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 1);

		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glTranslated(0, -1, -1);
		LegacyGunGL.glRotated(reloadRotate[2], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 1);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.texture(QuadroModels.quadro_tex);
		QuadroModels.quadro.renderPart("Launcher");

		LegacyGunGL.glPushMatrix();
		
		LegacyGunGL.glTranslated(0, -1, 0);
		LegacyGunGL.glTranslated(0, 3, 0);
		LegacyGunGL.glRotated(reloadPush[1] * 30, 1, 0, 0);
		LegacyGunGL.glTranslated(0, -3, 0);
		LegacyGunGL.glTranslated(0, 0, reloadPush[0] * 3);
		LegacyGunGL.texture(QuadroModels.quadro_rocket_tex);
		QuadroModels.quadro.renderPart("Rockets");
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
		
		if(gun.prevAimingProgress >= 1F && gun.aimingProgress >= 1F) {
			
			LegacyGunGL.glPushMatrix();
			Font font = com.hbm.client.gui.font.LegacyFonts.get();
			float f3 = 0.04F;
			LegacyGunGL.glTranslatef(-0.375F, 2.25F, 0.875F);
			LegacyGunGL.glRotated(180D + (System.currentTimeMillis() / 2) % 360D, 0, -1, 0);
			LegacyGunGL.glTranslated(-(font.width(label) / 2) * f3, 0, 0);
			LegacyGunGL.glScaled(f3, -f3, f3);
			QuadroModelText.draw(label,0x00ffff);
			LegacyGunGL.glPopMatrix();
		}

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(-1, 0.75, 6.5);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -0.5, -0.25);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 4.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -30D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1.125, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(QuadroModels.quadro_tex);
		QuadroModels.quadro.renderPart("Launcher");
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
			
			LegacyGunGL.glPushMatrix(); //TODO: adjust in third person, flash is too far forward/ large
			LegacyGunGL.glTranslated(0, 0.75, 2);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(shot, 150, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
