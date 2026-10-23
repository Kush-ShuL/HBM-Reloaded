package com.hbm.machine.fensu;

import java.util.List;
import net.minecraft.core.*;

/** Source getPortPos/getConPos and physical rotatePorts, expressed relative to the actual offset core. */
public final class FensuPorts {
    public record Port(BlockPos pos,Direction outward){}
    public static List<Port> at(BlockPos core,Direction facing){
        // Official ForgeDirection.ROTATION_MATRIX[UP][facing]: N→E,S→W,W→N,E→S.
        var rot=facing.getClockWise();
        return List.of(new Port(core.relative(facing,2).relative(rot,2),facing),new Port(core.relative(facing,2).relative(rot,-2),facing),new Port(core.relative(facing,-2).relative(rot,2),facing.getOpposite()),new Port(core.relative(facing,-2).relative(rot,-2),facing.getOpposite()),new Port(core.relative(rot,4),rot),new Port(core.relative(rot,-4),rot.getOpposite()));
    }
    private FensuPorts(){}
}
