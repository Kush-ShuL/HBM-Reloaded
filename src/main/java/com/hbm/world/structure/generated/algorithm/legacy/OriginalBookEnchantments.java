package com.hbm.world.structure.generated.algorithm.legacy;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
/** Original 1.7.10 EnchantmentHelper afv.b for its actual book, source costs/weights/compatibility. */
public final class OriginalBookEnchantments {
 private record Row(int id,String name,int weight,int[][] costs,int[] compatible){boolean allows(int other){for(int id:compatible)if(id==other)return true;return false;}}
 private record Definitions(int bookEnchantability,List<Row> enchantments){}
 private record Choice(Row row,int level){}
 private static final Definitions SOURCE=load();
 private static Definitions load(){try(var input=OriginalBookEnchantments.class.getResourceAsStream("/hbm/generated-source-enchantments.json")){if(input==null)throw new IllegalStateException("Original24 enchantments are missing");return new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class);}catch(IOException error){throw new IllegalStateException(error);}}
 public static ItemStack bookAt30(HolderLookup.Provider lookup,Random random){int enchantability=SOURCE.bookEnchantability/2;int bonus=1+random.nextInt((enchantability>>1)+1)+random.nextInt((enchantability>>1)+1);int initial=bonus+30;float variance=(random.nextFloat()+random.nextFloat()-1F)*.15F;int power=Math.max(1,(int)(initial*(1F+variance)+.5F));var candidates=new HashMap<Integer,Choice>();for(var row:SOURCE.enchantments)for(var cost:row.costs)if(power>=cost[1]&&power<=cost[2])candidates.put(row.id,new Choice(row,cost[0]));var chosen=new ArrayList<Choice>();if(!candidates.isEmpty()){chosen.add(weighted(candidates.values(),random));for(int remaining=power;random.nextInt(50)<=remaining;remaining>>=1){var iterator=candidates.entrySet().iterator();while(iterator.hasNext()){var candidate=iterator.next();boolean compatible=true;for(var selected:chosen)if(!selected.row.allows(candidate.getKey())){compatible=false;break;}if(!compatible)iterator.remove();}if(!candidates.isEmpty())chosen.add(weighted(candidates.values(),random));}}
   var stack=new ItemStack(Items.ENCHANTED_BOOK);var stored=new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);var registry=lookup.lookupOrThrow(Registries.ENCHANTMENT);for(var choice:chosen){var key=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.parse(choice.row.name));stored.upgrade(registry.getOrThrow(key),choice.level);}stack.set(DataComponents.STORED_ENCHANTMENTS,stored.toImmutable());return stack;}
 private static Choice weighted(Collection<Choice> choices,Random random){int total=0;for(var choice:choices)total+=choice.row.weight;int value=random.nextInt(total);for(var choice:choices)if((value-=choice.row.weight)<0)return choice;throw new IllegalStateException("No original enchantment candidate");}
 private OriginalBookEnchantments(){}
}
