package com.hbm.armor.rpa;
import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.weapon.sedna.gun.*;
import net.minecraft.world.item.ItemStack;
/** Source IPAMelee behavior contract; rendering is owned by the actual client's first-person provider. */
public interface PowerArmorMelee {
 void clickPrimary(ItemStack stack,ItemGunBaseNT.LambdaContext context);void clickSecondary(ItemStack stack,ItemGunBaseNT.LambdaContext context);
 void orchestra(ItemStack stack,ItemGunBaseNT.LambdaContext context);BusAnimation playAnim(ItemStack stack,GunAnimation animation);
}
