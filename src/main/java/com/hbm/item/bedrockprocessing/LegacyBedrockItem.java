package com.hbm.item.bedrockprocessing;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One fixed ore-stage or byproduct identity, with its original name and render-pass tint. */
public final class LegacyBedrockItem extends Item {
    public final String symbol;
    public final boolean byproduct;
    private final int sourceOrdinal;
    public LegacyBedrockItem(String symbol,int sourceOrdinal){
        super(new Properties());this.symbol=symbol;this.byproduct=symbol.equals("ore_byproduct");
        int size=byproduct?BedrockProcessingData.DATA.byproducts().size():BedrockProcessingData.DATA.oldOres().size();
        if(sourceOrdinal<0||sourceOrdinal>=size)throw new IllegalArgumentException("Unknown fixed bedrock identity "+symbol+"/"+sourceOrdinal);
        this.sourceOrdinal=sourceOrdinal;
    }
    public int sourceOrdinal(){return sourceOrdinal;}
    public int color(int pass){return byproduct?BedrockProcessingData.DATA.byproducts().get(sourceOrdinal).color():pass==1?BedrockProcessingData.DATA.oldOres().get(sourceOrdinal).color():0xffffff;}
    @Override public String getDescriptionId(ItemStack stack){return "item."+symbol+(byproduct?"."+BedrockProcessingData.DATA.byproducts().get(sourceOrdinal).name().toLowerCase(Locale.US):"");}
    @Override public Component getName(ItemStack stack){
        if(byproduct)return Component.translatable(getDescriptionId(stack)+".name");
        var ore=BedrockProcessingData.DATA.oldOres().get(sourceOrdinal);
        return Component.translatable("item."+symbol+".name",Component.translatable("item.ore."+ore.oreName().toLowerCase(Locale.US)));
    }
}
