package com.hbm.client.weapon.stinger;
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
public class ItemRenderStinger extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.5F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);
		
		float offset = 0.8F;
		standardAimingTransform(stack,
				-3.75F * offset, -9F * offset, -3.5F * offset,
				-2.625F * offset, -6.5, -8.5F);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {
		if(ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(StingerModels.stinger_tex);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] reload = HbmAnimations.getRelevantTransformation("RELOAD");
		double[] rocket = HbmAnimations.getRelevantTransformation("ROCKET");
		
		LegacyGunGL.glTranslated(0, -1, -1);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 1);
		
		LegacyGunGL.glTranslated(0, -4, -3);
		LegacyGunGL.glRotated(reload[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 4, 3);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glRotated(180, 0, 1, 0);
		StingerModels.stinger.renderAll();
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.texture(StingerModels.panzerschreck_tex);
		LegacyGunGL.glTranslated(rocket[0], rocket[1] + 3.5, rocket[2] - 3);
		StingerModels.panzerschreck.renderPart("Rocket");
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();

		String label = net.minecraft.network.chat.Component.translatable("gun.hbm.stinger.not_accurate").getString();
		Font font = com.hbm.client.gui.font.LegacyFonts.get();
		float f3 = 0.04F;
		LegacyGunGL.glTranslatef(0.025F, -0.5F, (font.width(label) / 2) * f3 - 3);
		LegacyGunGL.glScaled(f3, -f3, f3);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(45, -1, 0, 0);
		StingerModelText.draw(label,0xff0000);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 0, 6.5);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, -2.5, -3.5);
		LegacyGunGL.glRotated(180, 0, 1, 0);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.0625D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(225, 0, 1, 0);
		LegacyGunGL.glTranslated(0.25, -2.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(-90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -4, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(StingerModels.stinger_tex);
		StingerModels.stinger.renderAll();
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
			LegacyGunGL.glTranslated(0, 3.5, -10.3795);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(shot, 150, 10);
			LegacyGunGL.glPopMatrix();
		}
	}
}
