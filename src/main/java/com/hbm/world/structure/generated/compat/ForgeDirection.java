package com.hbm.world.structure.generated.compat;
import net.minecraft.core.Direction;
/** Source ordinal order and Forge1.7.10 left-hand rotations, including the UNKNOWN axis. */
public enum ForgeDirection {
    DOWN(0,-1,0),UP(0,1,0),NORTH(0,0,-1),SOUTH(0,0,1),WEST(-1,0,0),EAST(1,0,0),UNKNOWN(0,0,0);
    // Original ForgeDirection.ROTATION_MATRIX[axis][direction]; an ordinary cross product rotates the other way.
    public static final int[][] ROTATION_MATRIX={
        {0,1,4,5,3,2,6},
        {0,1,5,4,2,3,6},
        {5,4,2,3,0,1,6},
        {4,5,2,3,1,0,6},
        {2,3,1,0,4,5,6},
        {3,2,0,1,4,5,6},
        {0,1,2,3,4,5,6}
    };
    public final int offsetX,offsetY,offsetZ;ForgeDirection(int x,int y,int z){offsetX=x;offsetY=y;offsetZ=z;}
    public ForgeDirection getOpposite(){return ordinal()<6?values()[ordinal()^1]:UNKNOWN;}
    public static ForgeDirection getOrientation(int ordinal){return ordinal>=0&&ordinal<6?values()[ordinal]:UNKNOWN;}
    public ForgeDirection getRotation(ForgeDirection axis){return getOrientation(ROTATION_MATRIX[axis.ordinal()][ordinal()]);}
    public Direction nativeDirection(){return Direction.from3DDataValue(ordinal());}
}
