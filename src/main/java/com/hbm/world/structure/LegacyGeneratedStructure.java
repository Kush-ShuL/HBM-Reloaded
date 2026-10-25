package com.hbm.world.structure;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/** Independent old HbmWorldGen family; its complete source body runs at native decoration, not early starts. */
public final class LegacyGeneratedStructure extends Structure {
    public static final MapCodec<LegacyGeneratedStructure> CODEC=simpleCodec(LegacyGeneratedStructure::new);
    public LegacyGeneratedStructure(StructureSettings settings){super(settings);}
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        // SURFACE/CARVERS do not exist at STRUCTURE_STARTS. The decoration
        // coordinator performs the real source sequence once with ready terrain.
        return Optional.empty();
    }
    @Override public StructureType<?> type(){return NaturalStructures.OLD_TYPE.get();}
}
