package com.hbm.world.structure.vending;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** IBlockMulti's two item damages, distinct from the directional/proxy world metadata. */
public final class VendingMachineItem extends BlockItem {
    public VendingMachineItem(Block block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public static ItemStack stack(int metadata){var stack=new ItemStack(VendingMachineContent.ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile.vending_machine.name");}
}
