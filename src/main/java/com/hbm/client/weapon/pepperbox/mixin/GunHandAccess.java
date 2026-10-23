package com.hbm.client.weapon.pepperbox.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Original AnimationPacket resets the cached held stack and both equipped-progress values. */
@Mixin(value=ItemInHandRenderer.class,remap=false)
public interface GunHandAccess {
    @Accessor("mainHandItem") ItemStack hbm$cachedGun();
    @Accessor("mainHandItem") void hbm$cachedGun(ItemStack stack);
    @Accessor("mainHandHeight") float hbm$gunHeight();
    @Accessor("mainHandHeight") void hbm$gunHeight(float height);
    @Accessor("oMainHandHeight") float hbm$oldGunHeight();
    @Accessor("oMainHandHeight") void hbm$oldGunHeight(float height);
    @Accessor("offHandHeight") float hbm$offGunHeight();
    @Accessor("offHandHeight") void hbm$offGunHeight(float height);
    @Accessor("oOffHandHeight") float hbm$oldOffGunHeight();
    @Accessor("oOffHandHeight") void hbm$oldOffGunHeight(float height);
}
