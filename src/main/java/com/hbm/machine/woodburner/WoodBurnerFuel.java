package com.hbm.machine.woodburner;
import com.hbm.fuel.HbmFuels;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
/** Original ModuleBurnTime(log4,wood2) and FireboxBase ash classifier, with source-priority native item tags. */
public final class WoodBurnerFuel {
    private WoodBurnerFuel() {}
    public static List<String> tags(ItemStack stack) { return HbmFuels.tags(stack); }
    public static int burnTime(ItemStack stack) { int burn=HbmFuels.getBurnTimeFromCache(stack); if(burn==0) return 0; return (int)(burn*multiplier(stack)); }
    private static double multiplier(ItemStack stack) {
        if(stack.isEmpty()) return 0;
        String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if(Set.of("hbm:solid_fuel","hbm:solid_fuel_presto","hbm:solid_fuel_presto_triplet","hbm:solid_fuel_bf","hbm:solid_fuel_presto_bf","hbm:solid_fuel_presto_triplet_bf","hbm:rocket_fuel").contains(id)) return 1;
        for(String name:tags(stack)) {
            if(HbmFuels.isCoalLikeTag(name)) return 1;
            if(HbmFuels.isLogTag(name)) return 4;
            if(HbmFuels.isWoodTag(name)) return 2;
        }
        return 1;
    }
    /** Source EnumAshType ordinals WOOD0/COAL1/MISC2. */
    public static int ashType(ItemStack stack) {
        for(String name:tags(stack)) { if(HbmFuels.isCoalLikeTag(name)) return 1; if(HbmFuels.isLogTag(name)||HbmFuels.isWoodTag(name)||HbmFuels.isSaplingTag(name)) return 0; }
        return stack.is(Items.CHARCOAL)?0:2;
    }
    public static List<Component> description() { return List.of(Component.literal("Burn time bonuses:").withStyle(ChatFormatting.GOLD),Component.literal("- Logs: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("+300%").withStyle(ChatFormatting.GREEN)),Component.literal("- Wood: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("+100%").withStyle(ChatFormatting.GREEN))); }
}
