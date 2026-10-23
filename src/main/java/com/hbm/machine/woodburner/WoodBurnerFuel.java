package com.hbm.machine.woodburner;
import com.hbm.fuel.HbmFuels;
import com.hbm.registry.HbmOreDictionary;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
/** Original ModuleBurnTime(log4,wood2) and FireboxBase ash classifier, with ordered raw ore names. */
public final class WoodBurnerFuel {
    private WoodBurnerFuel() {}
    public static List<String> names(ItemStack stack) {
        var names=new ArrayList<>(HbmOreDictionary.names(stack));
        // These are exact old Forge vanilla ore memberships, mapped through actual modern item tags.
        if(stack.is(ItemTags.LOGS)&&!names.contains("logWood")) names.add("logWood");
        if(stack.is(ItemTags.PLANKS)&&!names.contains("plankWood")) names.add("plankWood");
        if(stack.is(ItemTags.SAPLINGS)&&!names.contains("treeSapling")) names.add("treeSapling");
        if(stack.is(Items.STICK)&&!names.contains("stickWood")) names.add("stickWood");
        if(stack.is(Items.COAL)&&!names.contains("gemCoal")) names.add("gemCoal");
        return names;
    }
    public static int burnTime(ItemStack stack) { int burn=HbmFuels.getBurnTimeFromCache(stack); if(burn==0) return 0; return (int)(burn*multiplier(stack)); }
    private static double multiplier(ItemStack stack) {
        if(stack.isEmpty()) return 0;
        String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if(Set.of("hbm:solid_fuel","hbm:solid_fuel_presto","hbm:solid_fuel_presto_triplet","hbm:solid_fuel_bf","hbm:solid_fuel_presto_bf","hbm:solid_fuel_presto_triplet_bf","hbm:rocket_fuel").contains(id)) return 1;
        for(String name:names(stack)) {
            if(name.contains("Coke")||name.contains("Coal")||name.contains("Lignite")) return 1;
            if(name.startsWith("log")) return 4;
            if(name.contains("Wood")) return 2;
        }
        return 1;
    }
    /** Source EnumAshType ordinals WOOD0/COAL1/MISC2. */
    public static int ashType(ItemStack stack) {
        for(String name:names(stack)) { if(name.contains("Coke")||name.contains("Coal")||name.contains("Lignite")) return 1; if(name.startsWith("log")||name.contains("Wood")||name.contains("Sapling")) return 0; }
        return stack.is(Items.CHARCOAL)?0:2;
    }
    public static List<Component> description() { return List.of(Component.literal("Burn time bonuses:").withStyle(ChatFormatting.GOLD),Component.literal("- Logs: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("+300%").withStyle(ChatFormatting.GREEN)),Component.literal("- Wood: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("+100%").withStyle(ChatFormatting.GREEN))); }
}
