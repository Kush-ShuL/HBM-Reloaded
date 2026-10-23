package com.hbm.world.structure.meteor.mixin;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets="net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$PieceState",remap=false)
public interface MeteorPieceStateAccess {
    @Accessor("piece") PoolElementStructurePiece hbm$piece();
}
