package com.hbm.world.structure.templates;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** One source-backed map shared by the offline native palettes, procedural resolver and native placement processor. */
public final class TemplateStateMappings {
    private static final Map<String,JsonObject> DEFINITIONS=read();
    private static final Map<String,BlockState> STATES=new ConcurrentHashMap<>();
    private static Map<String,JsonObject> read(){
        try(var stream=TemplateStateMappings.class.getResourceAsStream("/hbm/legacy-structure-states.json")){
            if(stream==null)throw new IllegalStateException("Missing complete source template state map");
            var object=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("states");var result=new LinkedHashMap<String,JsonObject>();object.entrySet().forEach(entry->result.put(entry.getKey(),entry.getValue().getAsJsonObject()));return Collections.unmodifiableMap(result);
        }catch(IOException error){throw new IllegalStateException(error);}
    }
    private static String key(String source,int metadata){String raw=source+":"+metadata;if(DEFINITIONS.containsKey(raw))return raw;return source.startsWith("hbm:")&&!source.startsWith("hbm:tile.")?"hbm:tile."+source.substring(4)+":"+metadata:raw;}
    public static boolean hasMapping(String source,int metadata){return DEFINITIONS.containsKey(key(source,metadata));}
    public static BlockState resolve(String source,int metadata,int turns){
        String key=key(source,metadata);var definition=DEFINITIONS.get(key);if(definition==null)throw new IllegalStateException("Source template state was not converted: "+key);
        if(definition.has("pending"))throw new IllegalStateException(definition.get("pending").getAsString());
        int rotation=turns&3;return STATES.computeIfAbsent(key+"/"+rotation,ignored->parse(definition.getAsJsonArray("rotations").get(rotation).getAsJsonObject()));
    }
    public static BlockState parse(CompoundTag tag){var json=new JsonObject();json.addProperty("Name",tag.getString("Name"));var properties=new JsonObject();var old=tag.getCompound("Properties");for(String key:old.getAllKeys())properties.addProperty(key,old.getString(key));json.add("Properties",properties);return parse(json);}
    private static BlockState parse(JsonObject definition){
        var id=ResourceLocation.parse(definition.get("Name").getAsString());if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalStateException("Actual structure block has not been registered: "+id);
        var block=BuiltInRegistries.BLOCK.get(id);BlockState state=block.defaultBlockState();
        if(definition.has("Properties"))for(var entry:definition.getAsJsonObject("Properties").entrySet()){
            Property<?> property=block.getStateDefinition().getProperty(entry.getKey());if(property==null)throw new IllegalStateException("Unknown actual property "+id+"["+entry.getKey()+"]");state=set(state,property,entry.getValue().getAsString());
        }
        return state;
    }
    private static <T extends Comparable<T>> BlockState set(BlockState state,Property<T> property,String value){return state.setValue(property,property.getValue(value).orElseThrow(()->new IllegalStateException("Invalid actual structure state "+state.getBlock()+"["+property.getName()+"="+value+"]")));}
    private TemplateStateMappings(){}
}
