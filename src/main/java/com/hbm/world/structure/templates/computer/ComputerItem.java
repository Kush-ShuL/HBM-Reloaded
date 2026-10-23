package com.hbm.world.structure.templates.computer;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original BlockEnumMulti multiName=true/multiTexture=false, single IBM_300PL enum value. */
public final class ComputerItem extends BlockItem {
    public ComputerItem(ComputerBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile.deco_computer.ibm_300pl.name");}
}
