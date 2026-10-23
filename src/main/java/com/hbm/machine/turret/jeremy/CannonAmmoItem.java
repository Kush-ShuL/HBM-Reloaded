package com.hbm.machine.turret.jeremy;
import com.hbm.item.EnumMultiItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Real ItemAmmo(Ammo240Shell): all five source trait sets are empty; exact independent names/icons/metadata/order, stack64. */
public final class CannonAmmoItem extends EnumMultiItem {
 public static final List<String> NAMES=List.of("ammo_shell","ammo_shell_explosive","ammo_shell_apfsds_t","ammo_shell_apfsds_du","ammo_shell_w9");
 public CannonAmmoItem(){super("ammo_shell",64,true,NAMES,List.of(0,1,2,3,4));}
 @Override public String getDescriptionId(ItemStack stack){return "item.hbm."+NAMES.get(ordinal(stack));}
 @Override public Component getName(ItemStack stack){return Component.translatable(getDescriptionId(stack));}
}
