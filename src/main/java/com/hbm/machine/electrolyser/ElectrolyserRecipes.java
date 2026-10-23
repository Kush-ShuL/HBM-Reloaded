package com.hbm.machine.electrolyser;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.material.*;
import com.hbm.recipe.generic.*;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Actual two SerializableRecipe replacement files, retained typo gate, real synchronized native recipes. */
public final class ElectrolyserRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<RecipeManager,List<ElectrolyserRecipe>> CACHED=new WeakHashMap<>();
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<ElectrolyserRecipe> all(RecipeManager manager){return CACHED.computeIfAbsent(manager,m->m.getAllRecipesFor(ElectrolyserContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList());}
    public static ElectrolyserRecipe fluid(RecipeManager manager,HbmFluidType type){return all(manager).stream().filter(r->!r.metal()&&r.inputFluids().getFirst().type==type).findFirst().orElse(null);}
    public static ElectrolyserRecipe metal(RecipeManager manager,ItemStack input){if(input.isEmpty())return null;var canonical=com.hbm.ore.RawOreContent.processingStack(input);
        for(var r:all(manager))if(r.metal()&&!(r.inputItems().getFirst().ingredient().getCustomIngredient() instanceof LegacyOreIngredient)&&!(r.inputItems().getFirst().ingredient().getCustomIngredient() instanceof com.hbm.machine.centrifuge.CentrifugeNbtIngredient)&&com.hbm.ore.OreRecipeInputs.test(r.inputItems().getFirst().ingredient(),canonical))return r;
        for(var name:HbmOreDictionary.names(canonical))for(var r:all(manager))if(r.metal()&&r.inputItems().getFirst().ingredient().getCustomIngredient() instanceof LegacyOreIngredient ore&&ore.tag().toString().equals(HbmOreDictionary.registerName(name)))return r;
        return null;
    }
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var h:manager.getOrderedRecipes())if(previous.get(h.id())!=h)merged.put(h.id(),h);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();var unresolved=new ArrayList<String>();var dir=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");
        for(boolean metal:new boolean[]{false,true}){String file=metal?"hbmElectrolyzerMetal.json":"hbmElectrolyzerFluid.json",resource=metal?"electrolyser-metal-defaults.json":"electrolyser-fluid-defaults.json";var active=dir.resolve(file);boolean custom=Files.isRegularFile(active);
            try{Files.createDirectories(dir);JsonObject root;if(custom){try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}merged.values().removeIf(h->h.value() instanceof ElectrolyserRecipe r&&r.metal()==metal);}else{try(var stream=ElectrolyserRecipes.class.getResourceAsStream("/hbm/"+resource)){if(stream==null)throw new IllegalStateException("Missing original "+resource);root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(dir.resolve("_"+file),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
                var keyed=new LinkedHashMap<String,ElectrolyserRecipe>();int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var outputs=new ArrayList<GenericOutput>();if(row.has("byproducts"))for(var stack:row.getAsJsonArray("byproducts"))outputs.add(new GenericOutput(GenericRecipeConfigCodec.readStack(stack.getAsJsonArray(),Map.of())));
                    GenericRecipeData data;MaterialStack left=null,right=null;String key;
                    if(metal){var input=GenericRecipeConfigCodec.readIngredient(row.getAsJsonArray("input"),Map.of());int duration=row.has("duration")?row.get("duration").getAsInt():600;left=material(row,"output1");right=material(row,"output2");data=new GenericRecipeData("electrolyser.metal."+order,duration,10000,List.of(input),List.of(new FluidStack(HbmFluids.NITRIC_ACID,100)),outputs,List.of(),List.of(),"",ItemStack.EMPTY,false,"",1000+order);key=row.getAsJsonArray("input").toString();}
                    else{var input=fluid(row.getAsJsonArray("input"));int duration=!custom&&row.has("duration")?row.get("duration").getAsInt():custom&&row.has("duraion")?row.get("duration").getAsInt():20;data=new GenericRecipeData("electrolyser.fluid."+order,duration,10000,List.of(),List.of(input),outputs,List.of(fluid(row.getAsJsonArray("output1")),fluid(row.getAsJsonArray("output2"))),List.of(),"",ItemStack.EMPTY,false,"",order);key=input.type.getName();}
                    keyed.put(key,new ElectrolyserRecipe(data,left,right,metal));
                }catch(Exception error){unresolved.add(file+" row"+order+": "+error.getMessage());}order++;}
                for(var recipe:keyed.values()){var id=ResourceLocation.parse("hbm:electrolyser/"+(metal?"metal/":"fluid/")+(custom?"config/":"default/")+recipe.data().order());additions.put(id,new RecipeHolder<>(id,recipe));}
            }catch(Exception error){LogUtils.getLogger().error("Cannot load source electrolysis table "+file,error);}
        }additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);CACHED.remove(manager);if(!unresolved.isEmpty())LogUtils.getLogger().warn("Electrolyser source recipes retain unbound dependencies: {}",unresolved);
    }
    private static FluidStack fluid(JsonArray array){String name=array.get(0).getAsString();var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown source fluid "+name);return new FluidStack(type,array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0);}
    private static MaterialStack material(JsonObject row,String key){if(!row.has(key))return null;var a=row.getAsJsonArray(key);var mat=HbmMaterials.byName(a.get(0).getAsString());if(mat==null)throw new IllegalArgumentException("Unknown source molten material "+a);return new MaterialStack(mat,a.get(1).getAsInt());}
    private ElectrolyserRecipes(){}
}
