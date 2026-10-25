package com.hbm.world.structure.mixin;

import com.hbm.world.structure.LegacyGeneratedStructure;
import com.hbm.world.structure.NaturalStructures;
import com.hbm.world.structure.SourceDecorationBootstrap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** The source body exists only after decoration, including for a native locate candidate. */
@Mixin(value = ChunkGenerator.class, remap = false)
public abstract class SourceStructureLocateMixin {
    @WrapOperation(method = "getStructureGeneratingAt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/StructureManager;checkStructurePresence(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/Structure;Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;Z)Lnet/minecraft/world/level/levelgen/structure/StructureCheckResult;"))
    private static StructureCheckResult hbm$completedSourceCandidate(
            StructureManager manager, ChunkPos position, Structure structure, StructurePlacement placement,
            boolean skipReferenced, Operation<StructureCheckResult> original,
            Set<Holder<Structure>> candidates, LevelReader reader, StructureManager actualManager,
            boolean actualSkipReferenced, StructurePlacement actualPlacement, ChunkPos actualPosition) {
        if (!(structure instanceof LegacyGeneratedStructure) || !(reader instanceof ServerLevel level))
            return original.call(manager, position, structure, placement, skipReferenced);
        if (level.dimension() != Level.OVERWORLD
                || !NaturalStructures.oldBuildingsEnabled(manager.shouldGenerateStructures()))
            return StructureCheckResult.START_NOT_PRESENT;

        // Early STRUCTURE_STARTS legitimately cached absence. Resolve this real native stage
        // before consulting that cache, without running a chance probe or creating a marker.
        var chunk = level.getChunk(position.x, position.z, SourceDecorationBootstrap.status(), true);
        var start = chunk.getStartForStructure(structure);
        return start != null && start.isValid() && (!skipReferenced || start.canBeReferenced())
                ? StructureCheckResult.START_PRESENT : StructureCheckResult.START_NOT_PRESENT;
        // The native caller still performs its own tryAddReference when skipReferenced is true.
    }
}
