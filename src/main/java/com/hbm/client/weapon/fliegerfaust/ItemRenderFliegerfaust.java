package com.hbm.client.weapon.fliegerfaust;

import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.legendary.LegacyGunGL;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** X5863 authored first/third-person, inventory and weapon-table transforms and full part hierarchy. */
public final class ItemRenderFliegerfaust extends LegacyGunRenderer {
    @Override
    protected float getTurnMagnitude(ItemStack stack) { return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -.25F; }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        float aiming = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
        return fov * (1 - aiming * .33F);
    }

    @Override
    public void setupFirstPerson(ItemStack stack) {
        LegacyGunGL.glTranslated(0, 0, .875);
        float offset = .8F;
        standardAimingTransform(stack, -2.75F * offset, -2F * offset, -1.5F * offset, -.9375, -7.75 / 8D, -2.5);
    }

    @Override
    public void renderFirstPerson(ItemStack stack) {
        var gun = (ItemGunBaseNT)stack.getItem();
        LegacyGunGL.texture(FliegerfaustModels.TEXTURE);
        float aiming = ItemGunBaseNT.prevAimingProgress + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
        LegacyGunGL.glScaled(1.25D, 1.25D, 1.25D);
        double[] equip = HbmAnimations.getRelevantTransformation("EQUIP");
        double[] reload = HbmAnimations.getRelevantTransformation("RELOAD");
        double[] drum = HbmAnimations.getRelevantTransformation("DRUM");
        double[] drumRotation = HbmAnimations.getRelevantTransformation("DRUMROT");
        double[] recoil = HbmAnimations.getRelevantTransformation("RECOIL");
        double[] unloading = HbmAnimations.getRelevantTransformation("UNLOADING");
        int rocketCount = unloading[0] > 0 ? gun.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null) : 9;

        LegacyGunGL.glRotated(aiming * -45, 0, 0, 1);
        LegacyGunGL.glTranslated(0, 0, 1);
        LegacyGunGL.glRotated(equip[0], 1, 0, 0);
        LegacyGunGL.glTranslated(0, 0, -1);
        LegacyGunGL.glTranslated(0, -6, -1);
        LegacyGunGL.glRotated(reload[0], 1, 0, 0);
        LegacyGunGL.glTranslated(0, 6, 1);
        LegacyGunGL.glTranslated(0, 0, recoil[2]);
        LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
        FliegerfaustModels.part("Launcher");

        LegacyGunGL.glPushMatrix();
        LegacyGunGL.glTranslated(0, 0, 2);
        LegacyGunGL.glTranslated(drum[0], drum[1], drum[2]);
        LegacyGunGL.glTranslated(0, 0, -8.5);
        LegacyGunGL.glRotated(drumRotation[0], 1, 0, 0);
        LegacyGunGL.glTranslated(0, 0, 8.5);
        FliegerfaustModels.part("Drum");
        for (int i = 1; i <= rocketCount; i++) FliegerfaustModels.part("Rocket" + i);
        LegacyGunGL.glPopMatrix();
        LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);

        LegacyGunGL.glPushMatrix();
        LegacyGunGL.glTranslated(0, 0, 8);
        LegacyGunGL.glRotated(90, 0, 1, 0);
        LegacyGunGL.glRotated(90 * gun.shotRand, 1, 0, 0);
        LegacyGunGL.glScaled(.75, .75, .75);
        renderMuzzleFlash(gun.lastShot[0], 75, 5);
        LegacyGunGL.glPopMatrix();
    }

    @Override
    public void setupThirdPerson(ItemStack stack) {
        super.setupThirdPerson(stack);
        LegacyGunGL.glScaled(2D, 2D, 2D);
        LegacyGunGL.glTranslated(0, 1, -2);
    }

    @Override
    public void setupInv(ItemStack stack) {
        super.setupInv(stack);
        LegacyGunGL.glScaled(1.375D, 1.375D, 1.375D);
        LegacyGunGL.glRotated(25, 1, 0, 0);
        LegacyGunGL.glRotated(45, 0, 1, 0);
        LegacyGunGL.glTranslated(0, .5, 0);
    }

    @Override
    public void setupModTable(ItemStack stack) {
        LegacyGunGL.glScaled(-8.5D, -8.5D, -8.5D);
        LegacyGunGL.glRotated(90, 0, 1, 0);
    }

    @Override
    public void renderOther(ItemStack stack, ItemRenderType type, Object... data) {
        LegacyGunGL.glEnable(LegacyGunGL.GL_LIGHTING);
        LegacyGunGL.glShadeModel(LegacyGunGL.GL_SMOOTH);
        LegacyGunGL.texture(FliegerfaustModels.TEXTURE);
        FliegerfaustModels.part("Launcher");
        LegacyGunGL.glTranslated(0, 0, 2);
        FliegerfaustModels.part("Drum");
        LegacyGunGL.glShadeModel(LegacyGunGL.GL_FLAT);
        if (type == ItemRenderType.EQUIPPED) {
            LivingEntity owner = (LivingEntity)data[1];
            long shot;
            double shotRandom = 0;
            if (owner == Minecraft.getInstance().player) {
                var gun = (ItemGunBaseNT)stack.getItem();
                shot = gun.lastShot[0];
                shotRandom = gun.shotRand;
            } else {
                shot = flashMap.getOrDefault(owner, -1L);
                if (shot < 0) return;
            }
            LegacyGunGL.glPushMatrix();
            LegacyGunGL.glTranslated(0, 0, 8);
            LegacyGunGL.glRotated(90, 0, 1, 0);
            LegacyGunGL.glRotated(90 * shotRandom, 1, 0, 0);
            LegacyGunGL.glScaled(.75, .75, .75);
            renderMuzzleFlash(shot, 150, 7.5);
            LegacyGunGL.glPopMatrix();
        }
    }
}
