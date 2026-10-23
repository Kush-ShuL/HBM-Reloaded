package com.hbm.world.structure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.state.BlockState;

/** Reads the untouched source custom NBT format; omitted voxels remain omitted. */
final class LegacyStructureTemplate {
    record Cell(int x,int y,int z,int palette){}
    record Palette(String name,int metadata){}
    private static final Map<String,LegacyStructureTemplate> CACHE=new ConcurrentHashMap<>();
    final int sizeX,sizeY,sizeZ;
    final List<Cell> cells;
    private final List<Palette> palette;
    private final Map<Integer,List<BlockState>> states=new ConcurrentHashMap<>();
    static LegacyStructureTemplate get(String name){return CACHE.computeIfAbsent(name,LegacyStructureTemplate::new);}
    private LegacyStructureTemplate(String name){
        try(var input=LegacyStructureTemplate.class.getResourceAsStream("/assets/hbm/structures/"+name+".nbt")){
            if(input==null)throw new IllegalStateException("Missing original structure template "+name);
            CompoundTag root=NbtIo.readCompressed(input,NbtAccounter.unlimitedHeap());var size=root.getList("size",Tag.TAG_INT);sizeX=size.getInt(0);sizeY=size.getInt(1);sizeZ=size.getInt(2);
            var definitions=new ArrayList<Palette>();var entries=root.getList("palette",Tag.TAG_COMPOUND);
            for(int i=0;i<entries.size();i++){var row=entries.getCompound(i);definitions.add(new Palette(row.getString("Name"),Integer.parseInt(row.getCompound("Properties").getString("meta"))));}palette=List.copyOf(definitions);
            if(!root.getList("entities",Tag.TAG_COMPOUND).isEmpty())throw new IllegalStateException("Real entity conversion required for "+name);
            var blocks=root.getList("blocks",Tag.TAG_COMPOUND);var voxels=new ArrayList<Cell>();
            for(int i=0;i<blocks.size();i++){var row=blocks.getCompound(i);if(row.contains("nbt"))throw new IllegalStateException("Real block entity conversion required for "+name);var pos=row.getList("pos",Tag.TAG_INT);voxels.add(new Cell(pos.getInt(0),pos.getInt(1),pos.getInt(2),row.getInt("state")));}
            voxels.sort(Comparator.comparingInt(Cell::x).thenComparingInt(Cell::z).thenComparingInt(Cell::y));cells=List.copyOf(voxels);
        }catch(java.io.IOException error){throw new IllegalStateException("Cannot read source structure "+name,error);}
    }
    List<BlockState> states(int turns){return states.computeIfAbsent(turns,rotation->palette.stream().map(row->LegacyStructureStates.resolve(row.name(),row.metadata(),rotation)).toList());}
    int x(Cell cell,int turns){return switch(turns&3){case 1->sizeZ-1-cell.z();case 2->sizeX-1-cell.x();case 3->cell.z();default->cell.x();};}
    int z(Cell cell,int turns){return switch(turns&3){case 1->cell.x();case 2->sizeZ-1-cell.z();case 3->sizeX-1-cell.x();default->cell.z();};}
}
