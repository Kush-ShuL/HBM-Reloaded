package com.hbm.world.structure;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Finite raw metadata produced by actual source call expressions, not every possible modern property value. */
public final class ProceduralStateUses {
    private static final Map<String,int[]> USES=load();
    private static Map<String,int[]> load(){
        try(var input=ProceduralStateUses.class.getResourceAsStream("/hbm/procedural-state-uses51.json")){
            if(input==null)throw new IllegalStateException("Missing source procedural metadata preflight data");
            var root=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();
            if(!root.getAsJsonArray("unresolvedExpressions").isEmpty())throw new IllegalStateException("Unresolved actual procedural metadata call expressions");
            var values=new TreeMap<String,TreeSet<Integer>>();
            for(var raw:root.getAsJsonArray("states")){var row=raw.getAsJsonObject();values.computeIfAbsent(row.get("source").getAsString(),ignored->new TreeSet<>()).add(row.get("metadata").getAsInt());}
            var result=new HashMap<String,int[]>();values.forEach((key,value)->result.put(key,value.stream().mapToInt(Integer::intValue).toArray()));return Map.copyOf(result);
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
    }
    public static int[] metadata(String source){var result=USES.get(source.replace("hbm:tile.","hbm:"));return result==null?new int[]{0}:result.clone();}
    private ProceduralStateUses(){}
}
