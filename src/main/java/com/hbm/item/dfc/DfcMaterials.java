package com.hbm.item.dfc;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The original fifteen ItemCatalyst, four ItemAMSCore and long-durability ItemLens identities. */
public final class DfcMaterials {
    public record Entry(String id,String kind,int color,long power,float powerMod,float heatMod,float fuelMod,int heat,int fuel,List<String> lore,List<String> specialLore){}
    private record Definitions(List<Entry> entries){}
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<Entry> ENTRIES=load();
    private static final Map<String,DeferredItem<? extends Item>> REGISTERED=new LinkedHashMap<>();
    static {
        for(var entry:ENTRIES)REGISTERED.put(entry.id(),ITEMS.register(entry.id(),()->switch(entry.kind()){
            case "catalyst"->new DfcCatalystItem(entry);
            case "core"->new DfcCoreItem(entry);
            case "lens"->new DfcLensItem(entry.power());
            default->throw new IllegalArgumentException("Unknown original DFC item type: "+entry.kind());
        }));
    }
    private static List<Entry> load(){try(var input=DfcMaterials.class.getResourceAsStream("/hbm/dfc-materials.json")){if(input==null)throw new IllegalStateException("Missing true source DFC materials");return List.copyOf(new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class).entries());}catch(java.io.IOException error){throw new ExceptionInInitializerError(error);}}
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static Item item(String id){var value=REGISTERED.get(id);if(value==null)throw new IllegalArgumentException(id);return value.get();}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("control"))return List.of();return REGISTERED.entrySet().stream().filter(row->!row.getKey().equals("ams_core_thingy")).map(row->new ItemStack(row.getValue().get())).toList();}
    private DfcMaterials(){}
}
