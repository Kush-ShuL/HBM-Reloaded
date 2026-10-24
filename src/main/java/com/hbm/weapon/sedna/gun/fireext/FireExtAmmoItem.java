package com.hbm.weapon.sedna.gun.fireext;
import com.hbm.item.FlatSourceItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
/** One fixed ItemAmmo(AmmoFireExt) identity; all three source trait sets are empty. */
public final class FireExtAmmoItem extends Item implements FlatSourceItem {
 public static final List<String> NAMES=List.of("ammo_fireext","ammo_fireext_foam","ammo_fireext_sand");
 private final int type;
 public FireExtAmmoItem(int type){super(new Properties().stacksTo(64));if(type<0||type>=NAMES.size())throw new IllegalArgumentException("Unknown extinguisher ammunition "+type);this.type=type;}
 public int type(){return type;}
 @Override public String getDescriptionId(){return "item.hbm."+NAMES.get(type);}
 @Override public String getDescriptionId(ItemStack stack){return getDescriptionId();}
 @Override public Component getName(ItemStack stack){return Component.translatable(getDescriptionId(stack));}
}
