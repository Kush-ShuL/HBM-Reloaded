package com.hbm.weapon.sedna.ammo;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**103 separate registry items; neither old family is registered, aliased, datafixed or accepted as a saved stack. */
public final class FlatAmmoContent {
    private record Family(String id,int stackSize,String tab,List<String> names,List<Integer> order){}
    private record Document(List<Family> families){}
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final Map<String,List<DeferredItem<FlatAmmoItem>>> FAMILIES=new LinkedHashMap<>();
    private static final Map<String,List<DeferredItem<FlatAmmoItem>>> TABS=new LinkedHashMap<>();
    static {
        try(var stream=FlatAmmoContent.class.getResourceAsStream("/hbm/flat-ammunition.json")){
            if(stream==null)throw new IllegalStateException("Missing complete source ammunition identities");
            for(var family:new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Document.class).families()){
                if(!Set.of("ammo_standard","ammo_secret").contains(family.id()))throw new IllegalStateException("Unexpected source ammunition family "+family.id());
                var holders=new ArrayList<DeferredItem<FlatAmmoItem>>();
                for(int index=0;index<family.names().size();index++){int ordinal=index;String variant=family.names().get(index);holders.add(ITEMS.register(family.id()+"_"+variant,()->new FlatAmmoItem(family.id(),variant,ordinal,family.stackSize())));}
                FAMILIES.put(family.id(),List.copyOf(holders));
                if(family.tab()!=null)for(int index:family.order())TABS.computeIfAbsent(family.tab(),ignored->new ArrayList<>()).add(holders.get(index));
            }
        }catch(IOException error){throw new IllegalStateException("Cannot read actual ammunition variants",error);}
    }
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static Item standard(int sourceOrdinal){return FAMILIES.get("ammo_standard").get(sourceOrdinal).get();}
    public static Item secret(int sourceOrdinal){return FAMILIES.get("ammo_secret").get(sourceOrdinal).get();}
    public static ItemStack standardStack(int sourceOrdinal,int count){return new ItemStack(standard(sourceOrdinal),count);}
    public static List<ItemStack> indexedItems(){return FAMILIES.values().stream().flatMap(Collection::stream).map(item->new ItemStack(item.get())).toList();}
    public static List<ItemStack> creativeStacks(String tab){return TABS.getOrDefault(tab,List.of()).stream().map(item->new ItemStack(item.get())).toList();}
    private FlatAmmoContent(){}
}
