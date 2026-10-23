package com.hbm.world.structure.meteor;

import com.hbm.world.structure.meteor.mixin.JigsawPlacementAccess;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

/** Real vanilla template pools, connection matching and collision placement. No legacy stitcher. */
public final class MeteorDungeonJigsaw {
    public static final ResourceLocation START_POOL=ResourceLocation.fromNamespaceAndPath("hbm","meteor/start");
    private static final ThreadLocal<RandomSource> SOURCE_RANDOM=new ThreadLocal<>();
    /** Scope the scheduler stream while delegating to the registered minecraft:jigsaw structure. */
    public static MeteorJigsawLimits.Scope usingSourceRandom(RandomSource random){
        var previous=SOURCE_RANDOM.get();SOURCE_RANDOM.set(random);
        return ()->{if(previous==null)SOURCE_RANDOM.remove();else SOURCE_RANDOM.set(previous);};
    }
    public static RandomSource sourceRandom(Structure.GenerationContext context){var random=SOURCE_RANDOM.get();return random==null?context.random():random;}
    /** sourceRandom must be the source scheduler's stream immediately after its weighted spawn draw. */
    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context,RandomSource sourceRandom){
        var pools=context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var start=pools.getHolderOrThrow(net.minecraft.resources.ResourceKey.create(Registries.TEMPLATE_POOL,START_POOL));
        var element=start.value().getRandomTemplate(sourceRandom);
        if(element==EmptyPoolElement.INSTANCE)return Optional.empty();
        Rotation rotation=Rotation.values()[sourceRandom.nextInt(4)];
        var templateManager=context.structureTemplateManager();
        var corner=new BlockPos(context.chunkPos().getMinBlockX(),32,context.chunkPos().getMinBlockZ());
        var zeroBounds=element.getBoundingBox(templateManager,BlockPos.ZERO,rotation);
        var origin=corner.offset(-zeroBounds.minX(),-zeroBounds.minY(),-zeroBounds.minZ());
        var root=new PoolElementStructurePiece(templateManager,element,origin,element.getGroundLevelDelta(),rotation,element.getBoundingBox(templateManager,origin,rotation),LiquidSettings.IGNORE_WATERLOGGING);
        return Optional.of(new Structure.GenerationStub(corner,builder->{
            var pieces=new ArrayList<PoolElementStructurePiece>();pieces.add(root);
            // Source tests each outgoing piece's center against128, rather than restricting all
            // template cells to a128-radius box. A roomy vanilla free volume leaves that rule
            // to the narrow per-parent limit hook and still uses vanilla collision subtraction.
            var extent=new AABB(corner.getX()-512,context.heightAccessor().getMinBuildHeight(),corner.getZ()-512,corner.getX()+513,context.heightAccessor().getMaxBuildHeight(),corner.getZ()+513);
            var free=Shapes.join(Shapes.create(extent),Shapes.create(AABB.of(root.getBoundingBox())),BooleanOp.ONLY_FIRST);
            try(var ignored=MeteorJigsawLimits.enter(corner,pieces,sourceRandom)){
                //1024 is a guard beyond the original total-count limit; it is deliberately not
                // the source sizeLimit128 interpreted as vanilla recursion depth.
                JigsawPlacementAccess.hbm$placeChildren(context.randomState(),1024,false,context.chunkGenerator(),templateManager,context.heightAccessor(),sourceRandom,pools,root,pieces,free,PoolAliasLookup.EMPTY,LiquidSettings.IGNORE_WATERLOGGING);
            }
            pieces.forEach(builder::addPiece);
        }));
    }
    private MeteorDungeonJigsaw(){}
}
