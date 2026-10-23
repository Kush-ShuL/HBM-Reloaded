package com.hbm.weapon.sedna.mixin;

import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Original per-entity ranges need not be divisible by modern sixteen-block chunks. */
@Mixin(value=ChunkMap.class,remap=false)
public abstract class ProjectileTrackingMixin {
    @ModifyVariable(method="addEntity",at=@At("STORE"),ordinal=0,require=1,allow=1)
    private int hbm$exactOriginalRange(int range,Entity entity){return entity instanceof LegacyTrackingRange original?original.legacyTrackingRange():range;}
}
