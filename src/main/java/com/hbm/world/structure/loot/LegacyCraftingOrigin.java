package com.hbm.world.structure.loot;
import java.util.List;
import net.minecraft.world.item.ItemStack;
/** Original 3x3 absolute coordinates that modern CraftingInput otherwise trims away. */
public interface LegacyCraftingOrigin {
    void hbm$setOrigin(int width,int height,List<ItemStack> items);
    int hbm$originalWidth();int hbm$originalHeight();List<ItemStack> hbm$originalItems();
}
