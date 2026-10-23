package com.hbm.world.taint.mixin;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
/** Removed1.7 public falling-block constructor; unlike modern fall(), source removal happens on its first entity tick. */
@Mixin(value=FallingBlockEntity.class,remap=false)
public interface TaintFallingFactory {
 @Invoker("<init>") static FallingBlockEntity hbm$taintFalling(Level level,double x,double y,double z,BlockState state){throw new AssertionError("Taint falling-block constructor mixin missing");}
}
