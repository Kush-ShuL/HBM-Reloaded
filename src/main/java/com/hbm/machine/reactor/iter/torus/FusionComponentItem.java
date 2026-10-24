package com.hbm.machine.reactor.iter.torus;

import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Four fixed material items share the source block's unchanged world states. */
public final class FusionComponentItem extends BlockItem {
    private static final String[] NAMES={"bscco","bscco_welded","blanket","motor"};
    private final int material;
    public FusionComponentItem(int material){super(TorusContent.COMPONENT.get(),new Properties());if(material<0||material>=4)throw new IllegalArgumentException("Unknown fusion component material");this.material=material;}
    public int material(){return material;}
    @Override public String getDescriptionId(){return "block.hbm.fusion_component_"+NAMES[material];}
    @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(material==0)mapping.put(getBlock(),item);}
}
