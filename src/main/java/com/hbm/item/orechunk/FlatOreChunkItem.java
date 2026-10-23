package com.hbm.item.orechunk;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** A physical static mineral chunk, not a new ore block or processing system. */
public final class FlatOreChunkItem extends Item implements FlatSourceItem {
    FlatOreChunkItem(){super(new Properties().stacksTo(64));}
}
