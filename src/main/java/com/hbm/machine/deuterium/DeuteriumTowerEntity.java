package com.hbm.machine.deuterium;

import com.hbm.energy.NodeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Four original independent two-face HE PortDefs, with combined per-fluid transport. */
public final class DeuteriumTowerEntity extends DeuteriumEntity {
    public DeuteriumTowerEntity(BlockPos pos,BlockState state){super(DeuteriumContent.TOWER_ENTITY.get(),pos,state,true);}
    public static NodeConnection[] connections(BlockPos core,Direction dir){
        // Forge1.7 left-hand ROTATION_MATRIX: DOWN is modern counter-clockwise, UP clockwise.
        var rot=dir.getCounterClockWise();var back=core.relative(dir,-1);
        return new NodeConnection[]{point(back,dir.getOpposite()),point(back,rot.getOpposite()),
            point(back.relative(rot),dir.getOpposite()),point(back.relative(rot),rot),
            point(core,rot.getOpposite()),point(core,dir),point(core.relative(rot),dir),point(core.relative(rot),rot)};
    }
    private static NodeConnection point(BlockPos pos,Direction face){return new NodeConnection(pos.relative(face),face);}
}
