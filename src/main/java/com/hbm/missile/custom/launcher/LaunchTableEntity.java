package com.hbm.missile.custom.launcher;

import api.hbm.item.IDesignatorItem;
import com.hbm.api.computer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;

/** Source ntm_custom_launch_pad six real OC callbacks, through the existing sole CC control contract. */
public final class LaunchTableEntity extends CustomLauncherEntity implements ComputerControl {
    public LaunchTableEntity(BlockPos pos, BlockState state) { super(CustomLauncherContent.TABLE_ENTITY.get(), pos, state, true); }
    @Override public String computerType() { return "ntm_custom_launch_pad"; }
    @Override public String[] computerMethods() { return new String[]{"getEnergyInfo", "getContents", "getLaunchInfo", "getCoords", "setCoords", "launch"}; }
    @Override public Object[] callComputer(String method, ComputerArguments args) {
        return switch (method) {
            case "getEnergyInfo" -> new Object[]{power, MAX_POWER};
            case "getContents" -> new Object[]{tanks[0].getFill(), tanks[0].getMaxFill(), tanks[0].getTankType().getUnlocalizedName(), tanks[1].getFill(), tanks[1].getMaxFill(), tanks[1].getTankType().getUnlocalizedName(), solid, maxSolid};
            case "getLaunchInfo" -> new Object[]{canLaunch(), isMissileValid(), hasDesignator(), hasFuel()};
            case "getCoords" -> {
                if (!(slots[1].getItem() instanceof IDesignatorItem)) yield new Object[]{false, "Designator not found"};
                if (!slots[1].has(DataComponents.CUSTOM_DATA)) yield new Object[]{false};
                var tag = slots[1].get(DataComponents.CUSTOM_DATA).copyTag(); yield new Object[]{tag.getInt("xCoord"), tag.getInt("zCoord")};
            }
            case "setCoords" -> {
                if (!(slots[1].getItem() instanceof IDesignatorItem)) yield new Object[]{false, "Designator not found"};
                var tag = new CompoundTag(); tag.putInt("xCoord", args.checkInteger(0)); tag.putInt("zCoord", args.checkInteger(1));
                slots[1].set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); setChanged(); yield new Object[]{true};
            }
            // Source returns true after an eligible attempt, even when no designator made the large-pad attempt do nothing.
            case "launch" -> { if (!canLaunch()) yield new Object[]{false}; launchFromDesignator(); yield new Object[]{true}; }
            default -> throw new ComputerException("No such method");
        };
    }
}
