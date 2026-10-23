package com.hbm.machine.lambda;

import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;

/** Exactly LaunchpadLambda's XR base plus four perimeter strips. No elevated Soyuz construction. */
public final class LambdaLayout {
    public static final int[][] DIMENSIONS={{1,0,7,7,7,7},{2,-2,7,-6,7,7},{2,-2,-6,7,7,7},{2,-2,7,7,7,-6},{2,-2,7,7,-6,7}};
    public static List<BlockPos> parts(BlockPos core,Direction facing){
        var result=new LinkedHashSet<BlockPos>();
        for(var dims:DIMENSIONS){var d=DummyableBlock.rotate(dims,facing);for(int x=-d[4];x<=d[5];x++)for(int y=-d[1];y<=d[0];y++)for(int z=-d[2];z<=d[3];z++)result.add(core.offset(x,y,z));}
        return List.copyOf(result);
    }
    /** Source dir.getRotation(UP) is the clockwise horizontal direction. */
    public static List<BlockPos> ports(BlockPos core,Direction facing){var result=new ArrayList<BlockPos>();var rot=facing.getClockWise();for(int i=2;i<=6;i++)result.add(core.relative(facing,7).relative(rot,-i).above());return List.copyOf(result);}
    private LambdaLayout(){}
}
