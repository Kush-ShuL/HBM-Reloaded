package com.hbm.machine.precisionassembler;
import com.google.gson.JsonParser;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
/** Actual original ItemMachineUpgrade identities, not a guessed upgrade_ name prefix or only two subclasses. */
public final class PrecisionUpgrades {
 private static final Set<String>IDS=load();private static Set<String>load(){try(var stream=PrecisionUpgrades.class.getResourceAsStream("/hbm/precision-assembler-upgrades.json")){if(stream==null)throw new IllegalStateException("Missing original upgrade identity list");var result=new LinkedHashSet<String>();JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray().forEach(value->result.add(value.getAsString()));return Set.copyOf(result);}catch(java.io.IOException error){throw new IllegalStateException(error);}}
 public static boolean isUpgrade(ItemStack stack){return !stack.isEmpty()&&IDS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());}
 private PrecisionUpgrades(){}
}
