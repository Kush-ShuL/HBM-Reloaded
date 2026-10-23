package com.hbm.equipment.jetpack.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Actual source ArmorUtil.resetFlightTime private counter, with its native1.21 field name. */
@Mixin(value = ServerGamePacketListenerImpl.class, remap = false)
public interface JetpackFlightAccess {
    @Accessor("aboveGroundTickCount") void hbm$resetJetpackFloatingTicks(int value);
}
