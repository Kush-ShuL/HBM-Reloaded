package com.hbm.item.nuclear.waste;

import java.util.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The eight actual source forms, each registered once per fixed isotope. */
public final class IsotopicWasteContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final Map<SourceWasteIsotopes.Form,List<DeferredItem<Item>>> FIXED=registerItems();
    private static Map<SourceWasteIsotopes.Form,List<DeferredItem<Item>>> registerItems(){
        var result=new EnumMap<SourceWasteIsotopes.Form,List<DeferredItem<Item>>>(SourceWasteIsotopes.Form.class);
        for(var form:SourceWasteIsotopes.Form.values()){
            var isotopes=new ArrayList<DeferredItem<Item>>();
            for(int i=0;i<form.variants();i++){int ordinal=i;String isotope=(form.longLived()?SourceWasteIsotopes.LongLived.values()[i].name():SourceWasteIsotopes.ShortLived.values()[i].name()).toLowerCase(Locale.ROOT);isotopes.add(ITEMS.register(form.symbol()+"_"+isotope,()->form.longLived()?new LongLivedWasteItem(form,SourceWasteIsotopes.LongLived.values()[ordinal]):new ShortLivedWasteItem(form,SourceWasteIsotopes.ShortLived.values()[ordinal])));}
            result.put(form,List.copyOf(isotopes));
        }
        return Collections.unmodifiableMap(result);
    }
    public static Item item(SourceWasteIsotopes.Form form,int isotope){return FIXED.get(form).get(isotope).get();}
    public static ItemStack stack(SourceWasteIsotopes.Form form,int isotope){return new ItemStack(item(form,isotope));}
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> allStacks(){var result=new ArrayList<ItemStack>();for(var form:SourceWasteIsotopes.Form.values())for(int i=0;i<form.variants();i++)result.add(stack(form,i));return List.copyOf(result);}
    public static List<ItemStack> creativeStacks(String tab){return "parts".equals(tab)?allStacks():List.of();}
    private IsotopicWasteContent(){}
}
