package com.hbm.damage;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** X5846 DT/DR ordering, symbolic armor sets, and hbmArmor.json format. */
public final class DamageResistanceHandler {
    public static final String CATEGORY_EXPLOSION="EXPL", CATEGORY_FIRE="FIRE", CATEGORY_PHYSICAL="PHYS", CATEGORY_ENERGY="EN";
    public static final TagKey<DamageType> ABSOLUTE=TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("hbm","legacy_absolute"));
    public static final Map<String,ResistanceStats> itemStats=new LinkedHashMap<>();
    public static final Map<ArmorSet,ResistanceStats> setStats=new LinkedHashMap<>();
    public static final Map<String,ResistanceStats> entityStats=new LinkedHashMap<>();
    public static final Map<String,List<ArmorSet>> itemInfoSet=new LinkedHashMap<>();
    private record Piercing(float dt,float dr) {}
    private static final ThreadLocal<Deque<Piercing>> PIERCING=ThreadLocal.withInitial(ArrayDeque::new);
    private static boolean registered;
    private DamageResistanceHandler() {}
    public static void register() {
        if(registered)return;
        registered=true;
        initDefaults();
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->load());
        NeoForge.EVENT_BUS.addListener(DamageResistanceHandler::onEntityAttacked);
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event)->addInfo(event.getItemStack(),event.getToolTip()));
    }
    public static void setup(float dt,float dr){PIERCING.get().push(new Piercing(dt,dr));}
    public static void reset(){Deque<Piercing> values=PIERCING.get();if(!values.isEmpty())values.pop();if(values.isEmpty())PIERCING.remove();}
    public static float currentPDT(){Piercing p=PIERCING.get().peek();return p==null?0:p.dt;}
    public static float currentPDR(){Piercing p=PIERCING.get().peek();return p==null?0:p.dr;}
    public static boolean isAbsolute(DamageSource source){return source.is(ABSOLUTE);}
    private static String id(String name){return name.indexOf(':')>=0?name:"hbm:"+name;}
    public record ArmorSet(String helmet,String chest,String legs,String boots) {}
    public static void registerSet(String helmet,String chest,String legs,String boots,ResistanceStats stats){
        ArmorSet set=new ArmorSet(helmet,chest,legs,boots);setStats.put(set,stats);
        for(String item:Arrays.asList(helmet,chest,legs,boots))if(item!=null)itemInfoSet.computeIfAbsent(item,key->new ArrayList<>()).add(set);
    }
    private static String worn(ItemStack stack){return stack.isEmpty()?null:BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();}
    public static void onEntityAttacked(LivingIncomingDamageEvent event){
        if(isAbsolute(event.getSource()))return;
        float[] values=getDTDR(event.getEntity(),event.getSource(),event.getAmount(),currentPDT(),currentPDR());
        // The source's attack-cancel check subtracts DR piercing directly; its
        // later damage calculation deliberately scales DR instead.
        float dt=values[0]-currentPDT(),dr=values[1]-currentPDR();
        if((dt>0&&dt>=event.getAmount())||dr>=1)event.setCanceled(true);
    }
    public static void beforeVanillaDamage(LivingEntity living,DamageSource source,DamageContainer container){
        float original=container.getNewDamage();
        float result=onEntityDamaged(living,source,original);
        container.setReduction(DamageContainer.Reduction.INNATE_RESISTANCE,original-result);
    }
    public static float onEntityDamaged(LivingEntity living,DamageSource source,float amount){
        amount=com.hbm.armor.LegacyArmorDamageHandler.hurt(living,source,amount);
        if(DamageClass.ELECTRIC.isApplicable(source.getMsgId())&&living.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PoweredFullSuitArmor)amount*=5;
        amount=calculateDamage(living,source,amount,currentPDT(),currentPDR());
        if(living instanceof IResistanceProvider provider)provider.onDamageDealt(source,amount);
        return amount;
    }
    public static String typeToCategory(DamageSource source){
        if(source.is(DamageTypeTags.IS_EXPLOSION))return CATEGORY_EXPLOSION;
        if(source.is(DamageTypeTags.IS_FIRE))return CATEGORY_FIRE;
        if(source.is(DamageTypeTags.IS_PROJECTILE))return CATEGORY_PHYSICAL;
        for(DamageClass type:List.of(DamageClass.LASER,DamageClass.PLASMA,DamageClass.MICROWAVE,DamageClass.SUBATOMIC,DamageClass.ELECTRIC))if(type.isApplicable(source.getMsgId()))return CATEGORY_ENERGY;
        if(source.getMsgId().equals("cactus")||source.getMsgId().equals("spikes"))return CATEGORY_PHYSICAL;
        if(source.getMsgId().equals("electricity")||source.getMsgId().equals("microwave"))return CATEGORY_ENERGY;
        if(source.getEntity()!=null)return CATEGORY_PHYSICAL;
        return source.getMsgId();
    }
    public static float calculateDamage(LivingEntity entity,DamageSource source,float amount,float pierceDT,float pierce){
        if(isAbsolute(source))return amount;
        float[] values=getDTDR(entity,source,amount,pierceDT,pierce);
        float dt=Math.max(0,values[0]-pierceDT);
        if(dt>=amount)return 0;
        amount-=dt;
        float dr=values[1]*Mth.clamp(1-pierce,0,2);
        return amount*(1-dr);
    }
    public static float[] getDTDR(LivingEntity entity,DamageSource source,float amount,float pierceDT,float pierce){
        float dt=0,dr=0;
        if(entity instanceof IResistanceProvider provider){float[] values=provider.getCurrentDTDR(source,amount,pierceDT,pierce);dt+=values[0];dr+=values[1];}
        ArmorSet worn=new ArmorSet(worn(entity.getItemBySlot(EquipmentSlot.HEAD)),worn(entity.getItemBySlot(EquipmentSlot.CHEST)),worn(entity.getItemBySlot(EquipmentSlot.LEGS)),worn(entity.getItemBySlot(EquipmentSlot.FEET)));
        List<ResistanceStats> all=new ArrayList<>();all.add(setStats.get(worn));
        for(EquipmentSlot slot:List.of(EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD))all.add(itemStats.get(worn(entity.getItemBySlot(slot))));
        all.add(entityStats.get(entity.getClass().getName()));
        // The source uses exact classes, never an instanceof hierarchy search.
        if(entity.getClass()==net.minecraft.world.entity.monster.Creeper.class)all.add(entityStats.get("net.minecraft.entity.monster.EntityCreeper"));
        for(ResistanceStats stats:all)if(stats!=null){Resistance res=stats.getResistance(source);if(res!=null){dt+=res.threshold;dr+=res.resistance;}}
        return new float[]{dt,dr};
    }
    public enum DamageClass {
        PHYSICAL,FIRE,EXPLOSIVE,ELECTRIC,PLASMA,LASER,MICROWAVE,SUBATOMIC,OTHER;
        public boolean isApplicable(String value){return name().equalsIgnoreCase(value);}
    }
    public record Resistance(float threshold,float resistance) {}
    public static final class ResistanceStats {
        public final Map<String,Resistance> exactResistances=new LinkedHashMap<>(),categoryResistances=new LinkedHashMap<>();
        public Resistance otherResistance;
        public ResistanceStats addExact(String type,float threshold,float resistance){exactResistances.put(type.toLowerCase(Locale.US),new Resistance(threshold,resistance));return this;}
        public ResistanceStats addCategory(String type,float threshold,float resistance){categoryResistances.put(type,new Resistance(threshold,resistance));return this;}
        public ResistanceStats setOther(float threshold,float resistance){otherResistance=new Resistance(threshold,resistance);return this;}
        public Resistance getResistance(DamageSource source){
            Resistance exact=exactResistances.get(source.getMsgId().toLowerCase(Locale.US));if(exact!=null)return exact;
            Resistance category=categoryResistances.get(typeToCategory(source));if(category!=null)return category;
            return source.is(DamageTypeTags.BYPASSES_ARMOR)?null:otherResistance;
        }
        private JsonObject serialize(){
            JsonObject object=new JsonObject();
            for(var named:Map.of("exact",exactResistances,"category",categoryResistances).entrySet())if(!named.getValue().isEmpty()){
                JsonArray values=new JsonArray();named.getValue().forEach((key,value)->{JsonArray tuple=new JsonArray();tuple.add(key);tuple.add(value.threshold);tuple.add(value.resistance);values.add(tuple);});object.add(named.getKey(),values);
            }
            if(otherResistance!=null){JsonArray values=new JsonArray();values.add(otherResistance.threshold);values.add(otherResistance.resistance);object.add("other",values);}return object;
        }
        public static ResistanceStats deserialize(JsonObject object){
            ResistanceStats result=new ResistanceStats();
            for(String name:List.of("exact","category"))if(object.has(name))for(JsonElement element:object.getAsJsonArray(name)){JsonArray row=element.getAsJsonArray();if(name.equals("exact"))result.addExact(row.get(0).getAsString(),row.get(1).getAsFloat(),row.get(2).getAsFloat());else result.addCategory(row.get(0).getAsString(),row.get(1).getAsFloat(),row.get(2).getAsFloat());}
            if(object.has("other")){JsonArray row=object.getAsJsonArray("other");result.setOther(row.get(0).getAsFloat(),row.get(1).getAsFloat());}return result;
        }
    }
    private static void clear(){itemStats.clear();setStats.clear();entityStats.clear();itemInfoSet.clear();}
    public static void load(){
        var folder=FMLPaths.CONFIGDIR.get().resolve("hbmConfig");var config=folder.resolve("hbmArmor.json");
        try{
            Files.createDirectories(folder);clear();
            if(Files.isRegularFile(config))try(var reader=Files.newBufferedReader(config,StandardCharsets.UTF_8)){deserialize(JsonParser.parseReader(reader).getAsJsonObject());}
            else{initDefaults();JsonObject root=serialize();root.addProperty("comment","Template file, remove the underscore ('_') from the name to enable the config.");Files.writeString(folder.resolve("_hbmArmor.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n",StandardCharsets.UTF_8);}
        }catch(Exception error){clear();initDefaults();LogUtils.getLogger().error("Cannot load original hbmArmor.json; retaining X5846 defaults",error);}
    }
    public static JsonObject serialize(){
        JsonObject root=new JsonObject();JsonArray items=new JsonArray(),sets=new JsonArray(),entities=new JsonArray();
        itemStats.forEach((key,value)->{JsonArray row=new JsonArray();row.add(key);row.add(value.serialize());items.add(row);});
        setStats.forEach((key,value)->{JsonArray row=new JsonArray();for(String name:Arrays.asList(key.helmet,key.chest,key.legs,key.boots))row.add(name);row.add(value.serialize());sets.add(row);});
        entityStats.forEach((key,value)->{JsonArray row=new JsonArray();row.add(key);row.add(value.serialize());entities.add(row);});
        root.add("itemStats",items);root.add("setStats",sets);root.add("entityStats",entities);return root;
    }
    public static void deserialize(JsonObject root){
        for(JsonElement element:root.getAsJsonArray("itemStats")){JsonArray row=element.getAsJsonArray();itemStats.put(row.get(0).getAsString(),ResistanceStats.deserialize(row.get(1).getAsJsonObject()));}
        for(JsonElement element:root.getAsJsonArray("setStats")){JsonArray row=element.getAsJsonArray();String[] names=new String[4];for(int i=0;i<4;i++)names[i]=row.get(i).isJsonNull()?null:row.get(i).getAsString();registerSet(names[0],names[1],names[2],names[3],ResistanceStats.deserialize(row.get(4).getAsJsonObject()));}
        for(JsonElement element:root.getAsJsonArray("entityStats")){JsonArray row=element.getAsJsonArray();entityStats.put(row.get(0).getAsString(),ResistanceStats.deserialize(row.get(1).getAsJsonObject()));}
    }
    public static void addInfo(ItemStack stack,List<Component> tooltip){
        String item=worn(stack);List<ArmorSet> sets=itemInfoSet.get(item);
        if(sets!=null&&!sets.isEmpty()){
            ArmorSet set=sets.getFirst();ResistanceStats stats=setStats.get(set);List<Component> lines=statsLines(stats);
            if(!lines.isEmpty()){tooltip.add(Component.translatable("damage.inset").withStyle(ChatFormatting.DARK_PURPLE));for(String piece:Arrays.asList(set.helmet,set.chest,set.legs,set.boots))if(piece!=null){var value=BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(piece));tooltip.add(Component.literal("  ").append(value.<Component>map(v->v.getDefaultInstance().getHoverName()).orElseGet(()->Component.translatable("item.hbm."+ResourceLocation.parse(piece).getPath()))).withStyle(ChatFormatting.DARK_PURPLE));}tooltip.addAll(lines);}
        }
        ResistanceStats individual=itemStats.get(item);if(individual!=null){List<Component> lines=statsLines(individual);if(!lines.isEmpty()){tooltip.add(Component.translatable("damage.item").withStyle(ChatFormatting.DARK_PURPLE));tooltip.addAll(lines);}}
    }
    private static List<Component> statsLines(ResistanceStats stats){
        List<Component> lines=new ArrayList<>();stats.categoryResistances.forEach((key,value)->lines.add(statLine("damage.category."+key,value)));stats.exactResistances.forEach((key,value)->lines.add(statLine("damage.exact."+key,value)));if(stats.otherResistance!=null)lines.add(statLine("damage.other",stats.otherResistance));return lines;
    }
    private static Component statLine(String key,Resistance value){return Component.translatable(key).append(": "+value.threshold+"/"+(int)(value.resistance*100)+"%");}
    public static void initDefaults(){
        DefaultResistanceStats.populate();
    }
}
