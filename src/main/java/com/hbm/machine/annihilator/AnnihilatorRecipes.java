package com.hbm.machine.annihilator;
import com.google.gson.*;
import com.hbm.config.GeneralModes;
import com.hbm.fluid.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.recipe.generic.*;
import com.hbm.registry.HbmItemTags;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
/** Real SerializableRecipe key/milestone grammar, including empty normal-mode defaults. */
public final class AnnihilatorRecipes {
 public record Key(int kind,String value,int meta){public static Key item(ItemStack s){return new Key(0,BuiltInRegistries.ITEM.getKey(s.getItem()).toString(),0);}public static Key comparable(ItemStack s){return new Key(1,BuiltInRegistries.ITEM.getKey(s.getItem()).toString(),s.getOrDefault(HbmHazards.LEGACY_METADATA.get(),s.getDamageValue()));}public static Key fluid(HbmFluidType f){return new Key(2,f.getName(),0);}public static Key tag(String name){return new Key(3,name,0);}}
 public record Milestone(BigInteger amount,ItemStack payout){}
 private static Map<Key,List<Milestone>> recipes=Map.of();private static Boolean lastMode;private static boolean serverSnapshot;private static Map<Key,List<Milestone>> localSnapshot;private static Boolean localMode;
 public static synchronized Map<Key,List<Milestone>> recipes(){if(!serverSnapshot&&(lastMode==null||lastMode!=GeneralModes.mode528()))load(false);return recipes;}
 public static synchronized Map<Key,List<Milestone>> snapshot(){return copy(recipes());}
 public static synchronized void applyServerSnapshot(Map<Key,List<Milestone>> values){if(!serverSnapshot){localSnapshot=recipes;localMode=lastMode;}recipes=copy(values);serverSnapshot=true;}
 public static synchronized boolean restoreLocalSnapshot(){if(!serverSnapshot)return false;recipes=localSnapshot;lastMode=localMode;localSnapshot=null;localMode=null;serverSnapshot=false;return true;}
 private static Map<Key,List<Milestone>> copy(Map<Key,List<Milestone>> values){var result=new HashMap<Key,List<Milestone>>();values.forEach((key,steps)->result.put(key,steps.stream().map(step->new Milestone(step.amount(),step.payout().copy())).toList()));return Map.copyOf(result);}
 public static void register(IEventBus bus){net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStartedEvent event)->load(true));}
 public static void reload(){load(true);}
 private static synchronized void load(boolean template){boolean mode=GeneralModes.mode528();var loaded=new HashMap<Key,List<Milestone>>();try{try(var stream=AnnihilatorRecipes.class.getResourceAsStream("/hbm/annihilator-defaults.json")){if(stream==null)throw new IllegalStateException("Missing real annihilator defaults");var rows=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("mode528");if(mode)for(var element:rows){var row=element.getAsJsonObject();var steps=new ArrayList<Milestone>();for(var value:row.getAsJsonArray("milestones")){var milestone=value.getAsJsonObject();steps.add(new Milestone(milestone.get("amount").getAsBigInteger(),Blueprints.make(milestone.get("pool").getAsString())));}loaded.put(readKey(row.getAsJsonObject("key")),List.copyOf(steps));}}
  var file=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes/hbmAnnihilator.json");if(Files.isRegularFile(file)){var root=JsonParser.parseString(Files.readString(file));var rows=root.isJsonArray()?root.getAsJsonArray():root.getAsJsonObject().getAsJsonArray("recipes");loaded.clear();for(var value:rows){var row=value.getAsJsonObject();var steps=new ArrayList<Milestone>();for(var element:row.getAsJsonArray("milestones")){var step=element.getAsJsonObject();steps.add(new Milestone(step.get("amount").getAsBigInteger(),GenericRecipeConfigCodec.readStack(step.getAsJsonArray("payout"),Map.of())));}loaded.put(readKey(row.getAsJsonObject("key")),List.copyOf(steps));}}
  else if(template){Files.createDirectories(file.getParent());var rows=new JsonArray();for(var entry:loaded.entrySet()){var row=new JsonObject();row.add("key",writeKey(entry.getKey()));var steps=new JsonArray();for(var step:entry.getValue()){var s=new JsonObject();s.addProperty("amount",step.amount);s.add("payout",GenericRecipeConfigCodec.writeStack(step.payout));steps.add(s);}row.add("milestones",steps);rows.add(row);}var root=new JsonObject();root.add("recipes",rows);Files.writeString(file.resolveSibling("_hbmAnnihilator.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
 }catch(Exception error){throw new IllegalStateException("Cannot load original hbmAnnihilator.json",error);}recipes=Map.copyOf(loaded);lastMode=mode;}
 public static Key readKey(JsonObject row){String type=row.get("type").getAsString();return switch(type){case "item"->new Key(0,canonicalItem(row.get("item").getAsString()),0);case "comp"->new Key(1,canonicalItem(row.get("item").getAsString()),row.get("meta").getAsInt());case "fluid"->Key.fluid(HbmFluids.fromName(row.get("fluid").getAsString()));case "tag"->Key.tag(row.get("tag").getAsString());default->throw new IllegalArgumentException(type);};}
 private static String canonicalItem(String name){var id=ResourceLocation.parse(name);var path=id.getPath();if(path.startsWith("item."))path=path.substring(5);else if(path.startsWith("tile."))path=path.substring(5);if(id.getNamespace().equals("hbm")&&path.equals("ingot_copper"))return "minecraft:copper_ingot";return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),path).toString();}
 private static JsonObject writeKey(Key key){var row=new JsonObject();row.addProperty("type",List.of("item","comp","fluid","tag").get(key.kind));row.addProperty(key.kind==2?"fluid":key.kind==3?"tag":"item",key.value);if(key.kind==1)row.addProperty("meta",key.meta);return row;}
 public static ItemStack payout(Key key,BigInteger previous,BigInteger current){var recipe=recipes().get(key);if(recipe==null)return ItemStack.EMPTY;BigInteger highest=BigInteger.ZERO;ItemStack result=ItemStack.EMPTY;for(var step:recipe){if(previous!=null&&previous.compareTo(step.amount)>=0)continue;if(current.compareTo(highest)!=1)continue;if(current.compareTo(step.amount)>=0){highest=step.amount;result=step.payout;}}return result.copy();}
 public static List<ItemStack> examples(Key key){if(key.kind==3)return HbmItemTags.items(key.value).toList();if(key.kind==2)return List.of(com.hbm.item.fluid.FluidIconItem.make(HbmFluids.fromName(key.value),0));var id=ResourceLocation.parse(key.value);if(!BuiltInRegistries.ITEM.containsKey(id))return List.of();var stack=new ItemStack(BuiltInRegistries.ITEM.get(id));if(key.kind==1)stack.set(HbmHazards.LEGACY_METADATA.get(),key.meta);return List.of(stack);}
 private AnnihilatorRecipes(){}
}
