package com.hbm.block.light.floodlight;
import com.google.gson.*;
import com.hbm.block.light.SourceBeamBlock;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
/** True source>=127 opacity, retaining old opaque half-slabs/stairs instead of comparing127 against modern0..15. */
public final class FloodlightOpacity{
 private static final Map<String,Integer> SOURCE=read();
 private static Map<String,Integer> read(){try(var input=FloodlightOpacity.class.getResourceAsStream("/hbm/source-light-opacity.json")){if(input==null)throw new IllegalStateException("Missing actual1.7.10 opacity table");var object=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("blocks");var values=new HashMap<String,Integer>();object.entrySet().forEach(e->values.put(e.getKey(),e.getValue().getAsInt()));return Map.copyOf(values);}catch(IOException failure){throw new UncheckedIOException(failure);}}
 public static boolean blocks(Level level,BlockPos pos){var state=level.getBlockState(pos);if(state.isAir()||state.getBlock()instanceof SourceBeamBlock)return false;if(state.getBlock()instanceof SlabBlock||state.getBlock()instanceof StairBlock)return true;var source=SOURCE.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());return source!=null?source>=127:state.getLightBlock(level,pos)>=8;}
 /** Actual source opacity for the rare <=2 growth check; retain old opaque slab/stair/leaves semantics instead of comparing modern0..15 directly. */
 public static int sourceOpacity(Level level,BlockPos pos){var state=level.getBlockState(pos);if(state.isAir()||state.getBlock()instanceof SourceBeamBlock)return 0;if(state.getBlock()instanceof SlabBlock||state.getBlock()instanceof StairBlock)return 255;if(state.getBlock()instanceof LeavesBlock)return 1;var source=SOURCE.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());return source!=null?source:state.getLightBlock(level,pos)*17;}
 private FloodlightOpacity(){}
}
