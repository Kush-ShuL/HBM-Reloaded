package com.hbm.client.hazard;

import com.hbm.hazard.HazardContext;
import com.hbm.client.config.ClientTooltipConfig;
import com.hbm.registry.HbmOreDictionary;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HazardTooltips {
    private HazardTooltips(){}
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
        var player=event.getEntity();
        HazardContext.withLookup(player==null?null:player.level().registryAccess(),()->{
            append(event.getItemStack(),event.getToolTip());
            appendOreDictionary(event.getItemStack(),event.getFlags().isAdvanced(),event.getToolTip());
            appendCustomNuke(event.getItemStack(),event.getToolTip());return null;
        });
    }
    private static void appendOreDictionary(ItemStack stack, boolean advanced, List<Component> text) {
        if (!advanced || !ClientTooltipConfig.oreDictionaryEnabled()) return;
        var names = HbmOreDictionary.names(stack);
        if (names.isEmpty()) return;
        text.add(Component.translatable("tooltip.hbm.ore_dictionary.title").withStyle(ChatFormatting.BLUE));
        for (String name : names) text.add(Component.literal(" -" + name).withStyle(ChatFormatting.AQUA));
    }

    private static void appendCustomNuke(ItemStack stack, List<Component> text) {
        if (!ClientTooltipConfig.customNukeEnabled()) return;
        var entry = com.hbm.bomb.nuclear.programmable.CustomNuclearIngredients.entry(stack);
        if (entry == null) return;
        if (!text.isEmpty()) text.add(Component.empty());
        var stage = Component.translatable("gui.hbm.custom_nuke.stage."
                + entry.stage().name().toLowerCase(java.util.Locale.ROOT));
        String operation = entry.operation()
                == com.hbm.bomb.nuclear.programmable.CustomNuclearIngredients.Operation.ADD ? "add" : "mult";
        text.add(Component.translatable("tooltip.hbm.custom_nuke." + operation, entry.value(), stage)
                .withStyle(ChatFormatting.GOLD));
    }

    private static void label(List<Component> text,String key,ChatFormatting color){
        text.add(Component.literal("[").append(Component.translatable(key)).append("]").withStyle(color));
    }
    public static void append(ItemStack stack,List<Component> text){
        for(var entry:HbmHazards.registry().evaluate(stack).entries()){
            Float level=entry.level();
            switch(entry.type()){
                case RADIATION->{
                    if(level!=null&&level<1e-5)continue;
                    label(text,"trait.radioactive",ChatFormatting.GREEN);
                    text.add(Component.literal(level==null?"?RAD/s":(Math.floor(level*1000)/1000)+"RAD/s").withStyle(ChatFormatting.YELLOW));
                    if(stack.getCount()>1)text.add(Component.literal(level==null?"Stack: ?RAD/s":"Stack: "+(Math.floor(level*1000*stack.getCount())/1000)+"RAD/s").withStyle(ChatFormatting.YELLOW));
                }
                case DIGAMMA->{
                    label(text,"trait.digamma",ChatFormatting.RED);
                    text.add(Component.literal(level==null?"?mDRX/s":((float)Math.floor(level*10000F)/10F)+"mDRX/s").withStyle(ChatFormatting.DARK_RED));
                    if(stack.getCount()>1)text.add(Component.literal(level==null?"Stack: ?mDRX/s":"Stack: "+(Math.floor(level*10000F*stack.getCount())/10F)+"mDRX/s").withStyle(ChatFormatting.DARK_RED));
                }
                case HOT->{if(level==null||level>0)label(text,"trait.hot",ChatFormatting.GOLD);}
                case BLINDING->label(text,"trait.blinding",ChatFormatting.DARK_AQUA);
                case ASBESTOS->label(text,"trait.asbestos",ChatFormatting.WHITE);
                case COAL->label(text,"trait.coal",ChatFormatting.DARK_GRAY);
                case HYDROACTIVE->label(text,"trait.hydro",ChatFormatting.RED);
                case EXPLOSIVE->label(text,"trait.explosive",ChatFormatting.RED);
            }
        }
    }
}
