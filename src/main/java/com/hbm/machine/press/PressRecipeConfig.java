package com.hbm.machine.press;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.hbm.world.structure.loot.LegacyStructureItems;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** The original hbmPress.json replacement operates on the existing native hbm:press type for all three actual machines. */
public final class PressRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private static boolean registered;
    private PressRecipeConfig(){}
    private static Path file(){return FMLPaths.CONFIGDIR.get().resolve("hbmRecipes/hbmPress.json");}
    public static boolean hasReplacement(){return Files.isRegularFile(file());}
    public static void register(){if(registered)return;registered=true;NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private record Key(String kind,String sourceName,int count,int metadata,String nbt,String stamp){}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var recipe:manager.getOrderedRecipes())if(previous.get(recipe.id())!=recipe)merged.put(recipe.id(),recipe);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();var active=file();
        try{Files.createDirectories(active.getParent());if(hasReplacement()){
            merged.values().removeIf(holder->holder.value().getType()==PressContent.RECIPE_TYPE.get());JsonObject root;try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            var lastKeys=new HashMap<Key,ResourceLocation>();int order=0;for(var element:root.getAsJsonArray("recipes")){try{var row=element.getAsJsonObject();var input=row.getAsJsonArray("input");String kind=input.get(0).getAsString();String stamp=row.get("stamp").getAsString().toLowerCase(Locale.ROOT);int count=input.size()>2?input.get(2).getAsInt():1,meta=kind.equals("nbt")?0:input.size()>3?input.get(3).getAsInt():0;String nbt=kind.equals("nbt")&&input.size()>4?TagParser.parseTag(input.get(4).getAsString()).toString():"";
                var key=new Key(kind,input.get(1).getAsString(),count,meta,nbt,stamp);var id=ResourceLocation.fromNamespaceAndPath("hbm","press/config/"+order);var old=lastKeys.put(key,id);if(old!=null)additions.remove(old);
                var recipe=new PressRecipe(ingredient(input,server.registryAccess()),com.hbm.item.StampItem.StampType.valueOf(stamp.toUpperCase(Locale.ROOT)),stack(row.getAsJsonArray("output"),server.registryAccess()));additions.put(id,new RecipeHolder<>(id,recipe));
            }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
        }else{
            // The original source exports its actual default table,including the configured-material WIRE loop.
            var root=new JsonObject();var rows=new JsonArray();for(var holder:merged.values())if(holder.value() instanceof PressRecipe recipe){try{var data=new GenericRecipeData("press",0,0,List.of(new GenericIngredient(recipe.input(),1)),List.of(),List.of(),List.of(),List.of(),"",ItemStack.EMPTY,false,"",0);var serialized=GenericRecipeConfigCodec.write(data);var row=new JsonObject();row.add("input",serialized.getAsJsonArray("inputItem").get(0));row.addProperty("stamp",recipe.stamp().name().toLowerCase(Locale.ROOT));row.add("output",GenericRecipeConfigCodec.writeStack(recipe.result()));rows.add(row);}catch(Exception error){UNRESOLVED.add("template "+holder.id()+": "+error.getMessage());}}
            root.add("recipes",rows);Files.writeString(active.resolveSibling("_hbmPress.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
        }}catch(Exception error){LogUtils.getLogger().error("Cannot load original hbmPress.json",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Unbound actual source press configuration: {}",UNRESOLVED);
    }
    private static Ingredient ingredient(JsonArray array,HolderLookup.Provider lookup)throws com.mojang.brigadier.exceptions.CommandSyntaxException{
        String kind=array.get(0).getAsString();if(kind.equals("dict"))return GenericRecipeConfigCodec.readIngredient(array,Map.of()).ingredient();if(!kind.equals("item")&&!kind.equals("nbt"))throw new IllegalArgumentException("Unknown original AStack "+kind);
        int damage=kind.equals("nbt")?0:array.size()>3?array.get(3).getAsInt():0;var value=new JsonArray();value.add(array.get(1));value.add(1);value.add(damage==32767?0:damage);var required=kind.equals("nbt")&&array.size()>4?TagParser.parseTag(array.get(4).getAsString()):new CompoundTag();return new PressSourceIngredient(stack(value,lookup),damage==32767,required).toVanilla();
    }
    private static ItemStack stack(JsonArray array,HolderLookup.Provider lookup)throws com.mojang.brigadier.exceptions.CommandSyntaxException{var tag=new CompoundTag();tag.putString("id",array.get(0).getAsString());tag.putByte("Count",(byte)1);tag.putShort("Damage",(short)(array.size()>2?array.get(2).getAsInt():0));if(array.size()>3)tag.put("tag",TagParser.parseTag(array.get(3).getAsString()));var stack=LegacyStructureItems.convertStack(tag,Map.of(),lookup);if(stack.isEmpty())throw new IllegalArgumentException("Unregistered actual source press item "+array);stack.setCount(array.size()>1?array.get(1).getAsInt():1);return stack;}
}
