package com.hbm.world.structure;

import com.hbm.item.constructionwand.ConstructionWandContent;
import com.hbm.item.constructionwand.WandAirMarkerBlock;
import com.hbm.item.structurewand.StructureBlacklist;
import com.hbm.world.structure.templates.TemplateStateMappings;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.fml.loading.FMLPaths;

/** Exact save-area selection, encoded by the target's real StructureTemplate schema and complete native BE/component NBT. */
public final class StructureAuthoringFiles {
    public static Path directory(){return FMLPaths.GAMEDIR.get().resolve("structures");}
    private static Path file(String name){var root=directory().toAbsolutePath().normalize();var path=root.resolve(name+".nbt").normalize();if(!root.equals(path.getParent()))throw new IllegalArgumentException("Structure name must be a file name");return path;}
    public static List<String> names(){try{Files.createDirectories(directory());var files=directory().toFile().listFiles(file->file.isFile()&&file.getName().endsWith(".nbt"));if(files==null)return List.of();return Arrays.stream(files).map(File::getName).toList();}catch(IOException error){throw new UncheckedIOException(error);}}
    static ListTag ints(int... values){var list=new ListTag();for(int value:values)list.add(IntTag.valueOf(value));return list;}
    static CompoundTag blank(int x,int y,int z){var tag=new CompoundTag();tag.putInt("hbm_authoring_format",332);tag.put("size",ints(x,y,z));tag.put("palette",new ListTag());tag.put("blocks",new ListTag());tag.put("entities",new ListTag());return NbtUtils.addCurrentDataVersion(tag);}
    static void capture(ServerLevel level,BlockPos world,int x,int y,int z,Set<StructureBlacklist.Key> blacklist,CompoundTag template,Map<net.minecraft.world.level.block.state.BlockState,Integer> palette){
        var actual=level.getBlockState(world);if(actual.is(Blocks.AIR)||actual.is(Blocks.CAVE_AIR)||actual.is(Blocks.VOID_AIR))return; // Source adds air to its blacklist before checking BlockWand.exportAs.
        if(!blacklist.isEmpty()&&blacklist.contains(StructureBlacklist.capture(actual)))return;
        var saved=actual.getBlock() instanceof WandAirMarkerBlock marker?marker.exportAs.defaultBlockState():actual;
        var entries=template.getList("palette",Tag.TAG_COMPOUND);Integer id=palette.get(saved);if(id==null){id=entries.size();palette.put(saved,id);entries.add(NbtUtils.writeBlockState(saved));}
        var row=new CompoundTag();row.put("pos",ints(x,y,z));row.putInt("state",id);
        var tile=level.getBlockEntity(world);if(tile!=null){var data=tile.saveWithFullMetadata(level.registryAccess());data.remove("x");data.remove("y");data.remove("z");row.put("nbt",data);}
        template.getList("blocks",Tag.TAG_COMPOUND).add(row);
    }
    static Map<net.minecraft.world.level.block.state.BlockState,Integer> palette(CompoundTag tag){var result=new HashMap<net.minecraft.world.level.block.state.BlockState,Integer>();var rows=tag.getList("palette",Tag.TAG_COMPOUND);for(int i=0;i<rows.size();i++)result.put(TemplateStateMappings.parse(rows.getCompound(i)),i);return result;}
    static Path write(String name,CompoundTag tag)throws IOException{Files.createDirectories(directory());var path=file(name);try(var stream=Files.newOutputStream(path)){NbtIo.writeCompressed(tag,stream);}return path;}
    static CompoundTag read(String name)throws IOException{
        var path=file(name);try(var stream=Files.newInputStream(path)){var tag=NbtIo.readCompressed(stream,NbtAccounter.unlimitedHeap());validate(tag);return tag;}
    }
    static void validate(CompoundTag tag){
        if(tag.contains("palettes"))throw new IllegalArgumentException("Source authoring has one palette; randomized external native palettes are not this format");
        var size=tag.getList("size",Tag.TAG_INT);if(size.size()!=3||size.getInt(0)<=0||size.getInt(1)<=0||size.getInt(2)<=0)throw new IllegalArgumentException("Invalid structure dimensions");
        var palette=tag.getList("palette",Tag.TAG_COMPOUND);for(var raw:palette)TemplateStateMappings.parse((CompoundTag)raw);
        var occupied=new HashSet<BlockPos>();for(var raw:tag.getList("blocks",Tag.TAG_COMPOUND)){var row=(CompoundTag)raw;var pos=row.getList("pos",Tag.TAG_INT);int id=row.getInt("state");if(pos.size()!=3||pos.getInt(0)<0||pos.getInt(0)>=size.getInt(0)||pos.getInt(1)<0||pos.getInt(1)>=size.getInt(1)||pos.getInt(2)<0||pos.getInt(2)>=size.getInt(2)||id<0||id>=palette.size())throw new IllegalArgumentException("Invalid structure cell "+row);if(!occupied.add(new BlockPos(pos.getInt(0),pos.getInt(1),pos.getInt(2))))throw new IllegalArgumentException("Duplicate structure cell "+pos);}
        if(!tag.getList("entities",Tag.TAG_COMPOUND).isEmpty())throw new IllegalArgumentException("Source saveArea never captures entities; external entity templates need their actual separate import path");
    }
    static Map<Long,List<CompoundTag>> columns(CompoundTag tag){var result=new HashMap<Long,List<CompoundTag>>();for(var raw:tag.getList("blocks",Tag.TAG_COMPOUND)){var row=(CompoundTag)raw;var pos=row.getList("pos",Tag.TAG_INT);result.computeIfAbsent(net.minecraft.world.level.ChunkPos.asLong(pos.getInt(0),pos.getInt(2)),ignored->new ArrayList<>()).add(row);}return result;}
    static StructureTemplate column(ServerLevel level,CompoundTag saved,List<CompoundTag> cells,int x,int z,BlockPos origin,boolean debug){
        var size=saved.getList("size",Tag.TAG_INT);var tag=blank(size.getInt(0),size.getInt(1),size.getInt(2));var palette=saved.getList("palette",Tag.TAG_COMPOUND).copy();var rows=new ListTag();var byY=new HashMap<Integer,CompoundTag>();if(cells!=null)for(var row:cells)byY.put(row.getList("pos",Tag.TAG_INT).getInt(1),row);
        var air=new CompoundTag();air.putString("Name","minecraft:air");int airIndex=palette.size();palette.add(air);
        for(int y=0;y<size.getInt(1);y++){
            if(origin.getY()+y<level.getMinBuildHeight()||origin.getY()+y>=level.getMaxBuildHeight())continue;
            var selected=byY.get(y);if(selected!=null&&origin.getY()+y<level.getMinBuildHeight()+1)continue; // Source guard relative to the modern bottom; omitted wipe cells keep their source branch.
            var row=selected==null?new CompoundTag():selected.copy();if(selected==null){row.put("pos",ints(x,y,z));row.putInt("state",airIndex);}
            var state=TemplateStateMappings.parse(palette.getCompound(row.getInt("state")));
            if(selected!=null&&debug&&state.is(Blocks.AIR)){row.putInt("state",palette.size());palette.add(NbtUtils.writeBlockState(ConstructionWandContent.AIR_MARKER.get().defaultBlockState()));}
            var data=row.contains("nbt")?row.getCompound("nbt").copy():new CompoundTag();data.putInt("hbm_source_block_order",y);
            if(selected!=null&&state.hasBlockEntity()){
                // Reuse the sole source processor's existing real transform callbacks, with the exact captured modern state in all four slots (manual rotation0).
                var marker=new CompoundTag();var rotations=new ListTag();for(int turn=0;turn<4;turn++)rotations.add(NbtUtils.writeBlockState(state));marker.put("rotations",rotations);data.put("hbm_legacy_state",marker);data.putBoolean("hbm_legacy_block_entity",true);
            }
            row.put("nbt",data);rows.add(row);
        }
        tag.put("palette",palette);tag.put("blocks",rows);var template=new StructureTemplate();template.load(level.registryAccess().lookupOrThrow(Registries.BLOCK),tag);return template;
    }
    private StructureAuthoringFiles(){}
}
