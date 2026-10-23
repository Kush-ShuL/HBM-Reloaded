package com.hbm.world.glyphidhive;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** IBlockMulti's one actual BlockItem, shared source name and source metadata, not three unrelated block IDs. */
public final class GlyphidHiveMaterialItem extends BlockItem {
    public GlyphidHiveMaterialItem(GlyphidHiveMaterialBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public static ItemStack stack(int metadata,int count){var stack=new ItemStack(GlyphidHiveMaterialContent.ITEM.get(),count);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile.glyphid_base.name");}
}
