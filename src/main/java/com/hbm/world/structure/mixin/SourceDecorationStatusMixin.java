package com.hbm.world.structure.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.EnumSet;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/** Register before INITIALIZE_LIGHT is constructed, keeping native parent/index derivation. */
@Mixin(value = ChunkStatus.class, remap = false)
public abstract class SourceDecorationStatusMixin {
    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;register(Ljava/lang/String;Lnet/minecraft/world/level/chunk/status/ChunkStatus;Ljava/util/EnumSet;Lnet/minecraft/world/level/chunk/status/ChunkType;)Lnet/minecraft/world/level/chunk/status/ChunkStatus;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;FEATURES:Lnet/minecraft/world/level/chunk/status/ChunkStatus;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;INITIALIZE_LIGHT:Lnet/minecraft/world/level/chunk/status/ChunkStatus;", opcode = Opcodes.PUTSTATIC)),
            require = 1, expect = 1, allow = 1)
    private static ChunkStatus hbm$insertSourceDecoration(
            String name, ChunkStatus parent, EnumSet<Heightmap.Types> heightmaps,
            ChunkType type, Operation<ChunkStatus> original) {
        if (!name.equals("initialize_light") || parent != ChunkStatus.FEATURES || type != ChunkType.PROTOCHUNK)
            throw new IllegalStateException("Native INITIALIZE_LIGHT registration changed before HBM source decoration");
        var source = original.call("hbm:source_decoration", parent, EnumSet.copyOf(heightmaps), type);
        return original.call(name, source, heightmaps, type);
    }
}
