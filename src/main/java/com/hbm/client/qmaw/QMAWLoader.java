package com.hbm.client.qmaw;

import com.google.gson.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.mojang.logging.LogUtils;
import java.util.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Native resource manager handles mod jars, development folders and selected resource packs in their real priority order. */
public final class QMAWLoader implements ResourceManagerReloadListener {
 public static final QMAWLoader INSTANCE=new QMAWLoader();
 public static final Map<String,QuickManualAndWiki> qmaw=new HashMap<>();
 private record Trigger(Item item,int metadata){}
 private static final Map<Trigger,QuickManualAndWiki> triggers=new HashMap<>();
 private static Map<String,String> aliases=Map.of();
 @Override public void onResourceManagerReload(ResourceManager resources){
  qmaw.clear();triggers.clear();var resolved=new HashMap<String,String>();
  for(var resource:resources.getResourceStack(net.minecraft.resources.ResourceLocation.parse("hbm:compat/manual-item-aliases.json")))try(var reader=resource.openAsReader()){var json=JsonParser.parseReader(reader).getAsJsonObject();json.entrySet().forEach(entry->resolved.put(entry.getKey(),entry.getValue().getAsString()));}catch(Exception error){LogUtils.getLogger().error("Cannot read original manual item aliases",error);}
  aliases=Map.copyOf(resolved);
  for(var entry:resources.listResourceStacks("manual",path->path.getNamespace().equals("hbm")&&path.getPath().endsWith(".json")).entrySet())for(var resource:entry.getValue())try(var reader=resource.openAsReader()){registerJson(entry.getKey().toString(),JsonParser.parseReader(reader).getAsJsonObject());}catch(Exception error){LogUtils.getLogger().error("Cannot read original manual {}",entry.getKey(),error);}
  LogUtils.getLogger().info("[QMAW] Loaded {} original manual entries",qmaw.size());
 }
 private static ItemStack stack(JsonArray json){try{return GenericRecipeConfigCodec.readStack(json,aliases);}catch(IllegalArgumentException error){return ItemStack.EMPTY;}}
 private static void language(JsonObject data,Map<String,String> target){data.entrySet().forEach(entry->target.put(entry.getKey().toLowerCase(Locale.ROOT),entry.getValue().getAsString()));}
 public static void registerJson(String file,JsonObject json){
  var page=new QuickManualAndWiki(json.get("name").getAsString());var source=net.minecraft.resources.ResourceLocation.parse(file);String path=source.getPath();page.languageKey="manual."+source.getNamespace()+"."+path.substring("manual/".length(),path.length()-".json".length()).replace('/','.');if(json.has("icon"))page.icon=stack(json.getAsJsonArray("icon"));language(json.getAsJsonObject("title"),page.title);language(json.getAsJsonObject("content"),page.contents);
  for(var element:json.getAsJsonArray("trigger")){var array=element.getAsJsonArray();var trigger=stack(array);if(trigger.isEmpty()){LogUtils.getLogger().debug("Original manual {} references pending trigger {}",file,array);continue;}int meta=array.size()>2?array.get(2).getAsInt():0;triggers.put(new Trigger(trigger.getItem(),meta==32767?-1:metadata(trigger)),page);}
  if(json.has("noindex")&&json.get("noindex").getAsBoolean())page.noIndex();if(!page.contents.isEmpty())qmaw.put(page.name,page);
 }
 private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public static QuickManualAndWiki forStack(ItemStack stack){if(stack.isEmpty())return null;var exact=triggers.get(new Trigger(stack.getItem(),metadata(stack)));return exact!=null?exact:triggers.get(new Trigger(stack.getItem(),-1));}
 private QMAWLoader(){}
}
