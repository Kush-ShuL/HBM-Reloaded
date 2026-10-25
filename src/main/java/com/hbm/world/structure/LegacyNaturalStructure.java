package com.hbm.world.structure;

import com.hbm.HbmReloaded;
import com.mojang.serialization.MapCodec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.*;

public final class LegacyNaturalStructure extends Structure {
    public static final MapCodec<LegacyNaturalStructure> CODEC=simpleCodec(LegacyNaturalStructure::new);
    private static final Set<String> MISSING=ConcurrentHashMap.newKeySet();
    public LegacyNaturalStructure(StructureSettings settings){super(settings);}
    @Override protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        if(!NaturalStructures.weightedStructuresEnabled())return Optional.empty();var chunk=context.chunkPos();int x=chunk.getMinBlockX(),z=chunk.getMinBlockZ();
        var biome=StructureSurfaceBiomes.sample(context,x+8,z+8);
        var set=context.registryAccess().registryOrThrow(Registries.STRUCTURE_SET).get(ResourceLocation.fromNamespaceAndPath("hbm","legacy_natural_structures"));
        if(set==null||!(set.placement() instanceof RandomSpreadStructurePlacement spread))return Optional.empty();
        var selected=LegacyStructureSelection.choose(context.seed(),chunk,biome,spread.spacing(),spread.separation(),((com.hbm.world.structure.mixin.StructurePlacementAccess)spread).hbm$sourceSalt());
        if(selected==null)return Optional.empty();var spawn=selected.spawn();
        var generator=NaturalStructures.generator(spawn.name());
        if(generator!=null)return generator.generate(spawn.name(),context,selected.random());
        if(!spawn.available())return Optional.empty();
        int turns=selected.random().nextInt(4);
        try{
            var template=LegacyStructureTemplate.get(spawn.template());template.states(turns);
            int y=context.chunkGenerator().getFirstFreeHeight(x+8,z+8,Heightmap.Types.WORLD_SURFACE_WG,context.heightAccessor(),context.randomState());
            var position=new BlockPos(x,y,z);
            return Optional.of(new GenerationStub(position,builder->builder.addPiece(new LegacyStructurePiece(spawn,template,turns,context.heightAccessor(),position))));
        }catch(RuntimeException missing){if(MISSING.add(spawn.name()))HbmReloaded.LOGGER.error("Natural structure {} requires its real source dependencies; entire piece skipped",spawn.name(),missing);return Optional.empty();}
    }
    @Override public StructureType<?> type(){return NaturalStructures.TYPE.get();}
}
