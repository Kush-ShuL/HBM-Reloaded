package com.hbm.world.structure.generated;
import com.hbm.world.structure.NaturalStructures;
import com.hbm.world.structure.generated.compat.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
/** Native persistent vanilla piece, preserving all source per-room flags, paths, frequencies and geometry. */
public final class GeneratedPiece extends StructurePiece {
    private static final Object SOURCE_SELECTORS=new Object();
    private final StructureComponent algorithm;private final GeneratedDependencies dependencies;
    public GeneratedPiece(StructureComponent algorithm,GeneratedDependencies dependencies,LevelHeightAccessor height){super(GeneratedStructures.PIECE.get(),algorithm.getComponentType(),new BoundingBox(algorithm.getBoundingBox().minX,height.getMinBuildHeight(),algorithm.getBoundingBox().minZ,algorithm.getBoundingBox().maxX,height.getMaxBuildHeight()-1,algorithm.getBoundingBox().maxZ));this.algorithm=algorithm;this.dependencies=dependencies;}
    public GeneratedPiece(StructurePieceSerializationContext context,CompoundTag tag){super(GeneratedStructures.PIECE.get(),tag);algorithm=GeneratedStructures.component(tag.getString("sourceAlgorithm"));algorithm.load(tag.getCompound("sourceState"));dependencies=GeneratedDependencies.load(tag.getCompound("sourceDependencies"));}
    @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context,CompoundTag tag){tag.putString("sourceAlgorithm",algorithm.getClass().getSimpleName());tag.put("sourceState",algorithm.save());tag.put("sourceDependencies",dependencies.save());}
    @Override public BlockPos getLocatorPosition(){var box=algorithm.getBoundingBox();return new BlockPos(box.minX,box.minY,box.minZ);}
    @Override public synchronized void postProcess(WorldGenLevel level,StructureManager manager,ChunkGenerator generator,RandomSource random,BoundingBox clip,ChunkPos chunk,BlockPos pivot){if(!NaturalStructures.enabled()||level.getLevel().dimension()!=Level.OVERWORLD)return;dependencies.requireRegistryAndHooks();dependencies.requirePools(level.getLevel());var sourceClip=new StructureBoundingBox(clip);synchronized(SOURCE_SELECTORS){com.hbm.world.structure.generated.compat.StructureConfig.lootAmountFactor=NaturalStructures.lootAmountFactor();algorithm.addComponentParts(new com.hbm.world.structure.generated.compat.World(level,sourceClip,new SourceRandom(level.getRandom())),new SourceRandom(random),sourceClip);}}
}
