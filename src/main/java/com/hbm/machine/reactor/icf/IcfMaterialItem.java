package com.hbm.machine.reactor.icf;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class IcfMaterialItem extends BlockItem{
 public static final String[] COMPONENT={"",".vessel",".vessel_welded",".structure",".structure_bolted"},LASER={"casing","port","cell","emitter","capacitor","turbo"};private final IcfMaterialBlock block;
 public IcfMaterialItem(IcfMaterialBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.block=block;}
 @Override public Component getName(ItemStack stack){int meta=block.subtype(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));return Component.translatable(block.laser?"tile.icf_laser_component."+LASER[meta]+".name":"tile.icf_component"+COMPONENT[meta]+".name");}
}
