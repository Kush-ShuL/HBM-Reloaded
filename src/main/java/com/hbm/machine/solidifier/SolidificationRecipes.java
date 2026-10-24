package com.hbm.machine.solidifier;

import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.world.structure.loot.LegacyStructureItems;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
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

/** Unique source solidification table, live flammability-derived defaults and hbmSolidifier.json replacement. */
public final class SolidificationRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private record View(Collection<RecipeHolder<?>> snapshot,List<SolidifierRecipe> rows){}
    private static final Map<RecipeManager,View> VIEWS=new WeakHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private SolidificationRecipes(){}
    public static void register(){
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<SolidifierRecipe> recipes(RecipeManager manager){
        var snapshot=manager.getOrderedRecipes();synchronized(VIEWS){var old=VIEWS.get(manager);if(old!=null&&old.snapshot==snapshot)return old.rows;
            var rows=manager.getAllRecipesFor(SolidifierContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(SolidifierRecipe::order)).toList();
            VIEWS.put(manager,new View(snapshot,rows));return rows;}
    }
    public static SolidifierRecipe find(RecipeManager manager,HbmFluidType type){var rows=recipes(manager);for(int i=rows.size()-1;i>=0;i--)if(rows.get(i).input().type==type)return rows.get(i);return null;}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}

    /** Exact cast, strict rounding thresholds and minimum from registerSFAuto, using the current real HBM trait. */
    private static int fuelAmount(HbmFluidType fluid,long tuPerSolidFuel){
        var flammable=fluid.getTrait(FluidTraits.Flammable.class);
        if(flammable==null)throw new IllegalArgumentException("Actual automatic solid fuel requires FT_Flammable: "+fluid.getName());
        int amount=(int)(tuPerSolidFuel*1000L*1.25D/flammable.getHeatEnergy());
        if(amount>10000)amount-=amount%1000;else if(amount>1000)amount-=amount%100;else if(amount>100)amount-=amount%10;
        return Math.max(amount,1);
    }
    private static JsonObject resolveDefault(JsonObject event){
        if(!event.has("automatic"))return event;
        var recipe=new JsonObject();var input=new JsonArray();String name=event.get("automatic").getAsString();
        input.add(name);input.add(fuelAmount(fluid(name),event.get("tuPerSolidFuel").getAsLong()));recipe.add("input",input);recipe.add("output",event.get("output").deepCopy());return recipe;
    }
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var recipe:manager.getOrderedRecipes())if(previous.get(recipe.id())!=recipe)merged.put(recipe.id(),recipe);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();var template=new JsonObject();var templateRows=new LinkedHashMap<HbmFluidType,JsonObject>();
        UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmSolidifier.json");boolean replacement=Files.isRegularFile(active);
        try{Files.createDirectories(directory);JsonObject root;
            if(replacement){merged.values().removeIf(recipe->recipe.value().getType()==SolidifierContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else try(var stream=SolidificationRecipes.class.getResourceAsStream("/hbm/machine-solidifier61-defaults.json")){
                if(stream==null)throw new IllegalStateException("Missing original solidification source defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();}
            int order=0;for(var value:root.getAsJsonArray("recipes")){
                try{var row=replacement?value.getAsJsonObject():resolveDefault(value.getAsJsonObject());var input=row.getAsJsonArray("input");var type=fluid(input.get(0).getAsString());
                    // Source readRecipe ignores FluidStack.pressure when storing Pair(amount,stack).
                    var recipe=new SolidifierRecipe(new FluidStack(type,input.get(1).getAsInt()),output(row.getAsJsonArray("output"),server.registryAccess()),order);
                    additions.values().removeIf(old->old.value() instanceof SolidifierRecipe r&&r.input().type==type);
                    var id=ResourceLocation.fromNamespaceAndPath("hbm","solidification/"+(replacement?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));templateRows.put(type,row);
                }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;
            }
            if(!replacement){var rows=new JsonArray();templateRows.values().forEach(rows::add);template.add("recipes",rows);Files.writeString(directory.resolve("_hbmSolidifier.json"),new GsonBuilder().setPrettyPrinting().create().toJson(template)+"\n");}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original hbmSolidifier.json",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);
        if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Unbound genuine solidification rows: {}",UNRESOLVED);
    }
    private static HbmFluidType fluid(String name){var fluid=HbmFluids.fromName(name);if(fluid==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown source solidification fluid "+name);return fluid;}
    private static ItemStack output(JsonArray value,HolderLookup.Provider lookup)throws com.mojang.brigadier.exceptions.CommandSyntaxException{
        var tag=new CompoundTag();tag.putString("id",value.get(0).getAsString());tag.putByte("Count",(byte)1);tag.putShort("Damage",(short)(value.size()>2?value.get(2).getAsInt():0));
        if(value.size()>3)tag.put("tag",TagParser.parseTag(value.get(3).getAsString()));
        var stack=LegacyStructureItems.convertStack(tag,Map.of(),lookup);if(stack.isEmpty())throw new IllegalArgumentException("Actual source output is absent "+value);stack.setCount(value.size()>1?value.get(1).getAsInt():1);return stack;
    }
}
