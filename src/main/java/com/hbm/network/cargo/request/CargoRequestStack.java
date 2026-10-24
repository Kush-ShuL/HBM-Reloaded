package com.hbm.network.cargo.request;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Singular comparable identity or a real native item tag. No old dictionary/save conversion. */
public record CargoRequestStack(Item item,int metadata,String tagId) {
 public static CargoRequestStack comparable(ItemStack stack){return new CargoRequestStack(stack.getItem(),meta(stack),null);}
 public static CargoRequestStack wildcard(ItemStack stack){return new CargoRequestStack(stack.getItem(),32767,null);}
 public static CargoRequestStack tag(String id){return new CargoRequestStack(null,0,ResourceLocation.parse(id).toString());}
 public static int meta(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public boolean matches(ItemStack stack){if(stack.isEmpty())return false;return tagId!=null?stack.is(HbmItemTags.key(tagId)):stack.getItem()==item&&(metadata==32767||metadata==meta(stack));}
 public void save(CompoundTag tag){if(tagId!=null){tag.putString("type","tag");tag.putString("tag",tagId);}else{tag.putString("type","comp");tag.putString("hbm_item",BuiltInRegistries.ITEM.getKey(item).toString());tag.putInt("meta",metadata);}}
 public static CargoRequestStack load(CompoundTag data){
  if(data.getString("type").equals("tag"))return tag(data.getString("tag"));
  if(!data.getString("type").equals("comp"))throw new IllegalArgumentException("Unknown native drone request type "+data.getString("type"));
  var id=ResourceLocation.parse(data.getString("hbm_item"));
  if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalArgumentException("Missing saved drone request item "+id);
  return new CargoRequestStack(BuiltInRegistries.ITEM.get(id),data.getInt("meta"),null);
 }
}
