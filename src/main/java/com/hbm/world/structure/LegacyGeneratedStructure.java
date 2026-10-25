package com.hbm.world.structure;

import com.hbm.world.structure.generated.GeneratedLegacyFeatures;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/** Independent old HbmWorldGen buildings; source chance tests run once per
 * native candidate chunk, separately from NTM's42 weighted spawn slots. */
public final class LegacyGeneratedStructure extends Structure {
    public static final MapCodec<LegacyGeneratedStructure> CODEC=simpleCodec(LegacyGeneratedStructure::new);
    public LegacyGeneratedStructure(StructureSettings settings){super(settings);}
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        if(!NaturalStructures.oldBuildingsEnabled())return Optional.empty();
        var random=new WorldgenRandom(new LegacyRandomSource(0));
        // Modern per-chunk structure seed, with Java's original48-bit RNG.
        // Individual source branches keep their own order and conditional draws.
        random.setLargeFeatureSeed(context.seed(),context.chunkPos().x,context.chunkPos().z);
        return GeneratedLegacyFeatures.generateAll(context,random);
    }
    @Override public StructureType<?> type(){return NaturalStructures.OLD_TYPE.get();}
}
