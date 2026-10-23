package com.hbm.world.structure.generated.algorithm.legacy;
import com.hbm.world.structure.generated.compat.World;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
/** Original Forge1.7.10 dungeon pool/8 draws and HBM MainRegistry additions, not a modern replacement loot table. */
public final class LegacyVanillaDungeon {
 @FunctionalInterface public interface SourceEnchantment {ItemStack enchantBookAt30(HolderLookup.Provider lookup,Random sourceRandom);}
 private static SourceEnchantment enchantment=OriginalBookEnchantments::bookAt30;
 public static void installSourceEnchantment(SourceEnchantment actual){enchantment=Objects.requireNonNull(actual);}
 public static void requireReady(){if(enchantment==null)throw new IllegalStateException("Original 30-level enchanted-book preparation has not been ported");for(var row:ROWS)if(!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(row.item)))throw new IllegalStateException("Missing original Forge dungeon loot "+row.item);}
 private record Row(String item,int minimum,int maximum,int weight){}
 private static final List<Row> ROWS=List.of(new Row("minecraft:saddle",1,1,10),new Row("minecraft:iron_ingot",1,4,10),new Row("minecraft:bread",1,1,10),new Row("minecraft:wheat",1,4,10),new Row("minecraft:gunpowder",1,4,10),new Row("minecraft:string",1,4,10),new Row("minecraft:bucket",1,1,10),new Row("minecraft:golden_apple",1,1,1),new Row("minecraft:redstone",1,4,10),new Row("minecraft:music_disc_13",1,1,10),new Row("minecraft:music_disc_cat",1,1,10),new Row("minecraft:name_tag",1,1,10),new Row("minecraft:golden_horse_armor",1,1,2),new Row("minecraft:iron_horse_armor",1,1,5),new Row("minecraft:diamond_horse_armor",1,1,1),new Row("minecraft:enchanted_book",1,1,1),new Row("hbm:heart_piece",1,1,1),new Row("hbm:key_red_cracked",1,1,5),new Row("hbm:scrumpy",1,1,1));
 public static int count(Random random){return 8;}
 public static String mob(Random random){int value=random.nextInt(400);return value<100?"Skeleton":value<300?"Zombie":"Spider";}
 public static void fill(HolderLookup.Provider lookup,Random random,Container inventory,int count){requireReady();var book=enchantment.enchantBookAt30(lookup,random);if(book.isEmpty())throw new IllegalStateException("Original level30 preparation returned no real enchanted book");int total=ROWS.stream().mapToInt(Row::weight).sum();for(int draw=0;draw<count;draw++){int value=random.nextInt(total);Row chosen=null;for(var row:ROWS)if((value-=row.weight)<0){chosen=row;break;}if(chosen==null)throw new IllegalStateException("Empty original dungeon loot");var stack=chosen.item.equals("minecraft:enchanted_book")?book.copy():new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(chosen.item)));int amount=chosen.minimum+random.nextInt(chosen.maximum-chosen.minimum+1);if(stack.getMaxStackSize()>=amount){stack.setCount(amount);inventory.setItem(random.nextInt(inventory.getContainerSize()),stack);}else for(int i=0;i<amount;i++)inventory.setItem(random.nextInt(inventory.getContainerSize()),stack.copyWithCount(1));}inventory.setChanged();}
 public static Spawner spawner(World world,int x,int y,int z){var tile=world.getTileEntity(x,y,z);if(tile==null)return null;if(!(tile instanceof SpawnerBlockEntity))throw new IllegalStateException("Original library expected actual vanilla spawner");return new Spawner(world,tile);}
 public static final class Spawner {private final World world;private final BlockEntity tile;private Spawner(World world,BlockEntity tile){this.world=world;this.tile=tile;}public Spawner func_145881_a(){return this;}public void setEntityName(String name){String id=switch(name){case "Skeleton"->"minecraft:skeleton";case "Zombie"->"minecraft:zombie";case "Spider"->"minecraft:spider";default->throw new IllegalStateException("Unported original dungeon mob "+name);};var data=tile.saveWithoutMetadata(world.lookup());var spawn=new CompoundTag();var entity=new CompoundTag();entity.putString("id",id);spawn.put("entity",entity);data.put("SpawnData",spawn);tile.loadWithComponents(data,world.lookup());}}
}
