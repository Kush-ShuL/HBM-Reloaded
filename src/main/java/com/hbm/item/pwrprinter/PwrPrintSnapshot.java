package com.hbm.item.pwrprinter;

import com.hbm.machine.reactor.pwr.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

/** Source six-neighbor BlockPWR flood bounds and whitelist. Sparse entries omit only source null/air values. */
public record PwrPrintSnapshot(BlockPos min,BlockPos max,Direction direction,List<Cell> cells) {
    public record Cell(BlockPos position,Block original){}
    public PwrPrintSnapshot{min=min.immutable();max=max.immutable();cells=List.copyOf(cells);}
    public int sizeX(){return max.getX()-min.getX()+1;}public int sizeY(){return max.getY()-min.getY()+1;}public int sizeZ(){return max.getZ()-min.getZ()+1;}
    public static PwrPrintSnapshot capture(ServerLevel level,PwrControllerBlockEntity controller){
        var core=controller.getBlockPos();var direction=controller.getBlockState().getValue(PwrControllerBlock.FACING).getOpposite();
        var visited=new HashSet<BlockPos>();visited.add(core);var pending=new ArrayDeque<BlockPos>();pending.push(core.relative(direction));
        int minX=core.getX(),minY=core.getY(),minZ=core.getZ(),maxX=minX,maxY=minY,maxZ=minZ;
        while(!pending.isEmpty()){
            var pos=pending.pop();if(visited.contains(pos)||!level.getBlockState(pos).is(PwrContent.ASSEMBLY.get()))continue;visited.add(pos);
            minX=Math.min(minX,pos.getX());minY=Math.min(minY,pos.getY());minZ=Math.min(minZ,pos.getZ());maxX=Math.max(maxX,pos.getX());maxY=Math.max(maxY,pos.getY());maxZ=Math.max(maxZ,pos.getZ());
            // Source +X,-X,+Y,-Y,+Z,-Z order, with no Java recursion limit.
            pending.push(pos.north());pending.push(pos.south());pending.push(pos.below());pending.push(pos.above());pending.push(pos.west());pending.push(pos.east());
        }
        var min=new BlockPos(minX,minY,minZ);var max=new BlockPos(maxX,maxY,maxZ);var cells=new ArrayList<Cell>();
        // Every real BlockPWR/controller has a BE. This also includes disconnected whitelist cells inside source bounds.
        // Avoid allocating source's sizeX*sizeY*sizeZ array filled with nulls; all visible cells remain identical.
        for(int x=minX>>4;x<=maxX>>4;x++)for(int z=minZ>>4;z<=maxZ>>4;z++)for(var tile:level.getChunk(x,z).getBlockEntities().values()){
            var at=tile.getBlockPos();if(at.getX()<minX||at.getX()>maxX||at.getY()<minY||at.getY()>maxY||at.getZ()<minZ||at.getZ()>maxZ)continue;
            if(tile instanceof PwrAssemblyBlockEntity part)cells.add(new Cell(at,part.original==null?PwrContent.ASSEMBLY.get():part.original));
            else if(tile instanceof PwrControllerBlockEntity)cells.add(new Cell(at,PwrContent.CONTROLLER.get()));
        }
        cells.sort(Comparator.comparingInt((Cell cell)->cell.position().getX()).thenComparingInt(cell->cell.position().getY()).thenComparingInt(cell->cell.position().getZ()));
        return new PwrPrintSnapshot(min,max,direction,cells);
    }
}
