package com.hbm.item.bedrockprocessing;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Ten true old ItemBedrockOre stages and the source ItemByproduct, all hidden from creative. */
public final class LegacyBedrockItem extends Item {
    public final String symbol;
    public final boolean byproduct;
    public LegacyBedrockItem(String symbol,boolean byproduct){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.symbol=symbol;this.byproduct=byproduct;}
    public int ordinal(ItemStack stack){return BedrockProcessingData.sourceIndex(BedrockProcessingData.metadata(stack),byproduct?BedrockProcessingData.DATA.byproducts().size():BedrockProcessingData.DATA.oldOres().size());}
    public int color(ItemStack stack,int pass){return byproduct?BedrockProcessingData.DATA.byproducts().get(ordinal(stack)).color():pass==1?BedrockProcessingData.DATA.oldOres().get(ordinal(stack)).color():0xffffff;}
    @Override public String getDescriptionId(ItemStack stack){return "item."+symbol+(byproduct?"."+BedrockProcessingData.DATA.byproducts().get(ordinal(stack)).name().toLowerCase(Locale.US):"");}
    @Override public Component getName(ItemStack stack){
        if(byproduct)return Component.translatable(getDescriptionId(stack)+".name");
        var ore=BedrockProcessingData.DATA.oldOres().get(ordinal(stack));
        return Component.translatable("item."+symbol+".name",Component.translatable("item.ore."+ore.oreName().toLowerCase(Locale.US)));
    }
    public ItemStack variant(int metadata){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public List<ItemStack> variants(){return IntStream.range(0,byproduct?BedrockProcessingData.DATA.byproducts().size():BedrockProcessingData.DATA.oldOres().size()).mapToObj(this::variant).toList();}
}
