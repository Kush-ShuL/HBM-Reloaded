package com.hbm.world.structure.templates;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class TemplateProcessors {
    private static final DeferredRegister<StructureProcessorType<?>> TYPES=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<TemplateSourceStateProcessor>> SOURCE_STATES=TYPES.register("legacy_template_states",()->()->TemplateSourceStateProcessor.CODEC);
    public static void register(IEventBus bus){TYPES.register(bus);}
    private TemplateProcessors(){}
}
