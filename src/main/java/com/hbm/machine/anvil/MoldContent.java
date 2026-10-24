package com.hbm.machine.anvil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.HbmMaterials;
import com.hbm.material.resources.GeneratedMaterialResources;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The configured source mold catalog is frozen before item registration and the first pack reload. */
public final class MoldContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final Map<ResourceLocation,DeferredItem<MoldItem>> PHYSICAL=new LinkedHashMap<>();
    private static final List<Definition> ORDERED=new ArrayList<>();
    private static final List<Identity> IDENTITIES=new ArrayList<>();
    private static boolean registered;
    private static List<MoldItem.Mold> resolved;
    private MoldContent(){}
    public static void register(IEventBus bus){
        if(registered)return;registered=true;HbmMaterials.initialize();
        var declared=new ArrayList<Seed>();
        try(var input=MoldContent.class.getResourceAsStream("/hbm/anvil-molds.json")){
            if(input==null)throw new IllegalStateException("Missing physical mold definitions");
            for(var value:JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonArray()){
                var row=value.getAsJsonObject();
                declared.add(new Seed(row.get("sourceOrdinal").getAsInt(),row.get("size").getAsInt(),row.get("name").getAsString(),row.has("shape")?row.get("shape").getAsString():"",row.has("amount")?row.get("amount").getAsInt():1,row.has("cost")?row.get("cost").getAsInt():0,row.has("fixed")?row.getAsJsonObject("fixed").deepCopy():new JsonObject(),row.has("blockOverride")&&row.get("blockOverride").getAsBoolean(),ResourceLocation.parse(row.get("itemId").getAsString()),false));
            }
        }catch(IOException error){throw new IllegalStateException(error);}
        // ItemMold appends all small custom molds, then all large molds. Their two counters may overlap.
        int small=100,large=200;
        for(boolean largeMold:new boolean[]{false,true})for(var mold:HbmMaterials.molds())if(mold.large()==largeMold){
            int ordinal=largeMold?large++:small++;
            String shape=mold.shape().symbol().substring("custom:".length()).toLowerCase(Locale.ROOT);
            var path=ResourceLocation.fromNamespaceAndPath("hbm","mold/custom/"+shape+"/"+(largeMold?"large":"small")+"/c"+mold.castAmount());
            declared.add(new Seed(ordinal,largeMold?1:0,mold.legacyName(),mold.shape().symbol(),mold.castAmount(),0,new JsonObject(),false,path,true));
        }
        // The source by-ID map keeps the last definition at an overlapping ordinal. Do not activate shadowed tools.
        var byOrdinal=new LinkedHashMap<Integer,Seed>();
        for(var seed:declared)byOrdinal.put(seed.sourceOrdinal(),seed);
        var physicalByOrdinal=new LinkedHashMap<Integer,ResourceLocation>();
        var repeats=new LinkedHashMap<ResourceLocation,Integer>();
        for(var seed:byOrdinal.values()){
            int copy=repeats.merge(seed.baseId(),1,Integer::sum);
            var id=copy==1?seed.baseId():seed.baseId().withPath(path->path+"/copy"+copy);
            var definition=seed.definition(id);
            var holder=ITEMS.register(id.getPath(),()->new MoldItem(definition));
            if(PHYSICAL.putIfAbsent(id,holder)!=null)throw new IllegalStateException("Duplicate physical mold "+id);
            physicalByOrdinal.put(seed.sourceOrdinal(),id);
            IDENTITIES.add(new Identity(id,"ModItems.mold",seed.sourceOrdinal(),true,seed.custom(),seed.name()));
        }
        // Preserve the source pretty order, including repeated display entries when source ordinals collide.
        for(var seed:declared)ORDERED.add(seed.definition(physicalByOrdinal.get(seed.sourceOrdinal())));
        ITEMS.register(bus);
        GeneratedMaterialResources.contribute(PackType.CLIENT_RESOURCES,MoldContent::customModels);
        GeneratedMaterialResources.register(bus);
    }
    public static List<MoldItem> items(){return PHYSICAL.values().stream().map(DeferredItem::get).toList();}
    public static List<Identity> identities(){return List.copyOf(IDENTITIES);}
    public static ResourceLocation orderAnchor(){return ResourceLocation.fromNamespaceAndPath("hbm","mold/nugget");}
    public static List<ItemStack> creativeStacks(){return ORDERED.stream().map(definition->new ItemStack(PHYSICAL.get(definition.itemId()).get())).toList();}
    public static synchronized List<MoldItem.Mold> molds(){if(resolved==null)resolved=ORDERED.stream().map(Definition::resolve).toList();return resolved;}
    public static ItemStack stack(MoldItem.Mold mold){return new ItemStack(PHYSICAL.get(mold.itemId()).get());}
    private static Map<ResourceLocation,byte[]> customModels(){
        var result=new LinkedHashMap<ResourceLocation,byte[]>();
        for(var identity:IDENTITIES)if(identity.custom()){
            var model=Map.of("parent","minecraft:item/generated","textures",Map.of("layer0","hbm:item/mold_"+identity.textureName().toLowerCase(Locale.ROOT)));
            result.put(identity.itemId().withPath(path->"models/item/"+path+".json"),new Gson().toJson(model).getBytes(StandardCharsets.UTF_8));
        }
        return result;
    }
    public record Identity(ResourceLocation itemId,String sourceField,int sourceOrdinal,boolean sourceHasSubtypes,boolean custom,String textureName){}
    private record Seed(int sourceOrdinal,int size,String name,String shape,int amount,int cost,JsonObject fixed,boolean blockOverride,ResourceLocation baseId,boolean custom){
        Definition definition(ResourceLocation id){return new Definition(id,sourceOrdinal,size,name,shape,amount,cost,fixed,blockOverride);}
    }
    public record Definition(ResourceLocation itemId,int sourceOrdinal,int size,String name,String shape,int amount,int cost,JsonObject fixed,boolean blockOverride){
        MoldItem.Mold resolve(){
            var outputs=new LinkedHashMap<String,ItemStack>();
            for(var entry:fixed.entrySet()){
                var data=entry.getValue().getAsJsonObject();var id=ResourceLocation.parse(data.get("id").getAsString());
                if(!BuiltInRegistries.ITEM.containsKey(id))continue;
                var stack=new ItemStack(BuiltInRegistries.ITEM.get(id),data.has("count")?data.get("count").getAsInt():1);
                if(data.has("components"))for(var component:data.getAsJsonObject("components").entrySet())if(component.getKey().equals("hbm:legacy_metadata"))stack.set(HbmHazards.LEGACY_METADATA.get(),component.getValue().getAsInt());
                outputs.put(entry.getKey(),stack);
            }
            return new MoldItem.Mold(itemId,size,name,shape,amount,cost,Map.copyOf(outputs),blockOverride);
        }
    }
}
