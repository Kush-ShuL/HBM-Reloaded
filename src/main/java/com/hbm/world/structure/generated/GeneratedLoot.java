package com.hbm.world.structure.generated;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.loot.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
/** Exact source call parameters forwarded to the shared real ItemPool implementation. */
public final class GeneratedLoot {
    public record Pool(String name,String fixedItem,int metadata,int minimum,int maximum){}
    public static Pool pool(String name){return new Pool(name,null,0,0,0);}public static Pool fixed(String item,int metadata,int min,int max){return new Pool(null,item,metadata,min,max);}
    public interface Extras {ItemStack book(String kind,Random random);void preset(String kind,WorldGenLevel level,BlockPos pos,Random random);void lore(WorldGenLevel level,BlockPos pos,ItemStack stack);void require(Set<String> effects);}
    private static Extras extras;
    public static void installExtras(Extras source){extras=Objects.requireNonNull(source);}
    /** Forward the real region/planning context; a ProtoChunk block entity may legitimately have no Level yet. */
    public static void fill(World world,Random random,Pool pool,Container inventory,int count){
        if(inventory==null)return;
        if(!(inventory instanceof BlockEntity))throw new IllegalArgumentException("Generated source loot requires its real native inventory block entity");
        if(!world.isPlanning()){fill(world.level(),random,pool,inventory,count);return;}
        if("DUNGEON_CHEST".equals(pool.name)){
            com.hbm.world.structure.generated.algorithm.legacy.LegacyVanillaDungeon.fill(world.lookup(),random,inventory,count);return;
        }
        var rng=new LegacyLootRandom(random);
        if(pool.fixedItem==null)LegacyStructureLoot.plan(world.lookup(),inventory,pool.name,count,rng);
        else LegacyStructureLoot.fillFixed(inventory,fixedStack(pool),pool.minimum,pool.maximum,count,rng);
    }
    /** ServerLevel is used only for canonical loot/registry context; the original region-owned inventory is retained. */
    public static void fill(WorldGenLevel region,Random random,Pool pool,Container inventory,int count){
        if(inventory==null)return;
        if(!(inventory instanceof BlockEntity tile))throw new IllegalArgumentException("Generated source loot requires its real native inventory block entity");
        if("DUNGEON_CHEST".equals(pool.name)){
            com.hbm.world.structure.generated.algorithm.legacy.LegacyVanillaDungeon.fill(region.registryAccess(),random,inventory,count);return;
        }
        ServerLevel level=region.getLevel();
        var origin=Vec3.atCenterOf(tile.getBlockPos());var rng=new LegacyLootRandom(random);
        if(pool.fixedItem==null)LegacyStructureLoot.fill(level,origin,inventory,pool.name,count,rng);
        else LegacyStructureLoot.fillFixed(level,origin,inventory,fixedStack(pool),pool.minimum,pool.maximum,count,rng);
    }
    /** Existing real ServerLevel/planning callers remain binary compatible; worldgen callers must forward their region. */
    public static void fill(Random random,Pool pool,Container inventory,int count){
        if(inventory==null)return;
        if(!(inventory instanceof BlockEntity tile))throw new IllegalArgumentException("Generated source loot requires its real native inventory block entity");
        var planning=World.planning();if(planning!=null){fill(planning,random,pool,inventory,count);return;}
        if(tile.getLevel() instanceof ServerLevel level){fill(level,random,pool,inventory,count);return;}
        throw new IllegalArgumentException("Generated source loot caller must forward its actual world generation region");
    }
    private static ItemStack fixedStack(Pool pool){var id=ResourceLocation.parse(pool.fixedItem);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Unported real fixed structure loot "+id);var stack=new ItemStack(BuiltInRegistries.ITEM.get(id));stack.set(HbmHazards.LEGACY_METADATA.get(),pool.metadata);return stack;}
    public static ItemStack book(String kind,Random random){if(extras==null)throw new IllegalStateException("Original HbmChestContents book backend is not installed");return extras.book(kind,random);}
    public static void preset(String kind,World world,int x,int y,int z){var pos=new BlockPos(x,y,z);if(!world.contains(pos))return;if(extras==null)throw new IllegalStateException("Original LootGenerator preset backend is not installed");extras.preset(kind,world.level(),pos,world.rand);}
    public static void lore(World world,int x,int y,int z,ItemStack stack){var pos=new BlockPos(x,y,z);if(!world.contains(pos))return;if(extras==null)throw new IllegalStateException("Original lore loot backend is not installed");extras.lore(world.level(),pos,stack);}
    public static void requireExtras(Set<String> effects){if(effects.isEmpty())return;if(extras==null)throw new IllegalStateException("Original generated structure book/preset dependencies are not installed: "+effects);extras.require(effects);}
    public interface Lockable {void setPins(int pins);void setMod(double mod);void lock();}
    public static Lockable lockable(BlockEntity tile){if(tile==null)return null;if(!(tile instanceof LockableBlockEntity lock))throw new IllegalStateException("Generated source lock requires real LockableBlockEntity, got "+tile);return new Lockable(){public void setPins(int pins){lock.setPins(pins);}public void setMod(double mod){lock.setMod(mod);}public void lock(){lock.lock();}};}
    private GeneratedLoot(){}
}
