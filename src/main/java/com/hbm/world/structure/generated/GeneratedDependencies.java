package com.hbm.world.structure.generated;
import com.google.gson.Gson;
import com.hbm.world.structure.generated.compat.*;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
/** Dependencies of the selected actual algorithm branch; no unrelated unused source selector blocks it. */
public record GeneratedDependencies(Set<String> blocks,Set<String> items,Set<String> pools,Set<String> attachments,Set<String> extras) {
    private static final Map<String,GeneratedDependencies> SOURCE=read();
    private static Map<String,GeneratedDependencies> read(){try(var input=GeneratedDependencies.class.getResourceAsStream("/hbm/generated-structure-dependencies.json")){if(input==null)throw new IllegalStateException("Run port_generated_structures.py");var type=new com.google.gson.reflect.TypeToken<Map<String,GeneratedDependencies>>(){}.getType();return new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),type);}catch(IOException error){throw new IllegalStateException(error);}}
    public static GeneratedDependencies forComponents(List<StructureComponent> components){return forNames(components.stream().map(value->value.getClass().getSimpleName()).toList());}
    public static GeneratedDependencies forNames(Collection<String> names){var blocks=new TreeSet<String>();var items=new TreeSet<String>();var pools=new TreeSet<String>();var attachments=new TreeSet<String>();var extras=new TreeSet<String>();for(var name:names){var source=SOURCE.get(name);if(source==null)throw new IllegalStateException("Missing source dependency list for "+name);blocks.addAll(source.blocks);items.addAll(source.items);pools.addAll(source.pools);attachments.addAll(source.attachments);extras.addAll(source.extras);}return new GeneratedDependencies(Set.copyOf(blocks),Set.copyOf(items),Set.copyOf(pools),Set.copyOf(attachments),Set.copyOf(extras));}
    public void requireRegistryAndHooks(){for(var id:blocks)com.hbm.world.structure.LegacyStructureStates.requireProceduralStates(id);for(var id:items)if(!net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(net.minecraft.resources.ResourceLocation.parse(id)))throw new IllegalStateException("Original generated structure fixed item dependency is not ready: "+id);GeneratedAttachments.require(attachments);GeneratedLoot.requireExtras(extras);}
    public void requirePools(ServerLevel level){for(var pool:pools)LegacyStructureLoot.requireReady(level,pool);}
    public CompoundTag save(){var tag=new CompoundTag();put(tag,"blocks",blocks);put(tag,"items",items);put(tag,"pools",pools);put(tag,"attachments",attachments);put(tag,"extras",extras);return tag;}
    private static void put(CompoundTag tag,String key,Set<String> values){var list=new ListTag();for(var value:new TreeSet<>(values))list.add(StringTag.valueOf(value));tag.put(key,list);}
    private static Set<String> get(CompoundTag tag,String key){var result=new HashSet<String>();for(var value:tag.getList(key,Tag.TAG_STRING))result.add(value.getAsString());return Set.copyOf(result);}
    public static GeneratedDependencies load(CompoundTag tag){return new GeneratedDependencies(get(tag,"blocks"),get(tag,"items"),get(tag,"pools"),get(tag,"attachments"),get(tag,"extras"));}
}
