package com.hbm.world.structure.mixin;

import com.hbm.world.structure.SourceDecorationBootstrap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.UnaryOperator;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/** A true post-FEATURES source step, with every source writer complete before lighting. */
@Mixin(value = ChunkPyramid.class, remap = false)
public abstract class SourceDecorationPyramidMixin {
    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;step(Lnet/minecraft/world/level/chunk/status/ChunkStatus;Ljava/util/function/UnaryOperator;)Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;", ordinal = 0),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;INITIALIZE_LIGHT:Lnet/minecraft/world/level/chunk/status/ChunkStatus;", opcode = Opcodes.GETSTATIC, ordinal = 0),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkPyramid;GENERATION_PYRAMID:Lnet/minecraft/world/level/chunk/status/ChunkPyramid;", opcode = Opcodes.PUTSTATIC)),
            require = 1, expect = 1, allow = 1)
    private static ChunkPyramid.Builder hbm$sourceGenerationStep(
            ChunkPyramid.Builder builder, ChunkStatus status,
            UnaryOperator<ChunkStep.Builder> configure, Operation<ChunkPyramid.Builder> original) {
        if (status != ChunkStatus.INITIALIZE_LIGHT)
            throw new IllegalStateException("Native generation pyramid INITIALIZE_LIGHT seam changed");
        var source = SourceDecorationBootstrap.status();
        // Native FEATURES may write one chunk beyond its center: finish every
        // such writer that can touch the source body's proven radius4 terrain.
        builder.step(source, step -> step.addRequirement(ChunkStatus.FEATURES, 5)
                .blockStateWriteRadius(4).setTask(SourceDecorationBootstrap::generate));
        return original.call(builder, status,
                (UnaryOperator<ChunkStep.Builder>) step -> configure.apply(step).addRequirement(source, 4));
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;step(Lnet/minecraft/world/level/chunk/status/ChunkStatus;Ljava/util/function/UnaryOperator;)Lnet/minecraft/world/level/chunk/status/ChunkPyramid$Builder;", ordinal = 0),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;INITIALIZE_LIGHT:Lnet/minecraft/world/level/chunk/status/ChunkStatus;", opcode = Opcodes.GETSTATIC, ordinal = 1),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkPyramid;LOADING_PYRAMID:Lnet/minecraft/world/level/chunk/status/ChunkPyramid;", opcode = Opcodes.PUTSTATIC)),
            require = 1, expect = 1, allow = 1)
    private static ChunkPyramid.Builder hbm$sourceLoadingStep(
            ChunkPyramid.Builder builder, ChunkStatus status,
            UnaryOperator<ChunkStep.Builder> configure, Operation<ChunkPyramid.Builder> original) {
        if (status != ChunkStatus.INITIALIZE_LIGHT)
            throw new IllegalStateException("Native loading pyramid INITIALIZE_LIGHT seam changed");
        var source = SourceDecorationBootstrap.status();
        builder.step(source, UnaryOperator.identity());
        return original.call(builder, status,
                (UnaryOperator<ChunkStep.Builder>) step -> configure.apply(step).addRequirement(source, 4));
    }
}
