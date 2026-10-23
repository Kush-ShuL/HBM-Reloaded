package com.hbm.machine.reactor.pile.core;
import net.minecraft.core.Direction;
/** Original ForgeDirection order and left-handed rotation, including UNKNOWN persisted value. */
public enum PileDirection {
 DOWN(0,-1,0),UP(0,1,0),NORTH(0,0,-1),SOUTH(0,0,1),WEST(-1,0,0),EAST(1,0,0),UNKNOWN(0,0,0);
 public final int offsetX,offsetY,offsetZ;PileDirection(int x,int y,int z){offsetX=x;offsetY=y;offsetZ=z;}
 private static final int[][] ROT={{0,1,4,5,3,2,6},{0,1,5,4,2,3,6},{5,4,2,3,0,1,6},{4,5,2,3,1,0,6},{2,3,1,0,4,5,6},{3,2,0,1,4,5,6},{0,1,2,3,4,5,6}};
 public static PileDirection getOrientation(int index){return index<0||index>=6?UNKNOWN:values()[index];}
 public static PileDirection of(Direction dir){return values()[dir.ordinal()];}
 public PileDirection getOpposite(){return this==UNKNOWN?UNKNOWN:values()[ordinal()^1];}
 public PileDirection getRotation(PileDirection axis){return values()[ROT[axis.ordinal()][ordinal()]];}
 public Direction nativeDirection(){return this==UNKNOWN?null:Direction.values()[ordinal()];}
}
