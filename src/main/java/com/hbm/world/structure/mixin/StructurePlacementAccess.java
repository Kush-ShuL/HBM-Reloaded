package com.hbm.world.structure.mixin;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** The actual datapack salt is protected; no reflective access or duplicate setting. */
@Mixin(value=StructurePlacement.class,remap=false)
public interface StructurePlacementAccess {@Accessor("salt") int hbm$sourceSalt();}
