package com.hbm.bomb.nuclear;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.TicketController;

/** Keep the actual explosion entity chunk ticking, including persistence without nearby players. */
public abstract class NuclearChunkloadingEntity extends Entity {
 public static final TicketController TICKETS=new TicketController(ResourceLocation.fromNamespaceAndPath("hbm","nuclear_explosion"));
 private ChunkPos origin,loaded;
 private boolean ticketRegistered;
 protected NuclearChunkloadingEntity(EntityType<?> type,Level world){super(type,world);}
 @Override public void onAddedToLevel(){super.onAddedToLevel();loadChunk(net.minecraft.util.Mth.floor(getX()/16D),net.minecraft.util.Mth.floor(getZ()/16D));}
 public void loadChunk(int x,int z){
  if(!(level() instanceof ServerLevel server)||!NuclearConfig.CHUNKLOADING.get()||isRemoved())return;
  var target=new ChunkPos(x,z);
  if(origin!=null&&!origin.equals(target)){TICKETS.forceChunk(server,this,origin.x,origin.z,false,true);origin=null;}
  if(loaded!=null&&!loaded.equals(target)){TICKETS.forceChunk(server,this,loaded.x,loaded.z,false,true);ticketRegistered=false;}
  if(!ticketRegistered||!target.equals(loaded)){TICKETS.forceChunk(server,this,x,z,true,true);loaded=target;ticketRegistered=true;}
  // Old saves recorded an artificial origin at (0,0); retain only the real entity chunk.
  origin=null;
 }
 public void clearChunkLoader(){if(level() instanceof ServerLevel server){if(origin!=null&&!origin.equals(loaded))TICKETS.forceChunk(server,this,origin.x,origin.z,false,true);if(loaded!=null)TICKETS.forceChunk(server,this,loaded.x,loaded.z,false,true);}origin=null;loaded=null;ticketRegistered=false;}
 protected void saveTicket(net.minecraft.nbt.CompoundTag tag){if(origin!=null)tag.putLong("hbmOriginTicket",origin.toLong());if(loaded!=null)tag.putLong("hbmLoadedTicket",loaded.toLong());}
 protected void loadTicket(net.minecraft.nbt.CompoundTag tag){origin=tag.contains("hbmOriginTicket")?new ChunkPos(tag.getLong("hbmOriginTicket")):null;loaded=tag.contains("hbmLoadedTicket")?new ChunkPos(tag.getLong("hbmLoadedTicket")):null;ticketRegistered=false;}
 @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
}
