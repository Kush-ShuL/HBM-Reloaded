package com.hbm.client.wiaj;

import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.hbm.world.structure.LegacyStructureStates;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Source identifiers retain true metadata through the already authoritative modern decoders. */
public final class JarSymbols {
    private static final Map<String,String> ALIASES=aliases();
    private static Map<String,String> aliases(){try(var input=JarSymbols.class.getResourceAsStream("/hbm/wiaj-item-aliases.json")){if(input==null)throw new IllegalStateException("Run port_wiaj.py");var result=new java.util.LinkedHashMap<String,String>();com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->result.put(entry.getKey(),entry.getValue().getAsString()));return Map.copyOf(result);}catch(java.io.IOException error){throw new IllegalStateException(error);}}

    public record SourceBlock(String id){public BlockState state(int metadata){var identifier=net.minecraft.resources.ResourceLocation.parse(ALIASES.getOrDefault(id,id));if(net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(identifier)){var block=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(identifier);if(block instanceof com.hbm.machine.foundry.FoundryBlock foundry){var state=foundry.defaultBlockState();if(foundry.kind.outlet()&&metadata>=2&&metadata<=5)state=state.setValue(com.hbm.machine.foundry.FoundryBlock.FACING,net.minecraft.core.Direction.from3DDataValue(metadata));return state;}}return LegacyStructureStates.resolve(ALIASES.getOrDefault(id,id),metadata,0);}}
    public static SourceBlock block(String id){return new SourceBlock(id.indexOf(':')<0?"hbm:"+id:id);}
    public static ItemStack item(String id,int count,int metadata){return GenericRecipeConfigCodec.legacyStack(id.indexOf(':')<0?"hbm:"+id:id,metadata,ALIASES).copyWithCount(Math.max(1,count));}
    public static ItemStack item(String id){return item(id,1,0);}
    public static String text(String key){return net.minecraft.network.chat.Component.translatable(key).getString();}
    private JarSymbols(){}
}
