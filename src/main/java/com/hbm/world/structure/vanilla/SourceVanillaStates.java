package com.hbm.world.structure.vanilla;

import com.google.gson.*;
import com.hbm.world.structure.templates.TemplateStateMappings;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Supplemental original vanilla names only; converted template states always keep priority in the caller. */
public final class SourceVanillaStates {
    private static final Map<String,JsonObject> DEFINITIONS=read();
    private static final Map<String,BlockState> STATES=new ConcurrentHashMap<>();
    private static Map<String,JsonObject> read(){
        try(var stream=SourceVanillaStates.class.getResourceAsStream("/hbm/generated-vanilla-states.json")){
            if(stream==null)throw new IllegalStateException("Missing official supplemental vanilla structure states");
            var rows=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("blocks");
            var result=new LinkedHashMap<String,JsonObject>();
            for(var entry:rows.entrySet()){
                var row=entry.getValue().getAsJsonObject();
                if(!entry.getKey().startsWith("minecraft:")||row.getAsJsonArray("states").size()!=16||!row.has("rotationPolicy"))throw new IllegalStateException("Incomplete actual vanilla palette: "+entry.getKey());
                result.put(entry.getKey(),row);
            }
            return Collections.unmodifiableMap(result);
        }catch(IOException error){throw new IllegalStateException("Cannot read official supplemental vanilla structure states",error);}
    }
    public static boolean hasSource(String source){return DEFINITIONS.containsKey(source);}
    public static BlockState resolve(String source,int metadata,int turns){
        var row=DEFINITIONS.get(source);
        if(row==null)throw new IllegalStateException("Original vanilla structure block was not exported: "+source);
        if(metadata<0||metadata>15)throw new IllegalStateException("Invalid original vanilla metadata: "+source+":"+metadata);
        int transformed=transform(row.get("rotationPolicy").getAsString(),metadata,turns&3);
        return STATES.computeIfAbsent(source+":"+transformed,ignored->parse(row.getAsJsonArray("states").get(transformed).getAsJsonObject(),source,transformed));
    }
    private static BlockState parse(JsonObject definition,String source,int metadata){
        if(definition.has("hbm_unmapped"))throw new IllegalStateException("Official palette has no actual source state "+source+":"+metadata+": "+definition.get("hbm_unmapped").getAsString());
        var encoded=new CompoundTag();encoded.putString("Name",definition.get("Name").getAsString());
        if(definition.has("Properties")){var properties=new CompoundTag();for(var property:definition.getAsJsonObject("Properties").entrySet())properties.putString(property.getKey(),property.getValue().getAsString());encoded.put("Properties",properties);}
        // Reuse the same actual-registry/property/value validator as native templates.
        return TemplateStateMappings.parse(encoded);
    }
    /** Literal supplied INBTBlockTransformable algorithms, selected by the real1.7 superclass. */
    private static int transform(String policy,int metadata,int turns){
        if(turns==0)return metadata;
        return switch(policy){
            case "none"->metadata;
            case "stair"->turns==2?metadata^1:metadata^(turns==1?((metadata&3)<2?2:3):((metadata&3)<2?3:2));
            case "trapdoor"->turns==2?metadata^1:metadata^(turns==1?((metadata&3)<2?3:2):((metadata&3)<2?2:3));
            case "pillar"->turns==2?metadata:(metadata&3)|switch(metadata&12){case 4->8;case 8->4;default->metadata&12;};
            case "directional"->(metadata&12)|(((metadata&3)+turns)&3);
            case "door"->metadata==8||metadata==9?metadata:(metadata&12)|(((metadata&3)+turns)&3);
            case "torch"->torch(metadata,turns);
            case "deco"->switch(turns){case 1->switch(metadata){case 2->5;case 3->4;case 4->2;case 5->3;default->metadata;};case 2->switch(metadata){case 2->3;case 3->2;case 4->5;case 5->4;default->metadata;};default->switch(metadata){case 2->4;case 3->5;case 4->3;case 5->2;default->metadata;};};
            case "lever"->metadata<=0||metadata>=7?((turns&1)!=0?metadata^7:metadata):metadata>=5?((turns&1)!=0?(metadata+1)%2+5:metadata):torch(metadata,turns);
            case "vine"->((metadata<<turns)|(metadata>>>(4-turns)))&15;
            default->throw new IllegalStateException("Unknown original vanilla rotation policy "+policy);
        };
    }
    private static int torch(int metadata,int turns){return switch(turns){case 1->switch(metadata){case 1->3;case 2->4;case 3->2;case 4->1;default->metadata;};case 2->switch(metadata){case 1->2;case 2->1;case 3->4;case 4->3;default->metadata;};default->switch(metadata){case 1->4;case 2->3;case 3->1;case 4->2;default->metadata;};};}
    private SourceVanillaStates(){}
}
