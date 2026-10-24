package com.hbm.item.secret;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** One real source component, without a metadata component or creative-tab entry. */
public final class FlatSecretItem extends Item implements FlatSourceItem {
    FlatSecretItem(){super(new Properties().stacksTo(64));}
}
