package com.hbm.entity.grenade.waste;

import com.google.gson.*;
import com.hbm.world.structure.vanilla.SourceVanillaStates;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;

/** Exact old unsigned-byte inTile read, followed by the existing official state decoder. */
final class WastePearlLegacyNbt {
    private static final Map<Integer,String> IDS=read();
    private static Map<Integer,String> read(){
        try(var stream=WastePearlLegacyNbt.class.getResourceAsStream("/hbm/waste-pearl122-legacy-block-ids.json")){
            if(stream==null)throw new IllegalStateException("Missing actual vanilla legacy block IDs");
            var json=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
            var out=new HashMap<Integer,String>();json.entrySet().forEach(row->out.put(Integer.parseInt(row.getKey()),row.getValue().getAsString()));return Map.copyOf(out);
        }catch(IOException error){throw new IllegalStateException("Cannot read original throwable block IDs",error);}
    }
    static void convertBlockId(CompoundTag tag){
        if(!tag.contains("inTile",Tag.TAG_ANY_NUMERIC))return;
        // Vanilla1.7 masks its saved byte with255; unregistered IDs resolve to actual vanilla AIR.
        String old=IDS.getOrDefault(tag.getByte("inTile")&255,"minecraft:air");
        var state=SourceVanillaStates.resolve(old,0,0);
        tag.putString("inTile",BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
    }
    private WastePearlLegacyNbt(){}
}
