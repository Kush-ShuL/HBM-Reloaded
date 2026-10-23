package com.hbm.machine.turbine;

import com.hbm.item.FlatSourceItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** All11 physical source Expensive-mode components. No metadata, charging or industrial-machine state is stored here. */
public final class ExpensiveComponentItem extends Item implements FlatSourceItem {
    public static final List<String> NAMES=List.of("steel_plating","heavy_frame","circuit","lead_plating","ferro_plating","computer","bronze_tubes","plastic","gold_dust","degenerate_matter","cmb_tubes");
    private ExpensiveComponentItem(){super(new Properties().stacksTo(64));}
    public static List<DeferredItem<ExpensiveComponentItem>> register(DeferredRegister.Items items){
        return NAMES.stream().map(name->items.register("item_expensive_"+name,ExpensiveComponentItem::new)).toList();
    }
    public static List<ItemStack> creativeStacks(){return IndustrialTurbineContent.EXPENSIVE_PARTS.stream().map(item->new ItemStack(item.get())).toList();}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.hbm.expensive_mode.item").withStyle(ChatFormatting.RED));
    }
}
