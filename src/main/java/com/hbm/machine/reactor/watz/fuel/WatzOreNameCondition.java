package com.hbm.machine.reactor.watz.fuel;

import com.hbm.registry.HbmOreDictionary;
import com.mojang.serialization.*;
import net.neoforged.neoforge.common.conditions.ICondition;

/** Source registerInit checks ore-name existence, not a fabricated replacement item or unconditional GT recipe. */
public record WatzOreNameCondition(String name) implements ICondition {
    public static final MapCodec<WatzOreNameCondition> CODEC=Codec.STRING.fieldOf("name").xmap(WatzOreNameCondition::new,WatzOreNameCondition::name);
    @Override public boolean test(IContext context){return HbmOreDictionary.allNames().contains(name);}
    @Override public MapCodec<? extends ICondition> codec(){return CODEC;}
}
