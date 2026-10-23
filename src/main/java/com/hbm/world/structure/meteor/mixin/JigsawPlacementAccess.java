package com.hbm.world.structure.meteor.mixin;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value=JigsawPlacement.class,remap=false)
public interface JigsawPlacementAccess {
    @Invoker("addPieces") static void hbm$placeChildren(RandomState state,int depth,boolean expansion,ChunkGenerator generator,StructureTemplateManager templates,LevelHeightAccessor height,RandomSource random,Registry<StructureTemplatePool> pools,PoolElementStructurePiece root,List<PoolElementStructurePiece> pieces,VoxelShape free,PoolAliasLookup aliases,LiquidSettings liquid){throw new AssertionError();}
}
