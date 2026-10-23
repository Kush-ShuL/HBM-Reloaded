package com.hbm.machine.pa.cooled;
import java.util.*;
import net.minecraft.core.*;
/** Exact TilePort groups/face coordinates from each source BE; no unioning independent external HE/fluid networks. */
public final class PaPorts{
    public record Port(BlockPos position,Direction side){}
    public static List<Port> positions(PaKind kind,BlockPos core,Direction facing){var out=new ArrayList<Port>();Direction rot=facing.getClockWise(),axis=facing.getCounterClockWise();switch(kind){case SOURCE->{for(int sign:new int[]{1,-1})for(int lateral:new int[]{0,2,-2})out.add(new Port(core.relative(facing,sign).relative(rot,lateral),sign>0?facing:facing.getOpposite()));out.add(new Port(core.relative(rot,4),rot));for(int lateral:new int[]{0,2,-2})out.add(new Port(core.relative(rot,lateral).below(),Direction.DOWN));}case RFC->{for(int y:new int[]{1,-1})for(int longitudinal:new int[]{3,-3,0})out.add(new Port(core.relative(axis,longitudinal).offset(0,y,0),y>0?Direction.UP:Direction.DOWN));}case QUADRUPOLE->{out.add(new Port(core.above(),Direction.UP));out.add(new Port(core.below(),Direction.DOWN));out.add(new Port(core.relative(facing),facing));out.add(new Port(core.relative(facing,-1),facing.getOpposite()));}case DIPOLE->{for(int y:new int[]{1,-1})for(var side:Direction.Plane.HORIZONTAL)out.add(new Port(core.relative(side).offset(0,y,0),y>0?Direction.UP:Direction.DOWN));}case DETECTOR->{BlockPos center=core.relative(rot,-4);for(int y:new int[]{0,1,-1})out.add(new Port(center.offset(0,y,0),rot.getOpposite()));out.add(new Port(center.relative(facing),rot.getOpposite()));out.add(new Port(center.relative(facing,-1),rot.getOpposite()));}}return List.copyOf(out);}
    private PaPorts(){}
}
