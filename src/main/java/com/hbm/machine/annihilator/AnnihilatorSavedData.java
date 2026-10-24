package com.hbm.machine.annihilator;
import com.hbm.fluid.*;
import com.hbm.registry.HbmItemTags;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import static com.hbm.machine.annihilator.AnnihilatorRecipes.Key;
/** Actual source per-dimension, named shared pools with unbounded integer counters; keys ignore item NBT. */
public final class AnnihilatorSavedData extends SavedData {
 public final Map<String,Map<Key,BigInteger>> pools=new HashMap<>();
 public static AnnihilatorSavedData get(ServerLevel level){return level.getDataStorage().computeIfAbsent(new Factory<>(AnnihilatorSavedData::new,AnnihilatorSavedData::load,null),"annihilator");}
 public ItemStack push(String pool,HbmFluidType fluid,long amount,boolean always){var result=increment(pool,Key.fluid(fluid),amount,always);setDirty();return result;}
 public ItemStack push(String pool,ItemStack stack,boolean always){var item=increment(pool,Key.item(stack),stack.getCount(),always);var comparable=increment(pool,Key.comparable(stack),stack.getCount(),always);var tagged=ItemStack.EMPTY;for(var name:HbmItemTags.tags(stack))if(name!=null&&!name.isEmpty()){var payout=increment(pool,Key.tag(name),stack.getCount(),always);if(!payout.isEmpty())tagged=payout;}setDirty();return !tagged.isEmpty()?tagged:!comparable.isEmpty()?comparable:item;}
 private ItemStack increment(String name,Key key,long amount,boolean always){var pool=pools.computeIfAbsent(name,ignored->new HashMap<>());var previous=pool.get(key);var current=previous==null?BigInteger.valueOf(amount):previous.add(BigInteger.valueOf(amount));var result=AnnihilatorRecipes.payout(key,previous==null?BigInteger.ZERO:always?null:previous,current);pool.put(key,current);return result;}
 public BigInteger monitor(String name,Key key){return pools.getOrDefault(name,Map.of()).getOrDefault(key,BigInteger.ZERO);}
 @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){var rows=new ListTag();for(var entry:pools.entrySet()){var p=new CompoundTag();p.putString("poolname",entry.getKey());var counters=new ListTag();for(var counter:entry.getValue().entrySet()){var row=new CompoundTag();var key=counter.getKey();row.putByte("key",(byte)key.kind());if(key.kind()==0||key.kind()==1){row.putString("item",key.value());if(key.kind()==1)row.putShort("meta",(short)key.meta());}else row.putString(key.kind()==2?"fluid":"tag",key.value());row.putByteArray("amount",counter.getValue().toByteArray());counters.add(row);}p.put("pool",counters);rows.add(p);}tag.put("pools",rows);return tag;}
 private static AnnihilatorSavedData load(CompoundTag tag,HolderLookup.Provider lookup){var result=new AnnihilatorSavedData();var rows=tag.getList("pools",10);for(var value:rows){var p=(CompoundTag)value;var pool=new HashMap<Key,BigInteger>();for(var item:p.getList("pool",10)){var row=(CompoundTag)item;try{int kind=row.getByte("key");var key=new Key(kind,row.getString(kind==2?"fluid":kind==3?"tag":"item"),kind==1?row.getShort("meta"):0);pool.put(key,new BigInteger(row.getByteArray("amount")));}catch(RuntimeException malformed){/* Source tolerates damaged world pool data. */}}result.pools.put(p.getString("poolname"),pool);}return result;}
}
