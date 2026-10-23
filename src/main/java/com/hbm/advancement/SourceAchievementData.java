package com.hbm.advancement;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.resources.ResourceLocation;

/** Single source of truth exported from MainRegistry and AchievementHandler, including explicit pending nodes. */
public final class SourceAchievementData {
    public record Node(String symbol,String id,String legacyName,String parent,float x,float y,boolean available,String criterion){}
    public record Crafting(String achievement,String item,int metadata,boolean damage,List<String> pending){}
    private record Data(List<Node> nodes,List<Crafting> crafting){}
    private static final Data DATA=load();
    private static final Map<String,Node> NODES=new LinkedHashMap<>();
    private static final Map<String,List<Crafting>> CRAFTS=new HashMap<>();
    static{
        for(var node:DATA.nodes){NODES.put(node.id,node);NODES.put(node.symbol,node);NODES.put(node.legacyName,node);}
        for(var binding:DATA.crafting)if(binding.item!=null&&binding.pending.isEmpty())CRAFTS.computeIfAbsent(binding.item,key->new ArrayList<>()).add(binding);
    }
    private static Data load(){
        try(var stream=SourceAchievementData.class.getResourceAsStream("/hbm/source-achievements.json")){
            if(stream==null)throw new IllegalStateException("Missing genuine X5846 achievement definitions: run tools/port_advancements.py");
            return new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Data.class);
        }catch(java.io.IOException error){throw new IllegalStateException("Cannot read source achievements",error);}
    }
    public static Node node(String name){var direct=NODES.get(name);if(direct!=null)return direct;var id=ResourceLocation.tryParse(name);return id!=null&&id.getNamespace().equals("hbm")?NODES.get(id.getPath()):null;}
    public static Node node(ResourceLocation id){return id.getNamespace().equals("hbm")?NODES.get(id.getPath()):null;}
    public static List<Crafting> crafting(ResourceLocation item){return CRAFTS.getOrDefault(item.toString(),List.of());}
    public static ResourceLocation id(Node node){return ResourceLocation.fromNamespaceAndPath("hbm",node.id);}
    private SourceAchievementData(){}
}
