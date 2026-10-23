package com.hbm.entity.mob.undead.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Records the modern event metadata without adding modern random spawn traits. */
@Mixin(Mob.class)
public interface UndeadSpawnTypeAccess {
    @Accessor("spawnType") void hbm$sourceSpawnType(MobSpawnType value);
}
