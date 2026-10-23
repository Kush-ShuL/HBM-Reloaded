package com.hbm.runtime.registry.mixin;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Numeric registry IDs may contain unmapped slots after NeoForge snapshot restoration. */
@Mixin(MappedRegistry.class)
abstract class SparseRegistryIterationMixin<T> {
    @Redirect(method = "iterator", at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/Iterators;transform(Ljava/util/Iterator;Lcom/google/common/base/Function;)Ljava/util/Iterator;",
            remap = false), require = 1)
    private Iterator<T> hbm$iterateAssignedIds(Iterator<Holder.Reference<T>> holders,
            Function<? super Holder.Reference<T>, ? extends T> value) {
        // Filter holes before Holder::value, retaining original numeric-ID order.
        // Bound holders still take the original value function, including its validation.
        return Iterators.transform(Iterators.filter(holders, Objects::nonNull), value);
    }

    @Inject(method = "holders", at = @At("RETURN"), cancellable = true, require = 1)
    private void hbm$streamAssignedIds(CallbackInfoReturnable<Stream<Holder.Reference<T>>> result) {
        result.setReturnValue(result.getReturnValue().filter(Objects::nonNull));
    }
}
