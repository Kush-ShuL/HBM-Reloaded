package com.hbm.world.structure;

import com.hbm.world.structure.generated.GeneratedLegacyFeatures;
import com.hbm.world.structure.generated.GeneratedPlanPiece;
import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.slf4j.Logger;

/** The original independent source population after actual FEATURES and before native lighting. */
public final class SourceDecoration {
    public static final ResourceLocation STRUCTURE_ID=ResourceLocation.fromNamespaceAndPath("hbm","legacy_generated_structures");
    private static final Logger LOGGER=LogUtils.getLogger();

    public static CompletableFuture<ChunkAccess> generate(WorldGenContext context,ChunkStep step,StaticCache2D<GenerationChunkHolder> cache,ChunkAccess origin){
        ServerLevel level=context.level();
        if(!level.dimension().equals(Level.OVERWORLD)||!SourceDecorationOwnership.admitted(origin)
                ||!NaturalStructures.oldBuildingsEnabled(level.getServer().getWorldData().worldGenOptions().generateStructures()))
            return CompletableFuture.completedFuture(origin);
        var registry=level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var holder=registry.getHolder(ResourceKey.create(Registries.STRUCTURE,STRUCTURE_ID)).orElse(null);
        if(holder==null||!(holder.value() instanceof LegacyGeneratedStructure structure))return CompletableFuture.completedFuture(origin);
        var state=level.getChunkSource().getGeneratorState();
        ChunkPos position=origin.getPos();
        if(state.getPlacementsForStructure(holder).stream().noneMatch(placement->placement.isStructureChunk(state,position.x,position.z)))
            return CompletableFuture.completedFuture(origin);
        var biome=context.generator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(position.getMiddleBlockX()),QuartPos.fromBlock(64),QuartPos.fromBlock(position.getMiddleBlockZ()),state.randomState().sampler());
        if(!structure.biomes().contains(biome))return CompletableFuture.completedFuture(origin);

        var terrain=new WorldGenRegion(level,cache,step,origin);
        var manager=level.structureManager().forWorldGenRegion(terrain);
        var generation=generationContext(structure,level,context.generator(),position,origin,false);
        synchronized(GeneratedLegacyFeatures.sourceGenerationLock()){
            List<GeneratedPlanPiece> pieces=GeneratedLegacyFeatures.plan(generation,generation.random(),terrain);
            var start=nativeStart(structure,position,0,pieces);
            var targets=new LinkedHashMap<ChunkPos,ChunkAccess>();
            for(var piece:pieces){
                var box=piece.getBoundingBox();
                int minX=box.minX()>>4,maxX=box.maxX()>>4,minZ=box.minZ()>>4,maxZ=box.maxZ()>>4;
                for(int x=minX;x<=maxX;x++)for(int z=minZ;z<=maxZ;z++){
                    if(position.getChessboardDistance(x,z)>4)throw new IllegalStateException("Source plan exceeds its native write bound at "+x+","+z);
                    var target=terrain.getChunk(x,z,ChunkStatus.FEATURES);
                    if(!SourceDecorationOwnership.admitted(target)){
                        // Reject the entire plan before any write; an old/new border must never make half a building.
                        LOGGER.debug("Rejected complete source plan at {}: target {} predates source generation admission",position,target.getPos());
                        SourceDecorationMetadata.publish(origin,structure,StructureStart.INVALID_START,Map.of());
                        level.onStructureStartsAvailable(origin);
                        return CompletableFuture.completedFuture(origin);
                    }
                    targets.putIfAbsent(target.getPos(),target);
                }
            }
            for(var entry:targets.entrySet())
                start.placeInChunk(terrain,manager,context.generator(),generation.random(),writableArea(entry.getValue()),entry.getKey());
            SourceDecorationMetadata.publish(origin,structure,start,targets);
            level.onStructureStartsAvailable(origin);
        }
        return CompletableFuture.completedFuture(origin);
    }

    /** Explicit native /place caller: its already-ready real terrain is allowed to include old chunks. */
    public static StructureStart planDirect(LegacyGeneratedStructure structure,ServerLevel level,ChunkGenerator generator,ChunkPos origin,int referenceCount){
        if(!NaturalStructures.oldBuildingsEnabled(level.getServer().getWorldData().worldGenOptions().generateStructures()))return StructureStart.INVALID_START;
        var generation=generationContext(structure,level,generator,origin,level,true);
        synchronized(GeneratedLegacyFeatures.sourceGenerationLock()){
            return nativeStart(structure,origin,referenceCount,GeneratedLegacyFeatures.plan(generation,generation.random(),level));
        }
    }

    private static Structure.GenerationContext generationContext(LegacyGeneratedStructure structure,ServerLevel level,ChunkGenerator generator,ChunkPos origin,net.minecraft.world.level.LevelHeightAccessor height,boolean direct){
        return new Structure.GenerationContext(level.registryAccess(),generator,generator.getBiomeSource(),level.getChunkSource().randomState(),level.getStructureManager(),level.getSeed(),origin,height,direct?biome->true:structure.biomes()::contains);
    }

    private static StructureStart nativeStart(LegacyGeneratedStructure structure,ChunkPos origin,int references,List<GeneratedPlanPiece> pieces){
        return pieces.isEmpty()?StructureStart.INVALID_START:new StructureStart(structure,origin,references,new PiecesContainer(List.copyOf(pieces)));
    }

    private static BoundingBox writableArea(ChunkAccess chunk){
        ChunkPos position=chunk.getPos();
        var height=chunk.getHeightAccessorForGeneration();
        return new BoundingBox(position.getMinBlockX(),height.getMinBuildHeight()+1,position.getMinBlockZ(),position.getMaxBlockX(),height.getMaxBuildHeight()-1,position.getMaxBlockZ());
    }

    private SourceDecoration(){}
}
