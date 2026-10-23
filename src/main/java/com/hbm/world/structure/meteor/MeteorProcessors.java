package com.hbm.world.structure.meteor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MeteorProcessors {
    private static final DeferredRegister<StructureProcessorType<?>> TYPES=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<MeteorSpikeProcessor>> SPIKE=TYPES.register("meteor_spike",()->()->MeteorSpikeProcessor.CODEC);
    static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<MeteorSourceStateProcessor>> SOURCE_STATES=TYPES.register("meteor_source_states",()->()->MeteorSourceStateProcessor.CODEC);
    static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<MeteorSourceTableProcessor>> TABLE=TYPES.register("meteor_source_table",()->()->MeteorSourceTableProcessor.CODEC);
    public static void register(IEventBus bus){TYPES.register(bus);}
    private MeteorProcessors(){}
}
