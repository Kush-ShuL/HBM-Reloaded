package com.hbm.pneumatic.anchor;
import com.hbm.block.pipe.NativePipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
/** Actual existing WrenchItem bridge across FLUID, EXHAUST and PNEUMATIC source pipeline families. */
public final class AnchorInteractions {
 public static boolean isAnchor(BlockEntity tile){return tile instanceof PneumaticAnchorBlockEntity||tile instanceof NativePipeBlockEntity pipe&&pipe.kind().anchor();}
 public static int canLink(BlockEntity first,BlockEntity second){if(first instanceof PneumaticAnchorBlockEntity a&&second instanceof PneumaticAnchorBlockEntity b)return PneumaticAnchorBlockEntity.canLink(a,b);if(first instanceof NativePipeBlockEntity a&&second instanceof NativePipeBlockEntity b)return NativePipeBlockEntity.canLink(a,b);return 1;}
 public static void addConnection(BlockEntity owner,BlockPos pos){if(owner instanceof PneumaticAnchorBlockEntity anchor)anchor.addConnection(pos);else if(owner instanceof NativePipeBlockEntity pipe)pipe.addConnection(pos);else throw new IllegalArgumentException("Not a real source pipeline anchor");}
 private AnchorInteractions(){}
}
