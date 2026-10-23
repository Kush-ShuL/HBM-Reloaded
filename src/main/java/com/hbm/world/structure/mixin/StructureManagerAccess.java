package com.hbm.world.structure.mixin;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=StructureManager.class,remap=false)
public interface StructureManagerAccess {@Accessor("level") LevelAccessor hbm$naturalLevel();}
