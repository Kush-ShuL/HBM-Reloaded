package com.hbm.machine.soyuzlauncher;

import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;

/** One faithful finite source layout, shared by the real construction checker and its original miniature. */
public final class SoyuzLauncherLayout {
    public enum Material { LAUNCHER, CONCRETE, SCAFFOLD }
    public record Cell(BlockPos offset, Material material) {}
    public static final List<Cell> MATERIALS;
    public static final int[][] DIMENSIONS = {
        {0,1,6,6,6,6}, {-2,4,-3,6,-3,6}, {-2,4,6,-3,-3,6}, {-2,4,6,-3,6,-3}, {-2,4,-3,6,6,-3}, {0,4,1,1,-6,8}, {0,4,2,2,9,-5}
    };
    static {
        var cells = new ArrayList<Cell>();
        add(cells, Material.LAUNCHER, -6,6,3,4,-6,6); add(cells, Material.LAUNCHER, -1,1,3,4,-8,-7); add(cells, Material.LAUNCHER, -2,2,3,4,7,9);
        add(cells, Material.LAUNCHER, -2,2,51,51,5,9); add(cells, Material.LAUNCHER, -1,1,38,38,-8,-6);
        add(cells, Material.CONCRETE, 3,6,0,2,3,6); add(cells, Material.CONCRETE, -6,-3,0,2,3,6);
        add(cells, Material.CONCRETE, -6,-3,0,2,-6,-3); add(cells, Material.CONCRETE, 3,6,0,2,-6,-3);
        add(cells, Material.CONCRETE, -1,1,0,2,-8,-6); add(cells, Material.CONCRETE, -2,2,0,2,5,9);
        add(cells, Material.SCAFFOLD, -1,1,5,50,6,8); add(cells, Material.SCAFFOLD, 0,0,5,37,-7,-7);
        MATERIALS = List.copyOf(cells);
    }
    private static void add(List<Cell> cells, Material material, int x0,int x1,int y0,int y1,int z0,int z1) { for(int x=x0;x<=x1;x++)for(int y=y0;y<=y1;y++)for(int z=z0;z<=z1;z++)cells.add(new Cell(new BlockPos(x,y,z),material)); }
    public static List<BlockPos> parts(BlockPos core) {
        var result = new LinkedHashSet<BlockPos>();
        for (var dims : DIMENSIONS) { var d = DummyableBlock.rotate(dims, Direction.EAST); for (int x=-d[4];x<=d[5];x++)for(int y=-d[1];y<=d[0];y++)for(int z=-d[2];z<=d[3];z++)result.add(core.offset(x,y,z)); }
        return List.copyOf(result);
    }
    private SoyuzLauncherLayout() {}
}
