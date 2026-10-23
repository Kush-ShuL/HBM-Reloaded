package com.hbm.client.weapon.folly;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.hbm.client.weapon.legendary.*;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
public class ItemRenderFolly extends LegacyGunRenderer {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/weapons/folly.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/weapons/moonlight.png");
    private static String tr(String name){return net.minecraft.client.resources.language.I18n.get("gui.hbm.folly."+name);}


	public static long timeAiming;
	public static boolean jingle = false;
	public static boolean wasAiming = false;

	@Override
	protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2F : 2.5F; }

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return  fov * (1 - aimingProgress * 0.33F);
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LegacyGunGL.glTranslated(0, 0, 0.875);

		float offset = 0.8F;
		float aim = 0.75F;
		standardAimingTransform(stack,
				-2.5F * offset, -1.5F * offset, 2.75F * offset,
				-2 * aim, -1 * aim, 2.25F * offset);
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Player player = Minecraft.getInstance().player;
		LegacyGunGL.texture(TEXTURE);
		double scale = 0.75D;
		LegacyGunGL.glScaled(scale, scale, scale);

		double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
		double[] load = HbmAnimations.getRelevantTransformation("LOAD");
		double[] shell = HbmAnimations.getRelevantTransformation("SHELL");
		double[] screw = HbmAnimations.getRelevantTransformation("SCREW");
		double[] breech = HbmAnimations.getRelevantTransformation("BREECH");
		
		LegacyGunGL.glTranslated(0, 1, -4);
		LegacyGunGL.glRotated(-equip[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, -1, 4);
		
		LegacyGunGL.glTranslated(0, -2, -2);
		LegacyGunGL.glRotated(load[0], 1, 0, 0);
		LegacyGunGL.glTranslated(0, 2, 2);

		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);

		LegacyGunGL.mesh(MODEL,"Cannon");
		
		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(recoil[0], recoil[1], recoil[2]);
		LegacyGunGL.mesh(MODEL,"Barrel");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(shell[0], shell[1], shell[2]);
		LegacyGunGL.mesh(MODEL,"Shell");
		LegacyGunGL.glPopMatrix();

		LegacyGunGL.glPushMatrix();
		LegacyGunGL.glTranslated(breech[0], breech[1], breech[2]);
		LegacyGunGL.mesh(MODEL,"Breech");
		LegacyGunGL.glTranslated(0, 1, 0);
		LegacyGunGL.glRotated(screw[2], 0, 0, 1);
		LegacyGunGL.glTranslated(0, -1, 0);
		LegacyGunGL.mesh(MODEL,"Cog");
		LegacyGunGL.glPopMatrix();
		
		
		boolean isAiming = gun.prevAimingProgress >= 1F && gun.aimingProgress >= 1F;
		if(isAiming & !wasAiming) timeAiming = System.currentTimeMillis();
		
		if(isAiming) {
			
			String splash = getBootSplash();
			
			if(!jingle && !splash.isEmpty()) {
				GunSounds.play(player,"hbm:weapon.fire.vstar",.5F,1F);
				jingle = true;
			}
	
			LegacyGunGL.glPushMatrix();





			Font font = com.hbm.client.gui.font.LegacyFonts.get();
			float variance = 0.85F + player.getRandom().nextFloat() * 0.15F;
			
			if(System.currentTimeMillis() - timeAiming > 5000 && load[0] == 0) {
				IMagazine mag = gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
				String msg = mag.getAmount(stack, new GunInventory(player)) > 0 ? "+" : tr("no_ammo");
				LegacyGunGL.glPushMatrix();
				float crosshairSize = 0.01F;
				LegacyGunGL.glTranslatef((font.width(msg) / 2) * crosshairSize + 2, 1F + font.lineHeight * crosshairSize / 2F, -2.75F);
				LegacyGunGL.glScaled(crosshairSize, -crosshairSize, crosshairSize);
				LegacyGunGL.glRotated(180D, 0, 1, 0);

				FollyModelText.draw(msg,new Color(variance,variance*.5F,0F).getRGB());
				LegacyGunGL.glPopMatrix();
			}
			
			LegacyGunGL.glPushMatrix();
			float splashSize = 0.02F;
			LegacyGunGL.glTranslatef((font.width(splash) / 2) * splashSize + 2, 1F + font.lineHeight * splashSize / 2F, -2.75F);
			LegacyGunGL.glScaled(splashSize, -splashSize, splashSize);
			LegacyGunGL.glRotated(180D, 0, 1, 0);

			FollyModelText.draw(splash,new Color(variance,variance*.5F,0F).getRGB());
			LegacyGunGL.glPopMatrix();
			
			List<String> tty = getTTY();
			if(!tty.isEmpty()) {
				LegacyGunGL.glPushMatrix();
				float fontSize = 0.005F;
				LegacyGunGL.glTranslatef(2.5F, 1.375F, -2.75F);
				LegacyGunGL.glScaled(fontSize, -fontSize, fontSize);
				LegacyGunGL.glRotated(180D, 0, 1, 0);

				for(String line : tty) {
					FollyModelText.draw(line,new Color(variance,variance*.5F,0F).getRGB());
					LegacyGunGL.glTranslated(0, (font.lineHeight + 2), 0);
				}
				LegacyGunGL.glPopMatrix();
			}




			LegacyGunGL.glPopMatrix();
			
		} else {
			jingle = false;
		}
		
		wasAiming = isAiming;
		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 3D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glTranslated(-0.25, 0.5, 3);

	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.25D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(25, 1, 0, 0);
		LegacyGunGL.glRotated(45, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -0.5, 0);
	}

	@Override
	public void setupModTable(ItemStack stack) {
		double scale = -8.75D;
		LegacyGunGL.glScaled(scale, scale, scale);
		LegacyGunGL.glRotated(90, 0, 1, 0);
		LegacyGunGL.glTranslated(0, -1, 0);
	}

	@Override
	public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {

		
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
		LegacyGunGL.texture(TEXTURE);
		LegacyGunGL.mesh(MODEL,null);
		LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
	}
	
	public static String getBootSplash() {
		long now = System.currentTimeMillis();
		if(timeAiming + 5000 < now) return "";
		if(timeAiming + 3000 > now) return "";
		int splashIndex = (int)((now - timeAiming - 3000) * 35 / 2000) - 10;
		//use the StringBuilder this, can't eat the drywall that, this used to be a free country
		char[] letters = "VStarOS".toCharArray();
		String splash = "";
		for(int i = 0; i < letters.length; i++) {
			if(i < splashIndex - 1) splash += ChatFormatting.LIGHT_PURPLE;
			if(i == splashIndex - 1) splash += ChatFormatting.AQUA;
			if(i == splashIndex) splash += ChatFormatting.WHITE;
			if(i == splashIndex + 1) splash += ChatFormatting.AQUA;
			if(i == splashIndex + 2) splash += ChatFormatting.LIGHT_PURPLE;
			if(i > splashIndex + 2) splash += ChatFormatting.BLACK;
			splash += letters[i];
		}
		return splash;
	}
	
	public static List<String> getTTY() {
		List<String> tty = new ArrayList();
		long now = System.currentTimeMillis();
		int time = (int)((now - timeAiming));
		if(time < 3000) {
			if(time > 250) tty.add(ChatFormatting.GREEN + tr("post"));
			if(time > 500) tty.add(ChatFormatting.GREEN + tr("ram"));
			if(time > 500) tty.add(ChatFormatting.GREEN + tr("available"));
			if(time > 750) tty.add(ChatFormatting.GREEN + tr("splines"));
			if(time > 1500) tty.add(ChatFormatting.GREEN + tr("keyboard"));
			if(time > 2000) tty.add(ChatFormatting.GREEN + tr("boot"));
		}
		if(time > 5000) {
			Player player = Minecraft.getInstance().player;
			HitResult mop = LegacyEntityDamage.getMouseOver(player,250);
			String target = ChatFormatting.GREEN + tr("target");
			if(mop.getType()==HitResult.Type.MISS) target += "N/A";
			if(mop instanceof BlockHitResult block) target += block.getBlockPos().getX()+"/"+block.getBlockPos().getY()+"/"+block.getBlockPos().getZ();
			if(mop instanceof EntityHitResult struck) target += struck.getEntity().getName().getString();
			tty.add(target);
			tty.add(ChatFormatting.GREEN + tr("angle") + ((int)(-player.getXRot() * 100) / 100D));
		}
		return tty;
	}
}
