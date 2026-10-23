package com.hbm.client.weapon.grenade40;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
public class ItemRenderCongoLake extends LegacyGunRenderer {

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F; }

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
				-1.5F * offset, -2F * offset, 1.25F * offset,
				0, -10 / 8D, 0.25);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		LegacyGunGL.texture(Grenade40Models.congolake_tex);
		double scale = 0.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

		HbmAnimations.applyRelevantTransformation("Gun");
		Grenade40Models.congolake.renderPart("Gun");


		LegacyGunGL.glPushMatrix();
		{
			HbmAnimations.applyRelevantTransformation("Pump");
			Grenade40Models.congolake.renderPart("Pump");
		}
		LegacyGunGL.glPopMatrix();


		LegacyGunGL.glPushMatrix();
		{
			float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
			HbmAnimations.applyRelevantTransformation("Sight");
			LegacyGunGL.glTranslated(0, 2.125, 3);
			LegacyGunGL.glRotated(aimingProgress * -90, 1, 0, 0);
			LegacyGunGL.glTranslated(0, -2.125, -3);
			Grenade40Models.congolake.renderPart("Sight");
		}
		LegacyGunGL.glPopMatrix();


		LegacyGunGL.glPushMatrix();
		{
			HbmAnimations.applyRelevantTransformation("Loop");
			Grenade40Models.congolake.renderPart("Loop");
		}
		LegacyGunGL.glPopMatrix();


		LegacyGunGL.glPushMatrix();
		{
			HbmAnimations.applyRelevantTransformation("GuardOuter");
			Grenade40Models.congolake.renderPart("GuardOuter");

			LegacyGunGL.glPushMatrix();
			{
				HbmAnimations.applyRelevantTransformation("GuardInner");
				Grenade40Models.congolake.renderPart("GuardInner");
			}
			LegacyGunGL.glPopMatrix();
		}
		LegacyGunGL.glPopMatrix();


		LegacyGunGL.glPushMatrix();
		{
			IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
			if(gun.getLastAnim(stack, 0) != GunAnimation.INSPECT || mag.getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) > 0) { //omit when inspecting and no shell is loaded

				LegacyGunGL.texture(Grenade40Models.casings_tex);

				HbmAnimations.applyRelevantTransformation("Shell");

				SpentCasing casing = mag.getCasing(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
				int[] colors = casing != null ? casing.getColors() : new int[] { SpentCasing.COLOR_CASE_40MM };

                Grenade40Models.congolake.renderPart("Shell",0xFF000000|colors[0]);
                Grenade40Models.congolake.renderPart("ShellFore",0xFF000000|colors[colors.length>1?1:0]);
			}
		}
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.25;

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.75, 4.25);
		double[] transform = HbmAnimations.getRelevantTransformation("Gun");
		LegacyGunGL.glRotated(-transform[5], 0, 0, 1);
		LegacyGunGL.glRotated(-transform[4], 0, 1, 0);
		LegacyGunGL.glRotated(-transform[3], 1, 0, 0);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 1D);
		LegacyGunGL.glPopMatrix();
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1.75, 4.25);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
		LegacyGunGL.glScaled(0.5, 0.5, 0.5);
		this.renderMuzzleFlash(gun.lastShot[0], 150, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		LegacyGunGL.glTranslated(0, -2.5, 4);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 2.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1.25, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -15D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1.25, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(Grenade40Models.congolake_tex);
		Grenade40Models.congolake.renderAll();
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
			LegacyGunGL.glTranslated(0, 1.75, 4.25);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(90 * shotRand, 1, 0, 0);
			LegacyGunGL.glScaled(0.5, 0.5, 0.5);
			this.renderMuzzleFlash(shot, 150, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
