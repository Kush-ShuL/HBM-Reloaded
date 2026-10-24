package com.hbm.world.structure.materials;

import com.hbm.block.ore.LegacyOreBlock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Sixteen fixed source colors share the actual block's original world-state palette. */
public final class ColoredConcreteItem extends BlockItem {
    private final DyeColor color;
    public ColoredConcreteItem(ColoredConcreteBlock block,DyeColor color){super(block,new Item.Properties());this.color=Objects.requireNonNull(color);}
    public DyeColor color(){return color;}
    @Override public String getDescriptionId(){return "block.hbm.concrete_colored_"+color.getName();}
    @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(color==DyeColor.WHITE)mapping.put(getBlock(),item);}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(LegacyOreBlock.LEGACY_META,color.getId());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
        super.appendHoverText(stack,context,lines,flags);
        lines.add(Component.translatable("tile.nospawn").withStyle(ChatFormatting.RED));
        lines.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",84F).withStyle(ChatFormatting.GOLD));
    }
}
