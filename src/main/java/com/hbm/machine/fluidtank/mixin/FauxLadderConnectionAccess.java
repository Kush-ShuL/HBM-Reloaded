package com.hbm.machine.fluidtank.mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=ServerGamePacketListenerImpl.class,remap=false)
public interface FauxLadderConnectionAccess {
    @Accessor("aboveGroundTickCount") void hbm$resetFlightTime(int value);
}
