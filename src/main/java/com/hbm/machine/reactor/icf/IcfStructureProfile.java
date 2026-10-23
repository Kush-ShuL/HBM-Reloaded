package com.hbm.machine.reactor.icf;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** Exact TileEntityICFStruct construction cells, also used by its original small-block preview. */
public final class IcfStructureProfile {
    public record Cell(int width,int height,int length,int material){}
    public static final List<Cell> CELLS=create();
    private static List<Cell> create() {
        var cells=new ArrayList<Cell>();
        for(int i=-8;i<=8;i++) {
            cells.add(new Cell(1,0,i,0));
            if(i!=0)cells.add(new Cell(0,0,i,0));
            cells.add(new Cell(-1,0,i,0));
            cells.add(new Cell(0,3,i,2));
            int material=Math.abs(i)<=2?2:4;
            for(int j=-1;j<=1;j++)cells.add(new Cell(j,1,i,material));
            for(int j=-2;j<=2;j++)cells.add(new Cell(j,2,i,material));
            for(int j=-2;j<=2;j++)if(j!=0)cells.add(new Cell(j,3,i,material));
            for(int j=-2;j<=2;j++)cells.add(new Cell(j,4,i,material));
            for(int j=-1;j<=1;j++)cells.add(new Cell(j,5,i,material));
        }
        return List.copyOf(cells);
    }
    public static BlockPos position(BlockPos core,Direction facing,Cell cell) {
        return core.relative(facing,cell.width()).relative(facing.getClockWise(),cell.length()).above(cell.height());
    }
    private IcfStructureProfile(){}
}
