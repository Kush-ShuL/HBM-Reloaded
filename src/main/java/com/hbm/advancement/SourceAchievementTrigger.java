package com.hbm.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Real event-backed criterion; no inventory possession substitution for the old crafting-only dispatcher. */
public final class SourceAchievementTrigger extends SimpleCriterionTrigger<SourceAchievementTrigger.Instance> {
    public record Instance(Optional<ContextAwarePredicate> player,ResourceLocation achievement)implements SimpleCriterionTrigger.SimpleInstance{
        public static final Codec<Instance> CODEC=RecordCodecBuilder.create(builder->builder.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),ResourceLocation.CODEC.fieldOf("achievement").forGetter(Instance::achievement)).apply(builder,Instance::new));
    }
    @Override public Codec<Instance> codec(){return Instance.CODEC;}
    public void fire(ServerPlayer player,ResourceLocation id){trigger(player,instance->instance.achievement.equals(id));}
}
