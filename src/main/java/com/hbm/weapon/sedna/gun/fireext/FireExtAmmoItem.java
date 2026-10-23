package com.hbm.weapon.sedna.gun.fireext;
import com.hbm.item.EnumMultiItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** ItemAmmo(AmmoFireExt): exact three metadata values/names/icons; source all three trait sets are empty. */
public final class FireExtAmmoItem extends EnumMultiItem {
 public static final List<String> NAMES=List.of("ammo_fireext","ammo_fireext_foam","ammo_fireext_sand");
 public FireExtAmmoItem(){super("ammo_fireext",64,true,NAMES,List.of(0,1,2));}
 @Override public String getDescriptionId(ItemStack stack){return "item.hbm."+NAMES.get(ordinal(stack));}
 @Override public Component getName(ItemStack stack){return Component.translatable(getDescriptionId(stack));}
}
