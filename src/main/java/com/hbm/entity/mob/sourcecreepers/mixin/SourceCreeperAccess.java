package com.hbm.entity.mob.sourcecreepers.mixin;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=Creeper.class,remap=false)
public interface SourceCreeperAccess {@Accessor("maxSwell")void hbm$setFuseTime(int ticks);}
