package com.hbm.client.weapon.pepperbox;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.util.Mth;
/** The actual item's NeoForge renderer owns its first-person model and authored transforms. */
public interface GunFirstPersonRenderer {
    void renderFirstPerson(ItemStack stack,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial);
    /** The actual source table preview; unsupported non-source renderers remain blank, as in GUIWeaponTable. */
    default void renderModificationTable(ItemStack stack,int configuration,PoseStack pose,MultiBufferSource buffers,int light,int overlay,float partial){}
    /** Retained source ItemRenderWeaponBase per-item walk-bob parameters. */
    default float swayMagnitude(ItemStack stack){return ItemGunBaseNT.getIsAiming(stack)?.1F:.5F;}
    default float swayPeriod(ItemStack stack){return .75F;}
    default float modelFov(ItemStack stack,float partial){return 70F;}
    default float viewFov(ItemStack stack,float fov,float partial){return fov*(1-Mth.lerp(partial,ItemGunBaseNT.prevAimingProgress,ItemGunBaseNT.aimingProgress)*.33F);}
    default float turnMagnitude(ItemStack stack){return ItemGunBaseNT.getIsAiming(stack)?2.5F:-.5F;}
    default boolean isAkimbo(net.minecraft.world.entity.LivingEntity owner){return false;}
}
