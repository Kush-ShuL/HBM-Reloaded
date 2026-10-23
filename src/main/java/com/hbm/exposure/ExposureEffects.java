package com.hbm.exposure;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact gas-related HbmPotion types, with the original per-tick effects and colors. */
public final class ExposureEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT,"hbm");
    public static final DeferredHolder<MobEffect,MobEffect> RADIATION = EFFECTS.register("radiation", () -> new SourceEffect("radiation",MobEffectCategory.HARMFUL,0x84C128));
    public static final DeferredHolder<MobEffect,MobEffect> RADAWAY = EFFECTS.register("radaway", () -> new SourceEffect("radaway",MobEffectCategory.BENEFICIAL,0xBB4B00));
    public static final DeferredHolder<MobEffect,MobEffect> RADX = EFFECTS.register("radx", () -> new SourceEffect("radx",MobEffectCategory.BENEFICIAL,0xBB4B00));
    public static final DeferredHolder<MobEffect,MobEffect> STABILITY = EFFECTS.register("stability", () -> new SourceEffect("stability",MobEffectCategory.BENEFICIAL,0xD0D0D0));
    public static final DeferredHolder<MobEffect,MobEffect> MUTATION = EFFECTS.register("mutation", () -> new SourceEffect("mutation",MobEffectCategory.BENEFICIAL,0x800080));
    private ExposureEffects() {}
    static void register(IEventBus bus) { EFFECTS.register(bus); }
    private static final class SourceEffect extends MobEffect {
        private final String name;
        SourceEffect(String name, MobEffectCategory category, int color) { super(category,color); this.name=name; }
        @Override public Component getDisplayName() { return Component.translatable("potion.hbm_"+name); }
        @Override public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) { return name.equals("radiation")||name.equals("radaway"); }
        @Override public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return true;
            if (name.equals("radiation")) HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,(amplifier+1F)*.05F);
            if (name.equals("radaway")) HbmExposure.incrementRadiation(entity,-(amplifier+1F));
            return true;
        }
    }
}
