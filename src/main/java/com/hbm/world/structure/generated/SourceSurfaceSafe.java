package com.hbm.world.structure.generated;

import com.hbm.machine.storage.StorageCrateBlockEntity;
import com.hbm.machine.storage.StorageCrateContent;
import com.hbm.machine.storage.StorageCrateKind;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Entire X5846 HbmWorldGen surface-safe branch, with real locked inventory and original weighted pools. */
public final class SourceSurfaceSafe {
 public static final List<String> POOLS=List.of("POOL_VAULT_RUSTY","POOL_VAULT_STANDARD","POOL_VAULT_REINFORCED","POOL_VAULT_UNBREAKABLE");
 public static void generate(World world,Random random,int i,int j,int frequency,boolean enabled,Set<String> selected){
  if(frequency<=0||!enabled||random.nextInt(frequency)!=0)return;
  int x=i+random.nextInt(16),z=j+random.nextInt(16),y=world.getHeightValue(x,z);
  if(!world.getBlock(x,y-1,z).canPlaceTorchOnTop(world,x,y-1,z))return;
  // A known incomplete pool gates this whole genuine branch; no missing weighted entry is deleted.
  var dependencies=GeneratedDependencies.forNames(List.of("old:SurfaceSafe"));dependencies.requireRegistryAndHooks();for(var pool:POOLS)if(!LegacyStructureLoot.ready(world.lookup(),pool))throw new IllegalStateException("Incomplete original surface-safe loot pool "+pool);
  int meta=random.nextInt(4)+2;var state=StorageCrateContent.BLOCK.get(StorageCrateKind.SAFE).get().fromLegacy(meta);
  world.setBlock(x,y,z,Block.observed(state),meta,2);selected.add("old:SurfaceSafe");
  if(world.getTileEntity(x,y,z) instanceof StorageCrateBlockEntity safe&&safe.kind==StorageCrateKind.SAFE){
   // Source dispatch consumes its tier draw first, then only the selected tier's draw-count RNG.
   switch(random.nextInt(10)){
    case 0:case 1:case 2:case 3:safe.setMod(1);GeneratedLoot.fill(world,random,GeneratedLoot.pool(POOLS.get(0)),safe,random.nextInt(4)+3);break;
    case 4:case 5:case 6:safe.setMod(.1);GeneratedLoot.fill(world,random,GeneratedLoot.pool(POOLS.get(1)),safe,random.nextInt(3)+2);break;
    case 7:case 8:safe.setMod(.02);GeneratedLoot.fill(world,random,GeneratedLoot.pool(POOLS.get(2)),safe,random.nextInt(3)+1);break;
    case 9:safe.setMod(0);GeneratedLoot.fill(world,random,GeneratedLoot.pool(POOLS.get(3)),safe,random.nextInt(2)+1);break;
   }
   safe.setPins(random.nextInt(999)+1);safe.lock();if(random.nextInt(10)<3)safe.fillWithSpiders();
  }
 }
 private SourceSurfaceSafe(){}
}
