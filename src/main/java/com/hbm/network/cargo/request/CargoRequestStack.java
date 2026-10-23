package com.hbm.network.cargo.request;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmOreDictionary;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** The two actual AStack branches used by drones: singular comparable metadata or original ore dictionary name. */
public record CargoRequestStack(Item item,int metadata,String dictionary) {
 public static CargoRequestStack comparable(ItemStack stack){return new CargoRequestStack(stack.getItem(),meta(stack),null);}public static CargoRequestStack wildcard(ItemStack stack){return new CargoRequestStack(stack.getItem(),32767,null);}public static CargoRequestStack ore(String name){return new CargoRequestStack(null,0,name);}
 public static int meta(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public boolean matches(ItemStack stack){if(stack.isEmpty())return false;return dictionary!=null?HbmOreDictionary.names(stack).contains(dictionary):stack.getItem()==item&&(metadata==32767||metadata==meta(stack));}
 public void save(CompoundTag tag){if(dictionary!=null){tag.putString("type","dict");tag.putString("dict",dictionary);}else{tag.putString("type","comp");tag.putString("hbm_item",BuiltInRegistries.ITEM.getKey(item).toString());tag.putInt("meta",metadata);}}
 public static CargoRequestStack load(CompoundTag tag){if(tag.getString("type").equals("dict"))return ore(tag.getString("dict"));var id=ResourceLocation.parse(tag.getString("hbm_item"));if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalArgumentException("Missing saved drone request item "+id);return new CargoRequestStack(BuiltInRegistries.ITEM.get(id),tag.getInt("meta"),null);}
}
