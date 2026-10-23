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
public class ItemRenderShredder extends LegacyGunRenderer {
	
	protected ResourceLocation texture;
	
	public ItemRenderShredder(ResourceLocation texture) {
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
				-1.5F * offset, -1.25F * offset, 1.5F * offset,
			0, -6.25 / 8D, 0.5);
	}
	
	protected static String label = "[> <]";

	@Override
	public void renderFirstPerson(ItemStack stack) {
		
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Player player = Minecraft.getInstance().player;
		double scale = 0.25D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] lift = HbmAnimations.getRelevantTransformation("LIFT");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] mag = HbmAnimations.getRelevantTransformation("MAG");
		double[] speen = HbmAnimations.getRelevantTransformation("SPEEN");
		double[] cycle = HbmAnimations.getRelevantTransformation("CYCLE");
		
		LegacyGunGL.glTranslated(0, -2, -6);
		LegacyGunGL.glRotated(equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 6);
		
		LegacyGunGL.glTranslated(0, 0, -4);
		LegacyGunGL.glRotated(lift[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 0, 4);
		
		LegacyGunGL.glTranslated(0, 0, recoil[2]);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		
		boolean sexy = stack.getItem() == com.hbm.weapon.sedna.gun.legendary.LegendaryGunsContent.SEXY.get();
		
		if(sexy || (gun.prevAimingProgress >= 1F && gun.aimingProgress >= 1F)) {

			LegacyGunGL.glPushMatrix();
            var font=Minecraft.getInstance().font;
            float f3=.04F;
            LegacyGunGL.glTranslated((font.width(label)/2)*f3,3.25F,-1.75F);
            LegacyGunGL.glScaled(f3,-f3,f3);
            LegacyGunGL.glRotated(180,0,1,0);
            float variance=.9F+player.getRandom().nextFloat()*.1F;
            int color=new java.awt.Color(sexy?variance:0F,sexy?0F:variance,0F).getRGB();
            var frame=LegacyGunGL.frame();
            font.drawInBatch(label,0,0,color,false,frame.pose().last().pose(),frame.buffers(),net.minecraft.client.gui.Font.DisplayMode.NORMAL,0,net.minecraft.client.renderer.LightTexture.FULL_BRIGHT);
            LegacyGunGL.glPopMatrix();
		}
		
		LegacyGunGL.texture(texture);

		KineticModels.shredder.renderPart("Gun");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(mag[0], mag[1], mag[2]);
		LegacyGunGL.glTranslated(0, -1, -0.5);
		LegacyGunGL.glRotated(speen[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 1, 0.5);
		KineticModels.shredder.renderPart("Magazine");
		LegacyGunGL.glTranslated(0, -1, -0.5);
		LegacyGunGL.glRotated(cycle[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, 1, 0.5);
		KineticModels.shredder.renderPart("Shells");
		LegacyGunGL.glPopMatrix();

		double smokeScale = 0.75;
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 7.5);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glScaled(smokeScale, smokeScale, smokeScale);
		this.renderSmokeNodes(gun.getConfig(stack, 0).smokeNodes, 0.5D);
		LegacyGunGL.glPopMatrix();
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(0, 1, 7.5);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glRotated(gun.shotRand * 90, 1, 0, 0);
		LegacyGunGL.glScaled(0.75, 0.75, 0.75);
		this.renderMuzzleFlash(gun.lastShot[0], 75, 7.5);
		LegacyGunGL.glPopMatrix();
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(0, 0.5, 4);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(-1.5, 0, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -7.5D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, 0, 1.5);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
		LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(texture);
		KineticModels.shredder.renderAll();
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
			LegacyGunGL.glTranslated(0, 1, 7.5);
			LegacyGunGL.glRotated(90, 0, 1, 0);
			LegacyGunGL.glRotated(shotRand * 90, 1, 0, 0);
			LegacyGunGL.glScaled(0.75, 0.75, 0.75);
			this.renderMuzzleFlash(shot, 75, 7.5);
			LegacyGunGL.glPopMatrix();
		}
	}
}
