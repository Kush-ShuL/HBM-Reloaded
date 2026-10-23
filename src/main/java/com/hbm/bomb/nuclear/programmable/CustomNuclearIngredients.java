package com.hbm.bomb.nuclear.programmable;

import com.google.gson.Gson;
import com.hbm.hazard.HbmHazards;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;

/** Original registerBombItems table. MULT deliberately multiplies value*stack count per slot, not value^count. */
public final class CustomNuclearIngredients {
 public enum Stage {TNT,NUKE,HYDRO,AMAT,DIRTY,SCHRAB,EUPH}
 public enum Operation {ADD,MULT}
 public record Entry(String symbol,String id,int metadata,Stage stage,float value,Operation operation) {
  public ItemStack stack(){ResourceLocation key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key))throw new IllegalStateException("Missing original custom-nuke ingredient "+key);ItemStack stack=new ItemStack(BuiltInRegistries.ITEM.get(key));if(metadata!=0)stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
 }
 private record Document(List<Entry> entries){}
 private static final List<Entry> ENTRIES=load();private static Map<Item,Entry> byItem;
 private static List<Entry> load(){try(var in=CustomNuclearIngredients.class.getResourceAsStream("/hbm/custom-nuclear-ingredients.json")){if(in==null)throw new IllegalStateException("Missing original custom nuclear ingredient table");return List.copyOf(new Gson().fromJson(new InputStreamReader(in,StandardCharsets.UTF_8),Document.class).entries());}catch(IOException ex){throw new IllegalStateException(ex);}}
 public static List<Entry> entries(){return ENTRIES;}
 private static Map<Item,Entry> bound(){if(byItem==null){Map<Item,Entry> map=new IdentityHashMap<>();for(Entry entry:ENTRIES)map.put(entry.stack().getItem(),entry);byItem=Collections.unmodifiableMap(map);}return byItem;}
 public static Entry entry(ItemStack stack){if(stack.isEmpty())return null;Entry entry=bound().get(stack.getItem());return entry!=null&&stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())==entry.metadata?entry:null;}
 public static CustomBombPayload calculate(Container inventory){float[] add=new float[7],mult={1,1,1,1,1,1,1};for(int slot=0;slot<inventory.getContainerSize();slot++){ItemStack stack=inventory.getItem(slot);Entry entry=entry(stack);if(entry==null)continue;int stage=entry.stage.ordinal();if(entry.operation==Operation.ADD)add[stage]+=entry.value*stack.getCount();else if(stage!=Stage.EUPH.ordinal())mult[stage]*=entry.value*stack.getCount();}for(int stage=0;stage<6;stage++)add[stage]*=mult[stage];if(add[0]<16)add[1]=0;if(add[1]<100)add[2]=0;if(add[1]<50){add[3]=0;add[5]=0;}if(add[5]==0)add[6]=0;return new CustomBombPayload(add[0],add[1],add[2],add[3],add[4],add[5],add[6]);}
 private CustomNuclearIngredients(){}
}
