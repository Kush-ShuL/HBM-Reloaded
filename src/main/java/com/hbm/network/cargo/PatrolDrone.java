package com.hbm.network.cargo;
import com.hbm.fluid.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.neoforged.neoforge.common.world.chunk.TicketController;
/** True18-slot private-fluid patrol drone and source8-block padded swept chunk footprint. */
public final class PatrolDrone extends CargoDroneBase implements Container {
 private static final EntityDataAccessor<Byte> LOAD=SynchedEntityData.defineId(PatrolDrone.class,EntityDataSerializers.BYTE);public static final TicketController TICKETS=new TicketController(ResourceLocation.parse("hbm:delivery_drone"));
 private final ItemStack[] slots=new ItemStack[18];public FluidStack fluid;private boolean chunkLoading;private final Set<ChunkPos> ticketChunks=new LinkedHashSet<>();
 public PatrolDrone(EntityType<? extends PatrolDrone> type,Level level){super(type,level);Arrays.fill(slots,ItemStack.EMPTY);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(LOAD,(byte)0);}public boolean express(){return entityData.get(LOAD)==1;}public void setExpress(boolean express){entityData.set(LOAD,(byte)(express?1:0));}public boolean chunkLoading(){return chunkLoading;}public void setChunkLoading(){chunkLoading=true;}
 @Override public double getSpeed(){return express()?.375D*3:.375D;}
 @Override public boolean skipAttackInteraction(Entity attacker){if(isRemoved())return false;if(attacker instanceof Player&&!level().isClientSide){for(var stack:slots)if(!stack.isEmpty())spawnAtLocation(stack,1F);int meta=(express()?2:0)+(chunkLoading?1:0);spawnAtLocation(CargoDroneContent.DRONE.get().stack(meta),1F);discard();}return false;}
 @Override public void onAddedToLevel(){super.onAddedToLevel();loadNeighboringChunks();}
 @Override protected void loadNeighboringChunks(){if(!(level() instanceof ServerLevel server)||!chunkLoading||isRemoved())return;var motion=getDeltaMovement();var next=new LinkedHashSet<>(CargoChunkShape.getChunksAlongLineSegment((int)Math.floor(getX()),(int)Math.floor(getZ()),(int)Math.floor(getX()+motion.x),(int)Math.floor(getZ()+motion.z),8));for(var old:List.copyOf(ticketChunks))if(!next.contains(old)){TICKETS.forceChunk(server,this,old.x,old.z,false,true);ticketChunks.remove(old);}for(var chunk:next){TICKETS.forceChunk(server,this,chunk.x,chunk.z,true,true);ticketChunks.add(chunk);}}
 public void clearChunkLoader(){if(level() instanceof ServerLevel server)for(var chunk:ticketChunks)TICKETS.forceChunk(server,this,chunk.x,chunk.z,false,true);ticketChunks.clear();}
 @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);var list=new ListTag();for(int i=0;i<18;i++)if(!slots[i].isEmpty()){var item=(CompoundTag)slots[i].save(registryAccess());item.putByte("Slot",(byte)i);list.add(item);}tag.put("Items",list);if(fluid!=null){tag.putInt("fluidType",fluid.type.getID());tag.putInt("fluidAmount",fluid.fill);}tag.putByte("load",entityData.get(LOAD));tag.putBoolean("chunkLoading",chunkLoading);tag.putLongArray("hbm_drone_tickets",ticketChunks.stream().mapToLong(ChunkPos::toLong).toArray());}
 @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);Arrays.fill(slots,ItemStack.EMPTY);for(var raw:tag.getList("Items",Tag.TAG_COMPOUND)){var item=(CompoundTag)raw;int slot=item.getByte("Slot")&255;if(slot<18)slots[slot]=ItemStack.parseOptional(registryAccess(),item);}if(tag.contains("fluidType")){var type=HbmFluids.fromNameCompat(tag.getString("fluidType"));if(type==HbmFluids.NONE)type=HbmFluids.fromID(tag.getInt("fluidType"));fluid=new FluidStack(type,tag.getInt("fluidAmount"));}entityData.set(LOAD,tag.getByte("load"));chunkLoading=tag.getBoolean("chunkLoading");ticketChunks.clear();for(long chunk:tag.getLongArray("hbm_drone_tickets"))ticketChunks.add(new ChunkPos(chunk));}
 @Override public int getContainerSize(){return 18;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int count){return slots[slot].split(count);}@Override public ItemStack removeItemNoUpdate(int slot){var stack=slots[slot];slots[slot]=ItemStack.EMPTY;return stack;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;stack.limitSize(64);}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);}@Override public void setChanged(){}@Override public boolean stillValid(Player player){return false;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
}
