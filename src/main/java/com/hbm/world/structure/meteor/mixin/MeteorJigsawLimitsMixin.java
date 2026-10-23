package com.hbm.world.structure.meteor.mixin;

import com.hbm.world.structure.meteor.MeteorJigsawLimits;
import com.hbm.world.structure.meteor.MeteorWeightedTemplates;
import com.hbm.world.structure.meteor.MeteorConnectorPolicy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SequencedPriorityIterator;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer",remap=false)
public abstract class MeteorJigsawLimitsMixin {
    @Shadow @Final private StructureTemplateManager structureTemplateManager;
    @Shadow @Final private SequencedPriorityIterator<?> placing;
    @Unique private boolean hbm$fallbackOnly;
    @Unique private PoolElementStructurePiece hbm$parent;
    @Unique private Vec3i hbm$target;
    @Unique private StructureTemplate.StructureBlockInfo hbm$outgoing;
    @Unique private MeteorConnectorPolicy.Selection hbm$incoming;
    @Inject(method="tryPlacingChildren",at=@At("HEAD"))
    private void hbm$parentLimits(PoolElementStructurePiece piece,org.apache.commons.lang3.mutable.MutableObject<net.minecraft.world.phys.shapes.VoxelShape> free,int depth,boolean expansion,net.minecraft.world.level.LevelHeightAccessor height,net.minecraft.world.level.levelgen.RandomState randomState,net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup aliases,net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings liquids,CallbackInfo ci){
        MeteorJigsawLimits.registerQueue(placing);
        hbm$fallbackOnly=MeteorJigsawLimits.fallbackOnly(piece);hbm$parent=piece;hbm$target=null;hbm$outgoing=null;hbm$incoming=null;MeteorWeightedTemplates.reset();
    }
    @Inject(method="tryPlacingChildren",at=@At("RETURN"))
    private void hbm$clearCandidate(CallbackInfo ci){MeteorWeightedTemplates.reset();}
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getShuffledJigsawBlocks(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/util/RandomSource;)Ljava/util/List;",ordinal=0))
    private List<StructureTemplate.StructureBlockInfo> hbm$sourceOutgoingGroups(StructurePoolElement element,StructureTemplateManager manager,BlockPos position,Rotation rotation,RandomSource random){
        return MeteorJigsawLimits.active()?MeteorConnectorPolicy.outgoing(hbm$parent,manager,random,info->hbm$outgoing=info):element.getShuffledJigsawBlocks(manager,position,rotation,random);
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/block/Rotation;getShuffled(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private List<Rotation> hbm$sourceSingleRotation(RandomSource random){
        if(!MeteorJigsawLimits.active())return Rotation.getShuffled(random);
        hbm$incoming=MeteorConnectorPolicy.incoming(hbm$outgoing,hbm$parent.getRotation(),MeteorWeightedTemplates.candidate(),structureTemplateManager,random);
        return hbm$incoming==null?List.of():List.of(hbm$incoming.rotation());
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getShuffledJigsawBlocks(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/util/RandomSource;)Ljava/util/List;",ordinal=1))
    private List<StructureTemplate.StructureBlockInfo> hbm$sourceSingleConnector(StructurePoolElement element,StructureTemplateManager manager,BlockPos position,Rotation rotation,RandomSource random){
        if(!MeteorJigsawLimits.active())return element.getShuffledJigsawBlocks(manager,position,rotation,random);
        return hbm$incoming==null?List.of():List.of(hbm$incoming.transformed(position));
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean hbm$rememberUsedEntrance(List<Object> pieces,Object value){
        if(MeteorJigsawLimits.active()&&hbm$incoming!=null&&value instanceof PoolElementStructurePiece piece)MeteorJigsawLimits.rememberEntrance(piece,hbm$incoming.connector().pos());
        return pieces.add(value);
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private ArrayList<StructurePoolElement> hbm$deferPoolDraws(){return MeteorJigsawLimits.active()?MeteorWeightedTemplates.collectingList():new ArrayList<>();}
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;",ordinal=0))
    private List<StructurePoolElement> hbm$keepOnlyFallbackAtSourceLimit(StructureTemplatePool pool,RandomSource random){
        if(hbm$fallbackOnly)return List.of();
        if(!MeteorJigsawLimits.active())return pool.getShuffledTemplates(random);
        return MeteorWeightedTemplates.candidates(pool,random,false);
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;",ordinal=1))
    private List<StructurePoolElement> hbm$singleSourceFallback(StructureTemplatePool pool,RandomSource random){
        if(!MeteorJigsawLimits.active())return pool.getShuffledTemplates(random);
        //The source skips a blocked fallback target before it spends any pool/rotation draws.
        if(!MeteorJigsawLimits.mayDrawFallback(hbm$parent,hbm$target,hbm$fallbackOnly))return List.of();
        return MeteorWeightedTemplates.candidates(pool,random,true);
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/level/levelgen/structure/BoundingBox;isInside(Lnet/minecraft/core/Vec3i;)Z"))
    private boolean hbm$rememberConnectionTarget(BoundingBox bounds,Vec3i target){hbm$target=target;return bounds.isInside(target);}
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"))
    private boolean hbm$sourceCollision(VoxelShape free,VoxelShape candidate,BooleanOp operation){
        if(!MeteorJigsawLimits.active())return Shapes.joinIsNotEmpty(free,candidate,operation);
        //Vanilla tests AABB.of(integerBounds).deflate(.25); recover those exact inclusive block bounds.
        var box=candidate.bounds();var bounds=new BoundingBox(Mth.floor(box.minX),Mth.floor(box.minY),Mth.floor(box.minZ),Mth.ceil(box.maxX)-1,Mth.ceil(box.maxY)-1,Mth.ceil(box.maxZ)-1);
        return !MeteorJigsawLimits.canPlace(hbm$parent,bounds,MeteorWeightedTemplates.isFallback(),hbm$fallbackOnly,hbm$target);
    }
    @Redirect(method="tryPlacingChildren",at=@At(value="INVOKE",target="Lnet/minecraft/util/SequencedPriorityIterator;add(Ljava/lang/Object;I)V"))
    private void hbm$sourceFallbackEndsBranch(SequencedPriorityIterator<Object> iterator,Object state,int priority){
        if(MeteorJigsawLimits.active()){
            if(hbm$fallbackOnly||MeteorWeightedTemplates.isFallback())return;
        }
        iterator.add(state,priority);
    }
}
