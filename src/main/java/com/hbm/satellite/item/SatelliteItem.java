package com.hbm.satellite.item;

import com.hbm.item.FlatSourceItem;
import com.hbm.satellite.SatelliteContent;
import com.hbm.satellite.SatelliteFrequency;
import com.hbm.satellite.SatelliteKind;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One physical source payload kind. Frequency remains mutable CUSTOM_DATA, independent of item identity. */
public final class SatelliteItem extends Item implements SatelliteFrequency, FlatSourceItem {
    private final SatelliteKind kind;
    private SatelliteItem(SatelliteKind kind){super(new Properties().stacksTo(1));this.kind=kind;}
    public static List<DeferredItem<SatelliteItem>> register(DeferredRegister.Items items){
        return Arrays.stream(SatelliteKind.values()).map(kind->items.register("satellite_"+kind.serializedName(),()->new SatelliteItem(kind))).toList();
    }
    public static SatelliteKind kind(ItemStack stack){return ((SatelliteItem)stack.getItem()).kind;}
    public static ItemStack stack(SatelliteKind kind){return new ItemStack(SatelliteContent.PAYLOADS.get(kind.ordinal()).get());}
    public static List<ItemStack> creativeStacks(){return Arrays.stream(SatelliteKind.values()).filter(SatelliteKind::ready).map(SatelliteItem::stack).toList();}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag){
        text.add(Component.translatable("satchip.frequency").append(": "+getFreq(stack)).withStyle(ChatFormatting.AQUA));
    }
}
