package com.hbm.armor;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.ExposureProtection;
import com.mojang.logging.LogUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;

/** Exact item-identity radiation exponents and original enabled JSON override. */
public final class HazmatRegistry implements ExposureProtection {
    public static final HazmatRegistry INSTANCE=new HazmatRegistry();
    public static final UUID SPECIAL_UUID=UUID.fromString("c95fdfd3-bea7-4255-a44b-d21bc3df95e3");
    private static final EquipmentSlot[] ORDER={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final Map<Item,Double> ENTRIES=new HashMap<>(),EXTERNAL=new java.util.LinkedHashMap<>();
    private static final Map<Item,List<Hazard>> HAZARDS=new HashMap<>();
    private static Map<String,String> aliases=Map.of();
    private HazmatRegistry(){}
    public static void registerHazmat(Item item,double resistance){ENTRIES.put(item,resistance);}
    public static void registerExternal(Item item,double resistance){EXTERNAL.put(item,resistance);}
    public static void registerHazards(Item item,List<Hazard> hazards){HAZARDS.put(item,List.copyOf(hazards));}
    public static List<Hazard> baseHazards(ItemStack stack){return HAZARDS.get(stack.getItem());}
    public static void initialize(){
        try(var stream=HazmatRegistry.class.getResourceAsStream("/hbm/hazmat.json")){
            if(stream==null)throw new IllegalStateException("Run tools/port_hazmat.py");
            var root=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
            var names=new HashMap<String,String>();root.getAsJsonObject("aliases").entrySet().forEach(entry->names.put(entry.getKey(),entry.getValue().getAsString()));aliases=Map.copyOf(names);ArmorModStorage.configureAliases(aliases);
            ENTRIES.clear();ENTRIES.putAll(EXTERNAL);
            for(var value:root.getAsJsonArray("external")){var row=value.getAsJsonObject();Item item=registered(row.get("item").getAsString());if(item!=null)registerHazmat(item,row.get("resistance").getAsDouble());}
            for(var value:root.getAsJsonArray("defaults")){var row=value.getAsJsonObject();Item item=registered(row.get("item").getAsString());if(item!=null)registerHazmat(item,row.get("resistance").getAsDouble());}
            HAZARDS.clear();
            for(var value:root.getAsJsonArray("hazards")){var row=value.getAsJsonObject();Item item=registered(row.get("item").getAsString());if(item==null)continue;var hazards=new ArrayList<Hazard>();row.getAsJsonArray("hazards").forEach(hazard->hazards.add(Hazard.valueOf(hazard.getAsString())));registerHazards(item,hazards);}
            var directory=FMLPaths.CONFIGDIR.get().resolve("hbmConfig");Files.createDirectories(directory);
            var enabled=directory.resolve("hbmRadResist.json");
            if(!Files.exists(enabled)){
                var template=new JsonObject();template.addProperty("comment","Template file, remove the underscore ('_') from the name to enable the config.");var entries=new JsonArray();
                ENTRIES.forEach((item,resistance)->{var row=new JsonObject();row.addProperty("item",BuiltInRegistries.ITEM.getKey(item).toString());row.addProperty("resistance",resistance);entries.add(row);});template.add("entries",entries);
                Files.writeString(directory.resolve("_hbmRadResist.json"),new GsonBuilder().setPrettyPrinting().create().toJson(template)+"\n");
            }else{
                Map<Item,Double> configured=readConfig(enabled);
                if(configured!=null){ENTRIES.clear();ENTRIES.putAll(configured);}
            }
        }catch(java.io.IOException error){LogUtils.getLogger().error("Cannot read/write hazmat template; preserving loaded original defaults",error);}
    }
    private static Item registered(String id){String target=aliases.getOrDefault(id,id);var location=ResourceLocation.tryParse(target);return location!=null&&BuiltInRegistries.ITEM.containsKey(location)?BuiltInRegistries.ITEM.get(location):null;}
    private static Map<Item,Double> readConfig(java.nio.file.Path path){
        try(var reader=Files.newBufferedReader(path)){
            var configured=new HashMap<Item,Double>();var root=JsonParser.parseReader(reader).getAsJsonObject();
            for(var value:root.getAsJsonArray("entries"))try{var row=value.getAsJsonObject();Item item=registered(row.get("item").getAsString());if(item!=null)configured.put(item,row.get("resistance").getAsDouble());else LogUtils.getLogger().error("Tried loading unknown item {} for hazmat entry",row.get("item"));}catch(RuntimeException error){LogUtils.getLogger().error("Error reading original hazmat entry {}",value,error);}
            return configured;
        }catch(Exception error){LogUtils.getLogger().error("Unable to read hbmRadResist.json; keeping original defaults",error);return null;}
    }
    public static double getCladding(ItemStack stack,HolderLookup.Provider lookup){
        if(stack.isEmpty())return 0;
        float external=ArmorModStorage.data(stack).getFloat("hfr_cladding");if(external>0)return external;
        ItemStack cladding=ArmorModStorage.pryMod(stack,ArmorModStorage.CLADDING,lookup);
        return cladding.getItem() instanceof ArmorCladdingItem item?item.radiation:0;
    }
    public static double getResistance(ItemStack stack,HolderLookup.Provider lookup){return stack.isEmpty()?0:ENTRIES.getOrDefault(stack.getItem(),0D)+getCladding(stack,lookup);}
    public static float accumulateArmor(LivingEntity entity,float resistance){for(var slot:ORDER)resistance+=getResistance(entity.getItemBySlot(slot),entity.level().registryAccess());return resistance;}
    public static float getResistance(Player player){float resistance=0;if(player.getUUID().equals(SPECIAL_UUID))resistance+=.4F;resistance=accumulateArmor(player,resistance);if(player.hasEffect(ExposureEffects.RADX))resistance+=.2F;return resistance;}
    @Override public double radiationResistance(LivingEntity entity){return entity instanceof Player?accumulateArmor(entity,0):0;}
    @Override public float accumulateRadiationResistance(LivingEntity entity,float resistance){return entity instanceof Player?accumulateArmor(entity,resistance):resistance;}
    public static List<Hazard> protection(ItemStack stack,LivingEntity entity){
        var result=new ArrayList<Hazard>();if(stack.isEmpty())return result;
        result.addAll(HAZARDS.getOrDefault(stack.getItem(),List.of()));
        if(stack.getItem() instanceof GasMask mask){
            ItemStack filter=mask.filter(stack,entity);
            if(!filter.isEmpty()){var filtered=new ArrayList<>(HAZARDS.getOrDefault(filter.getItem(),List.of()));filtered.removeAll(mask.blacklist(stack,entity));result.addAll(filtered);}
        }
        if(ArmorModStorage.hasMods(stack))for(ItemStack mod:ArmorModStorage.pryMods(stack,entity.level().registryAccess()))if(!mod.isEmpty())result.addAll(protection(mod,entity));
        return result;
    }
    @Override public boolean protects(LivingEntity entity,Hazard hazard){return protection(entity.getItemBySlot(EquipmentSlot.HEAD),entity).contains(hazard);}
    @Override public void damageFilter(LivingEntity entity,int damage){GasMaskFilters.damage(entity,damage);}
}
