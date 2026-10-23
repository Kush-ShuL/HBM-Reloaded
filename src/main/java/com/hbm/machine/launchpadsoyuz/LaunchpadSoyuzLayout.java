package com.hbm.machine.launchpadsoyuz;

import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;

/** Generated from the actual20 source check/fill calls separately; relay origins are not replaced with global-core rays. */
public final class LaunchpadSoyuzLayout {
    public record Segment(int forward,int up,int right,int[] dimensions){public Segment{dimensions=dimensions.clone();}}
    public enum Side{FRONT,BACK,LEFT}
    private record PortSpec(int forward,int up,int right,Side side){}
    public record Port(BlockPos position,Direction side){}
    public static final Segment[] FILL={
        new Segment(0,0,0,new int[]{2,0,2,2,2,2}),
        new Segment(0,0,0,new int[]{3,0,2,1,1,2}),
        new Segment(0,2,0,new int[]{0,0,2,2,-2,10}),
        new Segment(0,2,0,new int[]{0,0,10,-2,2,10}),
        new Segment(0,0,0,new int[]{3,-3,9,1,1,9}),
        new Segment(0,2,0,new int[]{-1,2,2,2,-6,10}),
        new Segment(0,2,0,new int[]{-1,2,10,-6,2,2}),
        new Segment(0,2,0,new int[]{-1,2,10,-6,-6,10}),
        new Segment(2,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(2,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(-10,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(-10,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(0,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-7,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-4,2,0,new int[]{0,0,1,1,7,-3}),
        new Segment(-4,2,0,new int[]{1,-1,5,5,7,-3}),
        new Segment(-4,2,0,new int[]{3,-2,4,4,7,-3}),
        new Segment(-4,2,0,new int[]{6,-4,3,3,7,-3}),
        new Segment(-4,2,0,new int[]{51,-7,2,2,7,-3}),
        new Segment(0,0,5,new int[]{7,0,-2,3,2,2})
    };
    public static final Segment[] CHECK={
        new Segment(0,0,0,new int[]{2,0,2,2,2,2}),
        new Segment(0,0,0,new int[]{3,0,2,1,1,2}),
        new Segment(0,2,0,new int[]{0,0,2,2,-2,10}),
        new Segment(0,2,0,new int[]{0,0,10,-2,2,10}),
        new Segment(0,0,0,new int[]{3,-3,9,1,1,9}),
        new Segment(0,2,0,new int[]{-1,2,2,2,-6,10}),
        new Segment(0,2,0,new int[]{-1,2,10,-6,2,2}),
        new Segment(0,2,0,new int[]{-1,2,10,-6,-6,10}),
        new Segment(2,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(2,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(-10,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(-10,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(0,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-7,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-4,2,0,new int[]{0,0,1,1,7,-3}),
        new Segment(-4,2,0,new int[]{1,-1,5,5,7,-3}),
        new Segment(-4,2,0,new int[]{3,-2,4,4,7,-3}),
        new Segment(-4,2,0,new int[]{6,-4,3,3,7,-3}),
        new Segment(-4,2,0,new int[]{51,-7,2,2,7,-3}),
        new Segment(-4,0,0,new int[]{7,0,-6,7,7,-3})
    };
    public static final Segment[] PREVIEW={
        new Segment(0,0,0,new int[]{2,0,2,2,2,2}),
        new Segment(0,0,0,new int[]{3,0,2,1,1,2}),
        new Segment(0,0,0,new int[]{2,-2,2,2,-2,10}),
        new Segment(0,0,0,new int[]{2,-2,10,-2,2,10}),
        new Segment(0,0,0,new int[]{3,-3,9,1,1,9}),
        new Segment(0,0,0,new int[]{1,0,2,2,-6,10}),
        new Segment(0,0,0,new int[]{1,0,10,-6,2,2}),
        new Segment(0,0,0,new int[]{1,0,10,-6,-6,10}),
        new Segment(2,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(2,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(-10,0,0,new int[]{0,0,0,0,-10,58}),
        new Segment(-10,0,0,new int[]{1,-1,0,0,-56,58}),
        new Segment(0,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-6,0,0,new int[]{2,0,2,1,7,-3}),
        new Segment(-4,2,0,new int[]{0,0,1,1,7,-3}),
        new Segment(-4,2,0,new int[]{1,-1,5,5,7,-3}),
        new Segment(-4,2,0,new int[]{3,-2,4,4,7,-3}),
        new Segment(-4,2,0,new int[]{6,-4,3,3,7,-3}),
        new Segment(-4,2,0,new int[]{51,-7,2,2,7,-3}),
        new Segment(0,0,5,new int[]{7,0,-2,3,2,2})
    };
    private static final PortSpec[] PORTS={
        new PortSpec(2,1,1,Side.FRONT),
        new PortSpec(2,1,-1,Side.FRONT),
        new PortSpec(2,1,-7,Side.FRONT),
        new PortSpec(2,1,-9,Side.FRONT),
        new PortSpec(-10,1,1,Side.BACK),
        new PortSpec(-10,1,-1,Side.BACK),
        new PortSpec(-10,1,-7,Side.BACK),
        new PortSpec(-10,1,-9,Side.BACK),
        new PortSpec(1,1,-10,Side.LEFT),
        new PortSpec(-1,1,-10,Side.LEFT),
        new PortSpec(-7,1,-10,Side.LEFT),
        new PortSpec(-9,1,-10,Side.LEFT)
    };
    public static BlockPos origin(BlockPos core,Direction facing,int forward,int up,int right){return core.relative(facing,forward).relative(facing.getClockWise(),right).above(up);}
    public static List<BlockPos> checkedCells(BlockPos core,Direction facing){
        var result=new LinkedHashSet<BlockPos>();
        for(var segment:CHECK){var start=origin(core,facing,segment.forward,segment.up,segment.right);var d=DummyableBlock.rotate(segment.dimensions,facing);
            for(int x=-d[4];x<=d[5];x++)for(int y=-d[1];y<=d[0];y++)for(int z=-d[2];z<=d[3];z++){
                result.add(start.offset(x,y,z));
            }
        }
        return List.copyOf(result);
    }
    public static Map<BlockPos,Integer> placement(BlockPos core,Direction facing){
        var result=new LinkedHashMap<BlockPos,Integer>();result.put(core,facing.get3DDataValue()+10);
        for(var segment:FILL){var start=origin(core,facing,segment.forward,segment.up,segment.right);var d=DummyableBlock.rotate(segment.dimensions,facing);
            for(int x=-d[4];x<=d[5];x++)for(int y=-d[1];y<=d[0];y++)for(int z=-d[2];z<=d[3];z++){
                Direction away=y<0?Direction.DOWN:y>0?Direction.UP:x<0?Direction.WEST:x>0?Direction.EAST:z<0?Direction.NORTH:z>0?Direction.SOUTH:null;
                if(away!=null)result.put(start.offset(x,y,z),away.get3DDataValue());
            }
        }
        return result;
    }
    public static List<Port> ports(BlockPos core,Direction facing){
        return Arrays.stream(PORTS).map(port->new Port(origin(core,facing,port.forward,port.up,port.right),switch(port.side){case FRONT->facing;case BACK->facing.getOpposite();case LEFT->facing.getClockWise().getOpposite();})).toList();
    }
    private LaunchpadSoyuzLayout(){}
}
