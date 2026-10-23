package com.hbm.bomb.nuclear.programmable;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
/** Original ingot_semtex ItemLemon(4,5,true): food and source tooltip, with no additional on-eaten branch. */
public final class SourceSemtexItem extends Item {
 public SourceSemtexItem(){super(new Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(5).build()));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){for(String line:Component.translatable("item.ingot_semtex.desc").getString().split("\\$"))lines.add(Component.literal(line));}
}
