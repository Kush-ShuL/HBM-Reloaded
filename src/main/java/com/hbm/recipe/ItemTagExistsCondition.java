package com.hbm.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

/** Tests actual membership against the current reload's tags, including optional external fuels. */
public record ItemTagExistsCondition(ResourceLocation tag) implements ICondition {
    public static final MapCodec<ItemTagExistsCondition> CODEC=ResourceLocation.CODEC.fieldOf("tag").xmap(ItemTagExistsCondition::new,ItemTagExistsCondition::tag);
    @Override public boolean test(IContext context){return !context.getAllTags(Registries.ITEM).getOrDefault(tag,java.util.List.of()).isEmpty();}
    @Override public MapCodec<? extends ICondition> codec(){return CODEC;}
}
