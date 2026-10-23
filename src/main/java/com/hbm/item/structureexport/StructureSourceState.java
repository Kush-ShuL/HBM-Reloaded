package com.hbm.item.structureexport;

import com.google.gson.JsonParser;
import com.hbm.world.structure.LegacyStructureStates;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;

/** Inverts the already admitted source decoders; never guesses a modern state to legacy_meta=0. */
public final class StructureSourceState {
    public record Sample(String name,int metadata){}
    private record Source(String id,String name){}
    private record Candidate(String name,int metadata,BlockState state){}
    private static final Map<String,List<Source>> SOURCES=read();
    private static final Map<Block,List<Candidate>> CACHE=new HashMap<>();
    private static Map<String,List<Source>> read(){
        try(var input=StructureSourceState.class.getResourceAsStream("/hbm/structure-export317-sources.json")){
            if(input==null)throw new IllegalStateException("Missing original authoring block identity table");
            var rows=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("blocks");
            var result=new LinkedHashMap<String,List<Source>>();
            for(var row:rows.entrySet()){var list=new ArrayList<Source>();for(var value:row.getValue().getAsJsonArray()){var object=value.getAsJsonObject();list.add(new Source(object.get("id").getAsString(),object.has("name")?object.get("name").getAsString():null));}result.put(row.getKey(),List.copyOf(list));}
            return Map.copyOf(result);
        }catch(IOException error){throw new IllegalStateException("Cannot read original authoring block identities",error);}
    }
    private static List<Candidate> candidates(Block block){
        var result=new ArrayList<Candidate>();
        for(var source:SOURCES.getOrDefault(BuiltInRegistries.BLOCK.getKey(block).toString(),List.of()))for(int meta=0;meta<16;meta++){
            BlockState state;
            try{state=LegacyStructureStates.resolve(source.id(),meta,0);}catch(IllegalArgumentException|IllegalStateException undefined){continue;} // Only decoder-declared valid raw states are eligible.
            if(state.getBlock()==block)result.add(new Candidate(source.name()==null?block.getDescriptionId():source.name(),meta,state));
        }
        return List.copyOf(result);
    }
    public static Sample capture(BlockState actual){
        var variants=CACHE.computeIfAbsent(actual.getBlock(),StructureSourceState::candidates);
        for(var candidate:variants)if(matches(candidate.state(),actual))return new Sample(candidate.name(),candidate.metadata());
        throw new IllegalArgumentException(actual.toString());
    }
    private static boolean matches(BlockState decoded,BlockState actual){
        for(var property:decoded.getProperties()){
            String name=property.getName();
            // Context-derived properties did not exist in the 1.7 four-bit block metadata.
            if(name.equals("waterlogged")||name.equals("distance"))continue;
            if(decoded.getBlock() instanceof StairBlock&&name.equals("shape"))continue;
            if(decoded.getBlock() instanceof ChestBlock&&name.equals("type"))continue;
            if(decoded.getBlock() instanceof net.minecraft.world.level.block.CrossCollisionBlock&&Set.of("north","east","south","west").contains(name))continue;
            if(decoded.getBlock() instanceof DoorBlock&&decoded.getValue(DoorBlock.HALF)==DoubleBlockHalf.UPPER&&Set.of("facing","open","powered").contains(name))continue;
            if(decoded.getBlock() instanceof DoorBlock&&decoded.getValue(DoorBlock.HALF)==DoubleBlockHalf.LOWER&&Set.of("hinge","powered").contains(name))continue;
            if(!actual.hasProperty(property)||!value(decoded,property).equals(value(actual,property)))return false;
        }
        return true;
    }
    private static <T extends Comparable<T>> T value(BlockState state,Property<T> property){return state.getValue(property);}
    private StructureSourceState(){}
}
