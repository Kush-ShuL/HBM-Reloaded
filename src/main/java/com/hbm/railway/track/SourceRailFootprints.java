package com.hbm.railway.track;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
/** Source check and fill are deliberately separate: wide7 and flipped switch do not use identical footprints. */
final class SourceRailFootprints {
 record Cell(BlockPos pos,int meta){}
 static boolean check(SourceRailKind kind,Level world,BlockPos origin,ForgeDirection direction){int x=origin.getX(),y=origin.getY(),z=origin.getZ(),o=-kind.offset;return switch(kind){
case NARROW_STRAIGHT -> check_NARROW_STRAIGHT(world,x,y,z,direction,o);
case NARROW_CURVE -> check_NARROW_CURVE(world,x,y,z,direction,o);
case STRAIGHT -> check_STRAIGHT(world,x,y,z,direction,o);
case SHORT -> check_SHORT(world,x,y,z,direction,o);
case CURVE -> check_CURVE(world,x,y,z,direction,o);
case CURVE7 -> check_CURVE7(world,x,y,z,direction,o);
case CURVE9 -> check_CURVE9(world,x,y,z,direction,o);
case RAMP -> check_RAMP(world,x,y,z,direction,o);
case BUFFER -> check_BUFFER(world,x,y,z,direction,o);
case SWITCH -> check_SWITCH(world,x,y,z,direction,o);
case SWITCH_FLIPPED -> check_SWITCH_FLIPPED(world,x,y,z,direction,o);
};}
static List<Cell> cells(SourceRailKind kind,BlockPos origin,ForgeDirection direction){var cells=new ArrayList<Cell>();int x=origin.getX(),y=origin.getY(),z=origin.getZ(),o=-kind.offset;switch(kind){
case NARROW_STRAIGHT -> fill_NARROW_STRAIGHT(cells,x,y,z,direction,o);
case NARROW_CURVE -> fill_NARROW_CURVE(cells,x,y,z,direction,o);
case STRAIGHT -> fill_STRAIGHT(cells,x,y,z,direction,o);
case SHORT -> fill_SHORT(cells,x,y,z,direction,o);
case CURVE -> fill_CURVE(cells,x,y,z,direction,o);
case CURVE7 -> fill_CURVE7(cells,x,y,z,direction,o);
case CURVE9 -> fill_CURVE9(cells,x,y,z,direction,o);
case RAMP -> fill_RAMP(cells,x,y,z,direction,o);
case BUFFER -> fill_BUFFER(cells,x,y,z,direction,o);
case SWITCH -> fill_SWITCH(cells,x,y,z,direction,o);
case SWITCH_FLIPPED -> fill_SWITCH_FLIPPED(cells,x,y,z,direction,o);
}return cells;}
static boolean check_NARROW_STRAIGHT(Level world,int x,int y,int z,ForgeDirection dir,int o){return checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.NARROW_STRAIGHT.dimensions,x,y,z,dir);
}
static void fill_NARROW_STRAIGHT(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.NARROW_STRAIGHT.dimensions,dir);
}
static boolean check_NARROW_CURVE(Level world,int x,int y,int z,ForgeDirection dir,int o){return checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.NARROW_CURVE.dimensions,x,y,z,dir);
}
static void fill_NARROW_CURVE(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.NARROW_CURVE.dimensions,dir);
}
static boolean check_STRAIGHT(Level world,int x,int y,int z,ForgeDirection dir,int o){return checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.STRAIGHT.dimensions,x,y,z,dir);
}
static void fill_STRAIGHT(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.STRAIGHT.dimensions,dir);
}
static boolean check_SHORT(Level world,int x,int y,int z,ForgeDirection dir,int o){return checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SHORT.dimensions,x,y,z,dir);
}
static void fill_SHORT(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SHORT.dimensions,dir);
}
static boolean check_CURVE(Level world,int x,int y,int z,ForgeDirection dir,int o){
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;
		
		return replaceable(world,new BlockPos(x + dX, y, z + dZ)) &&
				replaceable(world,new BlockPos(x + rX, y, z + rZ)) &&
				replaceable(world,new BlockPos(x + dX + rX, y, z + dZ + rZ)) &&
				replaceable(world,new BlockPos(x + dX + rX * 2, y, z + dZ + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX, y, z + dZ * 2 + rZ)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX * 2, y, z + dZ * 2 + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX, y, z + dZ * 3 + rZ)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 2, y, z + dZ * 3 + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX * 3, y, z + dZ * 2 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 3, y, z + dZ * 3 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 4 + rX * 3, y, z + dZ * 4 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 4, y, z + dZ * 3 + rZ * 4)) &&
				replaceable(world,new BlockPos(x + dX * 4 + rX * 4, y, z + dZ * 4 + rZ * 4));
	
}
static void fill_CURVE(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		
		
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;
		
		cells.add(new Cell(new BlockPos(x + dX, y, z + dZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + rX, y, z + rZ),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX + rX, y, z + dZ + rZ),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX + rX * 2, y, z + dZ + rZ * 2),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2 + rX, y, z + dZ * 2 + rZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2 + rX * 2, y, z + dZ * 2 + rZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX, y, z + dZ * 3 + rZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX * 2, y, z + dZ * 3 + rZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2 + rX * 3, y, z + dZ * 2 + rZ * 3),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX * 3, y, z + dZ * 3 + rZ * 3),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX * 3, y, z + dZ * 4 + rZ * 3),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX * 4, y, z + dZ * 3 + rZ * 4),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX * 4, y, z + dZ * 4 + rZ * 4),rot.ordinal()));
		
		
	
}
static boolean check_CURVE7(Level world,int x,int y,int z,ForgeDirection dir,int o){
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;
		
		return replaceable(world,new BlockPos(x + dX, y, z + dZ)) &&
				replaceable(world,new BlockPos(x + rX, y, z + rZ)) &&
				replaceable(world,new BlockPos(x + dX + rX, y, z + dZ + rZ)) &&
				replaceable(world,new BlockPos(x + dX + rX * 2, y, z + dZ + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX, y, z + dZ * 2 + rZ)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX * 2, y, z + dZ * 2 + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX, y, z + dZ * 3 + rZ)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 2, y, z + dZ * 3 + rZ * 2)) &&
				replaceable(world,new BlockPos(x + dX * 2 + rX * 3, y, z + dZ * 2 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 3, y, z + dZ * 3 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 4 + rX * 3, y, z + dZ * 4 + rZ * 3)) &&
				replaceable(world,new BlockPos(x + dX * 3 + rX * 4, y, z + dZ * 3 + rZ * 4)) &&
				replaceable(world,new BlockPos(x + dX * 4 + rX * 4, y, z + dZ * 4 + rZ * 4));
	
}
static void fill_CURVE7(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		
		
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;

		cells.add(new Cell(new BlockPos(x + dX, y, z + dZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2, y, z + dZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + rX, y, z + rZ),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX + rX, y, z + dZ + rZ),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2 + rX, y, z + dZ * 2 + rZ),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX, y, z + dZ * 3 + rZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX, y, z + dZ * 4 + rZ),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 2 + rX * 2, y, z + dZ * 2 + rZ * 2),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX * 2, y, z + dZ * 3 + rZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX * 2, y, z + dZ * 4 + rZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 2, y, z + dZ * 5 + rZ * 2),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 3 + rX * 3, y, z + dZ * 3 + rZ * 3),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX * 3, y, z + dZ * 4 + rZ * 3),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 3, y, z + dZ * 5 + rZ * 3),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 4 + rX * 4, y, z + dZ * 4 + rZ * 4),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 4, y, z + dZ * 5 + rZ * 4),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 6 + rX * 4, y, z + dZ * 6 + rZ * 4),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 5, y, z + dZ * 5 + rZ * 5),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 6, y, z + dZ * 5 + rZ * 6),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 6 + rX * 5, y, z + dZ * 6 + rZ * 5),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 6 + rX * 6, y, z + dZ * 6 + rZ * 6),rot.ordinal()));
		
		
	
}
static boolean check_CURVE9(Level world,int x,int y,int z,ForgeDirection dir,int o){
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;
		
		int[][] dim = new int[][] {
			{1, 0},
			{2, 0},
			{0, 1},
			{1, 1},
			{2, 1},
			{3, 1},
			{4, 1},
			{2, 2},
			{3, 2},
			{4, 2},
			{5, 2},
			{4, 3},
			{5, 3},
			{5, 4},
			{6, 3},
			{6, 4},
			{7, 4},
			{6, 5},
			{7, 5},
			{6, 6},
			{7, 6},
			{7, 7},
			{7, 8},
			{8, 6},
			{8, 7},
			{8, 8},
		};
		
		for(int[] array : dim) {
			if(!replaceable(world,new BlockPos(x + dX * array[0] + rX * array[1], y, z + dZ * array[0] + rZ * array[1]))) return false;
		}
		
		return true;
	
}
static void fill_CURVE9(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		
		
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;
		
		int d = dir.ordinal();
		int r = rot.ordinal();
		
		int[][] dim = new int[][] {
			{1, 0, d},
			{2, 0, d},
			{0, 1, r},
			{1, 1, d},
			{2, 1, d},
			{3, 1, d},
			{4, 1, d},
			{2, 2, r},
			{3, 2, r},
			{4, 2, r},
			{5, 2, d},
			{4, 3, r},
			{5, 3, r},
			{5, 4, r},
			{6, 3, d},
			{6, 4, d},
			{7, 4, d},
			{6, 5, r},
			{7, 5, r},
			{6, 6, r},
			{7, 6, r},
			{7, 7, r},
			{7, 8, r},
			{8, 6, d},
			{8, 7, d},
			{8, 8, d},
		};
		
		for(int[] array : dim) {
			cells.add(new Cell(new BlockPos(x + dX * array[0] + rX * array[1], y, z + dZ * array[0] + rZ * array[1]),array[2]));
		}
		
		
	
}
static boolean check_RAMP(Level world,int x,int y,int z,ForgeDirection dir,int o){
		return checkRect(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, SourceRailKind.RAMP.dimensions, x, y, z, dir) &&
				checkRect(world, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, new int[] {1, -1, 2, 2, 1, 0}, x, y, z, dir);
	
}
static void fill_RAMP(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		addRect(cells, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, SourceRailKind.RAMP.dimensions, dir);
		addRect(cells, x + dir.offsetX * o, y + dir.offsetY * o, z + dir.offsetZ * o, new int[] {1, -1, 2, 2, 1, 0}, dir);
	
}
static boolean check_BUFFER(Level world,int x,int y,int z,ForgeDirection dir,int o){return checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.BUFFER.dimensions,x,y,z,dir);
}
static void fill_BUFFER(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.BUFFER.dimensions,dir);
}
static boolean check_SWITCH(Level world,int x,int y,int z,ForgeDirection dir,int o){
		if(!checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SWITCH.dimensions,x,y,z,dir)) return false;
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;

		for(int i = 0; i < 4; i++) if(!replaceable(world,new BlockPos(x + dX * (2 + i) + rX * 2, y, z + dZ * (2 + i) + rZ * 2))) return false;
		for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (4 + i) + rX * 3, y, z + dZ * (4 + i) + rZ * 3))) return false;
		if(!replaceable(world,new BlockPos(x + dX * 5 + rX * 4, y, z + dZ * 5 + rZ * 4))) return false;
		for(int j = 0; j < 2; j++) for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (6 + j) + rX * (3 + i), y, z + dZ * (6 + j) + rZ * (3 + i)))) return false;
		if(!replaceable(world,new BlockPos(x + dX * 7 + rX * 5, y, z + dZ * 7 + rZ * 5))) return false;
		for(int j = 0; j < 7; j++) for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (8 + j) + rX * (4 + i), y, z + dZ * (8 + j) + rZ * (4 + i)))) return false;
		
		return true;
	
}
static void fill_SWITCH(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SWITCH.dimensions,dir);
		
		
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;

		for(int i = 0; i < 4; i++) cells.add(new Cell(new BlockPos(x + dX * (2 + i) + rX * 2, y, z + dZ * (2 + i) + rZ * 2),rot.ordinal()));
		for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (4 + i) + rX * 3, y, z + dZ * (4 + i) + rZ * 3),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 4, y, z + dZ * 5 + rZ * 4),rot.ordinal()));
		for(int j = 0; j < 2; j++) for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (6 + j) + rX * (3 + i), y, z + dZ * (6 + j) + rZ * (3 + i)),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 7 + rX * 5, y, z + dZ * 7 + rZ * 5),rot.ordinal()));
		for(int j = 0; j < 7; j++) for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (8 + j) + rX * (4 + i), y, z + dZ * (8 + j) + rZ * (4 + i)),dir.ordinal()));
		
		
	
}
static boolean check_SWITCH_FLIPPED(Level world,int x,int y,int z,ForgeDirection dir,int o){
		if(!checkRect(world,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SWITCH_FLIPPED.dimensions,x,y,z,dir)) return false;
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;

		for(int i = 0; i < 4; i++) if(!replaceable(world,new BlockPos(x + dX * (2 + i) + rX * 2, y, z + dZ * (2 + i) + rZ * 2))) return false;
		for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (4 + i) + rX * 3, y, z + dZ * (4 + i) + rZ * 3))) return false;
		if(!replaceable(world,new BlockPos(x + dX * 5 + rX * 4, y, z + dZ * 5 + rZ * 4))) return false;
		for(int j = 0; j < 2; j++) for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (6 + j) + rX * (3 + i), y, z + dZ * (6 + j) + rZ * (3 + i)))) return false;
		if(!replaceable(world,new BlockPos(x + dX * 7 + rX * 5, y, z + dZ * 7 + rZ * 5))) return false;
		for(int j = 0; j < 7; j++) for(int i = 0; i < 2; i++) if(!replaceable(world,new BlockPos(x + dX * (8 + j) + rX * (4 + i), y, z + dZ * (8 + j) + rZ * (4 + i)))) return false;
		
		return true;
	
}
static void fill_SWITCH_FLIPPED(List<Cell> cells,int x,int y,int z,ForgeDirection dir,int o){
		addRect(cells,x+dir.offsetX*o,y+dir.offsetY*o,z+dir.offsetZ*o,SourceRailKind.SWITCH_FLIPPED.dimensions,dir);
		
		
		
		ForgeDirection rot = dir.getRotation(ForgeDirection.DOWN);
		dir = dir.getOpposite();

		int dX = dir.offsetX;
		int dZ = dir.offsetZ;
		int rX = rot.offsetX;
		int rZ = rot.offsetZ;

		for(int i = 0; i < 4; i++) cells.add(new Cell(new BlockPos(x + dX * (2 + i) + rX * 1, y, z + dZ * (2 + i) + rZ * 1),rot.ordinal()));
		for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (4 + i) + rX * 2, y, z + dZ * (4 + i) + rZ * 2),rot.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 5 + rX * 3, y, z + dZ * 5 + rZ * 3),rot.ordinal()));
		for(int j = 0; j < 2; j++) for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (6 + j) + rX * (2 + i), y, z + dZ * (6 + j) + rZ * (2 + i)),dir.ordinal()));
		cells.add(new Cell(new BlockPos(x + dX * 7 + rX * 4, y, z + dZ * 7 + rZ * 4),rot.ordinal()));
		for(int j = 0; j < 7; j++) for(int i = 0; i < 2; i++) cells.add(new Cell(new BlockPos(x + dX * (8 + j) + rX * (3 + i), y, z + dZ * (8 + j) + rZ * (3 + i)),dir.ordinal()));
		
		
	
}

 static boolean replaceable(Level world,BlockPos p){return SourceRailBlock.full(world,p)&&world.isInWorldBounds(p)&&world.getBlockState(p).canBeReplaced();}
 static boolean checkRect(Level world,int x,int y,int z,int[] dimensions,int ox,int oy,int oz,ForgeDirection dir){int[] d=DummyableBlock.rotate(dimensions,dir.nativeDirection());for(int a=x-d[4];a<=x+d[5];a++)for(int b=y-d[1];b<=y+d[0];b++)for(int c=z-d[2];c<=z+d[3];c++){if(a==ox&&b==oy&&c==oz)continue;if(!replaceable(world,new BlockPos(a,b,c)))return false;}return true;}
 static void addRect(List<Cell> cells,int x,int y,int z,int[] dimensions,ForgeDirection dir){int[] d=DummyableBlock.rotate(dimensions,dir.nativeDirection());for(int a=x-d[4];a<=x+d[5];a++)for(int b=y-d[1];b<=y+d[0];b++)for(int c=z-d[2];c<=z+d[3];c++){int meta=b<y?0:b>y?1:a<x?4:a>x?5:c<z?2:c>z?3:-1;if(meta>=0)cells.add(new Cell(new BlockPos(a,b,c),meta));}}
 private SourceRailFootprints(){}
}
