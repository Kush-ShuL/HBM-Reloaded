package com.hbm.world.structure.generated.compat;
import com.google.gson.*;
import com.hbm.world.structure.LegacyStructureStates;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.world.level.block.state.BlockState;
/** Real original vanilla Material.rock membership, resolved through actual native state counterparts. */
final class SourceRockMaterials {
 private static Set<net.minecraft.world.level.block.Block> rocks;
 static synchronized boolean isRock(BlockState state){if(rocks==null){var blocks=Collections.newSetFromMap(new IdentityHashMap<net.minecraft.world.level.block.Block,Boolean>());try(var input=SourceRockMaterials.class.getResourceAsStream("/hbm/generated-vanilla-materials.json")){if(input==null)throw new IllegalStateException("Original vanilla material export is absent");var rows=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("materials");for(var entry:rows.entrySet())if(entry.getValue().getAsString().equals("e"))for(int metadata=0;metadata<16;metadata++)try{blocks.add(LegacyStructureStates.resolve(entry.getKey(),metadata,0).getBlock());}catch(IllegalStateException unavailable){/* No counterpart is invented for unrelated unavailable palette entries. */}rocks=Set.copyOf(blocks);}catch(IOException error){throw new IllegalStateException(error);}}return rocks.contains(state.getBlock());}
}
