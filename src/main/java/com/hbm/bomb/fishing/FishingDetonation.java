package com.hbm.bomb.fishing;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.bomb.nuclear.NuclearChunkloadingEntity;
import com.hbm.explosion.ExplosionNT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/** Modern waiting carrier only: original3F no-fire/no-block blast then15 fishing attempts, without a second grenade core. */
public final class FishingDetonation extends NuclearChunkloadingEntity {
 private final NuclearChunkAccess access=new NuclearChunkAccess();
 private boolean executing;
 public FishingDetonation(EntityType<? extends FishingDetonation> type,Level level){super(type,level);noPhysics=true;}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){}
 @Override public void tick(){
  if(!(level() instanceof ServerLevel server))return;
  try{
   loadChunk(Mth.floor(getX()/16D),Mth.floor(getZ()/16D));
   int ix=Mth.floor(getX()),iy=Mth.floor(getY()),iz=Mth.floor(getZ());boolean ready=true;
   // Every possible source sample is within±7. Same region also contains the3F legacy rays/6F damage LOS.
   for(int x=(ix-7)>>4;x<=(ix+7)>>4;x++)for(int z=(iz-7)>>4;z<=(iz+7)>>4;z++)if(access.getWithNeighbors(server,x,z)==null)ready=false;
   if(!ready)return;
   executing=true;
   try{
    // World.newExplosion(null,x,y+.25,z,3,false,false), through the existing exact legacy explosion implementation.
    var explosion=new ExplosionNT(server,null,getX(),getY()+.25D,getZ(),3F);explosion.isSmoking=false;explosion.explode();
    var table=server.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING_FISH);
    for(int i=0;i<15;i++){
     int x=ix+server.random.nextInt(15)-7,y=iy+server.random.nextInt(15)-7,z=iz+server.random.nextInt(15)-7;
     if(!LegacyMaterials.of(server.getBlockState(new BlockPos(x,y,z))).name().equals("water"))continue;
     // Source chance is still consumed. Forge1.7 luck0/speed100 clamps junk and treasure to0.
     server.random.nextFloat();
     var params=new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN,new Vec3(x+.5D,y+.5D,z+.5D))
       .withParameter(LootContextParams.TOOL,ItemStack.EMPTY).withLuck(0).create(LootContextParamSets.FISHING);
     // Native fish-only table keeps source60/25/2/13; explicitly pass the same world RNG, never a seeded sequence.
     for(var loot:table.getRandomItems(params,server.random))if(!loot.isEmpty()){
      var drop=new EntityItemBuoyant(server,x+.5D,y+.5D,z+.5D,loot.copy());
      drop.setDeltaMovement(drop.getDeltaMovement().x,1D,drop.getDeltaMovement().z);server.addFreshEntity(drop);
     }
    }
    discard();
   }finally{executing=false;access.close();}
  }catch(RuntimeException|Error failure){access.close();throw failure;}
 }
 @Override public void remove(RemovalReason reason){if(!executing)access.close();super.remove(reason);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){saveTicket(tag);}
 @Override protected void readAdditionalSaveData(CompoundTag tag){loadTicket(tag);access.close();}
}
