package com.hbm.world.structure.loot;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
/** BlockCrate's fixed lists are deliberately independent from configurable ItemPool tables. */
public final class LegacyCrateLoot {
    private static final JsonObject LISTS=load();
    private static JsonObject load(){try(var input=LegacyCrateLoot.class.getResourceAsStream("/hbm/structure-crate-loot.json")){if(input==null)throw new IllegalStateException("Run port_structure_loot.py");return JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();}catch(IOException error){throw new IllegalStateException(error);}}
    public static List<ItemStack> contents(ServerLevel level,String name,Random random){var row=LISTS.getAsJsonObject(name);if(row==null||!row.get("ready").getAsBoolean())throw new IllegalStateException("Real source crate list remains incomplete: "+name);var list=new ArrayList<ItemStack>();for(var value:row.getAsJsonArray("entries")){var entry=value.getAsJsonObject();var stack=ItemStack.CODEC.parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE),entry.get("stack")).getOrThrow();for(int i=0;i<entry.get("weight").getAsInt();i++)list.add(stack);}int count=random.nextInt(3)+3;if(name.equals("weaponList")){count=1+random.nextInt(2);if(random.nextInt(100)==34)count=25;}var result=new ArrayList<ItemStack>();for(int i=0;i<count;i++)result.add(list.get(random.nextInt(list.size())).copy());if(name.equals("redList")){result.clear();for(var stack:list)result.add(stack.copy());}return result;}
    private LegacyCrateLoot(){}
}
