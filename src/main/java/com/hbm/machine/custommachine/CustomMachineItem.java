package com.hbm.machine.custommachine;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original metadata100+niceList index; source machineType output NBT does not replace the ordinal contract. */
public final class CustomMachineItem extends BlockItem {
    public CustomMachineItem(CustomMachineBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public Component getName(ItemStack stack){var config=CustomMachinesConfig.byMetadata(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));return config==null?Component.translatable("item.hbm.custom_machine.invalid"):Component.literal(config.displayName(CustomMachineContent.LANGUAGE.get()));}
}
