package com.hbm.machine.reactor.watz;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** The exact source circular37-cell layer, not a replacement7×7 cube. */
public final class WatzLayout {
    public record Part(int x,int y,int z,int kind) {
        public BlockPos at(BlockPos core){return core.offset(x,y,z);}
        public BlockState state(){return switch(kind){case 0->WatzContent.ELEMENT.get().defaultBlockState();case 1->WatzContent.COOLER.get().defaultBlockState();default->WatzContent.CASING.get().defaultBlockState().setValue(WatzCasing.META,1);};}
    }
    public static final List<Part> COMPONENTS;
    public static final List<BlockPos> CELLS;
    static {
        var parts=new ArrayList<Part>();parts.add(new Part(0,1,0,1));parts.add(new Part(0,2,0,1));
        for(int y=0;y<3;y++){
            for(int[] p:new int[][]{{1,0},{2,0},{0,1},{0,2},{-1,0},{-2,0},{0,-1},{0,-2},{1,1},{1,-1},{-1,1},{-1,-1}})parts.add(new Part(p[0],y,p[1],0));
            for(int[] p:new int[][]{{2,1},{2,-1},{1,2},{-1,2},{-2,1},{-2,-1},{1,-2},{-1,-2}})parts.add(new Part(p[0],y,p[1],1));
            for(int i=-1;i<=1;i++){parts.add(new Part(3,y,i,2));parts.add(new Part(i,y,3,2));parts.add(new Part(-3,y,i,2));parts.add(new Part(i,y,-3,2));}
            for(int[] p:new int[][]{{2,2},{2,-2},{-2,2},{-2,-2}})parts.add(new Part(p[0],y,p[1],2));
        }
        COMPONENTS=List.copyOf(parts);var cells=new ArrayList<BlockPos>();cells.add(BlockPos.ZERO);for(var p:parts)cells.add(new BlockPos(p.x,p.y,p.z));CELLS=List.copyOf(cells);
    }
    private WatzLayout(){}
}
