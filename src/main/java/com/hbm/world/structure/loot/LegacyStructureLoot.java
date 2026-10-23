package com.hbm.world.structure.loot;

import com.google.gson.*;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** X5846 WeightedRandomChestContent/ItemPool selection and its replacement JSON config. */
public final class LegacyStructureLoot {
    public record Entry(ItemStack template,int minimum,int maximum,int weight){public Entry{template=template.copy();if(maximum<minimum||weight<0)throw new IllegalArgumentException("Invalid source loot range/weight");}}
    public record Draw(ItemStack item,int count){public Draw{item=item.copy();}}
    private static final ThreadLocal<java.util.function.Consumer<Draw>> CAPTURE=new ThreadLocal<>();
    private record Pool(List<Entry> entries,boolean ready,String reason){}
    private static final Map<MinecraftServer,Map<String,Pool>> POOLS=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<net.minecraft.core.HolderLookup.Provider,Map<String,Pool>> PLANS=Collections.synchronizedMap(new WeakHashMap<>());
    private static final String BACKUP="__SOURCE_BACKUP";
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->load(event.getServer()));NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event)->{if(event.getPlayer()==null)load(event.getPlayerList().getServer());});}
    public static ResourceKey<LootTable> table(String pool){return ResourceKey.create(Registries.LOOT_TABLE,ResourceLocation.fromNamespaceAndPath("hbm","structure/"+pool.toLowerCase(Locale.ROOT)));}
    public static Set<String> names(ServerLevel level){return Set.copyOf(pools(level).keySet());}
    /** Original editor iterates ItemPool.pools (HashMap), including real pending pools, excluding the unregistered backup. */
    public static List<String> editorNames(ServerLevel level){var source=new HashMap<String,Boolean>();for(String name:pools(level).keySet())if(!name.equals(BACKUP))source.put(name,true);return new ArrayList<>(source.keySet());}
    public static boolean ready(ServerLevel level,String name){Pool pool=pools(level).get(name);return pool==null?pools(level).get(BACKUP).ready:pool.ready;}
    private static Map<String,Pool> pools(ServerLevel level){var pools=POOLS.get(level.getServer());if(pools==null){load(level.getServer());pools=POOLS.get(level.getServer());}return pools;}
    public static void requireReady(ServerLevel level,String name){var pool=pools(level).get(name);if(pool!=null&&!pool.ready)throw new IllegalStateException("Source loot pool "+name+" still has real unbound dependencies: "+pool.reason);}
    /** Source ItemPool.getStack always consumes nextInt(range), even when range is one. */
    public static ItemStack sample(ServerLevel level,String name,RandomSource random){return sample(pools(level),name,random);}
    private static Entry choose(Map<String,Pool> pools,String name,RandomSource random){var pool=pools.getOrDefault(name,pools.get(BACKUP));if(!pool.ready)throw new IllegalStateException("Incomplete source loot pool "+name+": "+pool.reason);int total=0;for(var entry:pool.entries)total=Math.addExact(total,entry.weight);int value=random.nextInt(total);Entry chosen=null;for(var entry:pool.entries)if((value-=entry.weight)<0){chosen=entry;break;}if(chosen==null)throw new IllegalStateException("Empty source loot pool "+name);return chosen;}
    private static ItemStack sample(Map<String,Pool> pools,String name,RandomSource random){var chosen=choose(pools,name,random);int count=chosen.minimum+random.nextInt(chosen.maximum-chosen.minimum+1);if(CAPTURE.get()!=null)CAPTURE.get().accept(new Draw(chosen.template,count));return chosen.template.copyWithCount(count);}
    /** BlockKeyhole copies the selected WeightedRandomChestContent template without rolling its amount. */
    public static ItemStack template(ServerLevel level,String name,RandomSource random){return choose(pools(level),name,random).template.copy();}
    /** Actual datapack LootTable execution, with the caller's original random source. */
    public static ItemStack draw(ServerLevel level,Vec3 origin,String name,RandomSource random){requireReady(level,name);String resolved=pools(level).containsKey(name)?name:BACKUP;var loot=level.getServer().reloadableRegistries().getLootTable(table(resolved));if(loot==LootTable.EMPTY){var json=JsonParser.parseString("{\"type\":\"minecraft:chest\",\"pools\":[{\"rolls\":1,\"entries\":[{\"type\":\"hbm:structure_pool\",\"pool\":\""+resolved+"\"}]}]}");loot=LootTable.DIRECT_CODEC.parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE),json).getOrThrow();loot.setLootTableId(table(resolved).location());}var params=new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN,origin).create(LootContextParamSets.CHEST);var context=new LootContext.Builder(params).withOptionalRandomSource(random).create(Optional.empty());var result=new ArrayList<ItemStack>();loot.getRandomItemsRaw(context,result::add);if(result.size()!=1)throw new IllegalStateException("Source pool table must emit exactly one raw draw: "+name);return result.getFirst();}
    /** A source zero-count pile entry still displayed its real icon in1.7. Keep that visual payload separately from its zero collectible quantity. */
    public static Draw drawPlaced(ServerLevel level,Vec3 origin,String name,RandomSource random){var captures=new ArrayList<Draw>();var previous=CAPTURE.get();CAPTURE.set(captures::add);try{var result=draw(level,origin,name,random);return captures.size()==1?captures.getFirst():new Draw(result,result.getCount());}finally{if(previous==null)CAPTURE.remove();else CAPTURE.set(previous);}}
    /** Planning uses the same original pool snapshot/sample kernel, retaining even zero-count visual pile entries. */
    public static Draw planDraw(net.minecraft.core.HolderLookup.Provider lookup,String name,RandomSource random){
        Map<String,Pool> definitions;synchronized(PLANS){definitions=PLANS.get(lookup);if(definitions==null){definitions=build(lookup);PLANS.put(lookup,definitions);}}
        var captures=new ArrayList<Draw>();var previous=CAPTURE.get();CAPTURE.set(captures::add);
        try{var result=sample(definitions,name,random);return captures.size()==1?captures.getFirst():new Draw(result,result.getCount());}
        finally{if(previous==null)CAPTURE.remove();else CAPTURE.set(previous);}
    }
    /** 1.7 chose arbitrary slots and overwrote earlier results; modern LootTable.fill shuffles empty slots. */
    public static void fill(ServerLevel level,Vec3 origin,Container inventory,String name,int amount,RandomSource random){requireReady(level,name);if(inventory.getContainerSize()==0)throw new IllegalArgumentException("Source inventory has no slots");for(int i=0;i<amount;i++){var stack=draw(level,origin,name,random);if(stack.getMaxStackSize()>=stack.getCount())inventory.setItem(random.nextInt(inventory.getContainerSize()),stack);else for(int j=0;j<stack.getCount();j++)inventory.setItem(random.nextInt(inventory.getContainerSize()),stack.copyWithCount(1));}inventory.setChanged();}
    public static void fillFixed(ServerLevel level,Vec3 origin,Container inventory,ItemStack source,int minimum,int maximum,int amount,RandomSource random){if(source.isEmpty())throw new IllegalArgumentException("Missing true fixed loot item");for(int i=0;i<amount;i++){random.nextInt(1);var stack=source.copyWithCount(minimum+random.nextInt(maximum-minimum+1));if(stack.getMaxStackSize()>=stack.getCount())inventory.setItem(random.nextInt(inventory.getContainerSize()),stack);else for(int j=0;j<stack.getCount();j++)inventory.setItem(random.nextInt(inventory.getContainerSize()),stack.copyWithCount(1));}inventory.setChanged();}
    public static int markerAmount(int minimum,int maximum,RandomSource random){return minimum+(maximum-minimum>0?random.nextInt(maximum-minimum):0);}
    public static boolean ready(net.minecraft.core.HolderLookup.Provider lookup,String name){Map<String,Pool> definitions;synchronized(PLANS){definitions=PLANS.get(lookup);if(definitions==null){definitions=build(lookup);PLANS.put(lookup,definitions);}}return definitions.getOrDefault(name,definitions.get(BACKUP)).ready;}
    public static void plan(net.minecraft.core.HolderLookup.Provider lookup,Container inventory,String name,int amount,RandomSource random){Map<String,Pool> definitions;synchronized(PLANS){definitions=PLANS.get(lookup);if(definitions==null){definitions=build(lookup);PLANS.put(lookup,definitions);}}var pool=definitions.getOrDefault(name,definitions.get(BACKUP));if(!pool.ready)throw new IllegalStateException("Incomplete planned source loot pool "+name+": "+pool.reason);for(int i=0;i<amount;i++){var stack=sample(definitions,name,random);if(stack.getMaxStackSize()>=stack.getCount())inventory.setItem(random.nextInt(inventory.getContainerSize()),stack);else for(int j=0;j<stack.getCount();j++)inventory.setItem(random.nextInt(inventory.getContainerSize()),stack.copyWithCount(1));}inventory.setChanged();}
    public static void fillFixed(Container inventory,ItemStack source,int minimum,int maximum,int amount,RandomSource random){fillFixed(null,null,inventory,source,minimum,maximum,amount,random);}
    private static synchronized void load(MinecraftServer server){var definitions=build(server.registryAccess());POOLS.put(server,definitions);PLANS.put(server.registryAccess(),definitions);}
    private static Map<String,Pool> build(net.minecraft.core.HolderLookup.Provider lookup){
        var result=new LinkedHashMap<String,Pool>();JsonObject defaults;
        try(var input=LegacyStructureLoot.class.getResourceAsStream("/hbm/structure-loot-pools.json")){if(input==null)throw new IllegalStateException("Run port_structure_loot.py");defaults=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();}
        catch(IOException error){throw new IllegalStateException(error);}
        for(var element:defaults.getAsJsonObject("pools").entrySet()){var row=element.getValue().getAsJsonObject();var entries=new ArrayList<Entry>();var reasons=new ArrayList<String>();for(var source:row.getAsJsonArray("entries")){var entry=source.getAsJsonObject();try{if(entry.has("pending"))throw new IllegalArgumentException(entry.get("pending").getAsString());var stack=ItemStack.CODEC.parse(lookup.createSerializationContext(JsonOps.INSTANCE),entry.get("stack")).getOrThrow();entries.add(new Entry(stack,entry.get("minimum").getAsInt(),entry.get("maximum").getAsInt(),entry.get("weight").getAsInt()));}catch(Exception error){reasons.add(error.getMessage());}}result.put(element.getKey(),new Pool(List.copyOf(entries),reasons.isEmpty(),String.join("; ",reasons)));}
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmConfig"),active=directory.resolve("hbmItemPools.json");
        try{Files.createDirectories(directory);if(Files.isRegularFile(active)){var configured=new LinkedHashMap<String,Pool>();configured.put(BACKUP,result.get(BACKUP));JsonObject json;try(var input=Files.newBufferedReader(active)){json=JsonParser.parseReader(input).getAsJsonObject();}for(var element:json.getAsJsonObject("pools").entrySet()){var entries=new ArrayList<Entry>();for(var value:element.getValue().getAsJsonArray()){var entry=value.getAsJsonArray();entries.add(new Entry(configStack(entry.get(0).getAsJsonArray(),lookup),entry.get(1).getAsInt(),entry.get(2).getAsInt(),entry.get(3).getAsInt()));}if(entries.isEmpty())throw new IllegalArgumentException("Empty configured source pool "+element.getKey());configured.put(element.getKey(),new Pool(List.copyOf(entries),true,""));}result=configured;}else{var exported=new JsonObject();var pools=new JsonObject();for(var element:result.entrySet())if(element.getValue().ready&&!element.getKey().equals(BACKUP)){var array=new JsonArray();for(var entry:element.getValue().entries){var value=new JsonArray();value.add(GenericRecipeConfigCodec.writeStack(entry.template));value.add(entry.minimum);value.add(entry.maximum);value.add(entry.weight);array.add(value);}pools.add(element.getKey(),array);}exported.add("pools",pools);Files.writeString(directory.resolve("_hbmItemPools.json"),new GsonBuilder().setPrettyPrinting().create().toJson(exported)+"\n");}}
        catch(Exception error){LogUtils.getLogger().error("Cannot replace source item pools; keeping defaults",error);}
        return Collections.unmodifiableMap(result); // Keep source definition/config insertion order for the editor HashMap view; immutable pool data/selection unchanged.
    }
    private static ItemStack configStack(JsonArray array,net.minecraft.core.HolderLookup.Provider lookup){var legacy=new net.minecraft.nbt.CompoundTag();legacy.putString("id",array.get(0).getAsString());legacy.putByte("Count",(byte)1);legacy.putShort("Damage",(short)(array.size()>2?array.get(2).getAsInt():0));if(array.size()>3)try{legacy.put("tag",net.minecraft.nbt.TagParser.parseTag(array.get(3).getAsString()));}catch(Exception error){throw new IllegalArgumentException("Invalid source loot NBT "+array,error);}return LegacyStructureItems.convertStack(legacy,Map.of(),lookup);}
    private LegacyStructureLoot(){}
}
