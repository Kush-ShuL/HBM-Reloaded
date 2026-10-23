package com.hbm.machine.reactor.rbmk.core;
import net.minecraft.world.level.block.entity.BlockEntity;
/** Strict roles supplied by the actual column block entities, never a synthetic registered machine. */
public interface RbmkFluxColumn {
 enum Type {ROD,MODERATOR,CONTROL_ROD,REFLECTOR,ABSORBER,OUTGASSER,OTHER}
 BlockEntity entity();Type neutronType();boolean hasLid();boolean isModerated();
 interface Receiver extends RbmkFluxColumn {void receiveFlux(RbmkNeutronField.Stream stream);}
 interface Fuel extends Receiver {boolean hasFuelRod();double lastFluxQuantity();boolean reasimFuel();}
 interface Outgasser extends Receiver {boolean canProcess();}
 interface Control extends RbmkFluxColumn {double controlLevel();double controlMultiplier();}
 interface Absorber extends RbmkFluxColumn {void addHeat(double heat);}
}
