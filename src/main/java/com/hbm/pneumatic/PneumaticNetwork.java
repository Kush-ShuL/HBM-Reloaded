package com.hbm.pneumatic;

import api.hbm.ntl.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Literal X5846 transfer/filter/mass/receiver policy over the actual native containers. */
public final class PneumaticNetwork {
 public static final byte SEND_FIRST=0,SEND_LAST=1,SEND_RANDOM=2,RECEIVE_ROBIN=0,RECEIVE_RANDOM=1;public static final int ITEMS_PER_TRANSFER=64;
 public record Receiver(Direction side,long timestamp,PneumaticEndpoint endpoint){}
 public final HashMap<Container,Receiver> receivers=new HashMap<>();public final LinkedHashSet<StackCache> accessors=new LinkedHashSet<>();public final LinkedHashSet<ISlotMonitorProvider> storages=new LinkedHashSet<>();final Set<PneumaticNode> nodes=new LinkedHashSet<>();public final Random rand=new Random();private boolean valid=true;
 /** Original autocrafter is capped to one item per candidate slot operation. Its real future machine opts into this contract. */
 public interface SingleItemReceiver{}
 public boolean isValid(){return valid;}
 public void destroy(){if(!valid)return;valid=false;receivers.clear();for(var cache:accessors)cache.dissolveCache();accessors.clear();storages.clear();for(var node:nodes)if(node.network==this)node.network=null;nodes.clear();}
 public void addReceiver(Container inventory,Direction pipeDir,PneumaticEndpoint endpoint){receivers.put(inventory,new Receiver(pipeDir,System.currentTimeMillis(),endpoint));}
 public void addStackCache(StackCache cache){if(accessors.add(cache))for(var storage:storages)storage.onNewCacheHasJoined(cache,this);}
 private static boolean bad(Container inventory){if(inventory instanceof BlockEntity tile)return tile.isRemoved()||tile.getLevel()==null||!tile.getLevel().hasChunkAt(tile.getBlockPos())||tile.getLevel().getBlockEntity(tile.getBlockPos())!=tile;return false;}
 public void update(){long now=System.currentTimeMillis();receivers.entrySet().removeIf(e->now-e.getValue().timestamp>1000||bad(e.getKey()));accessors.removeIf(cache->cache.hasExpired);}
 public static int getRangeFromPressure(int pressure){return switch(pressure){case 1->10;case 2->25;case 3->100;case 4->250;case 5->1000;default->0;};}
 public static int getIdentifier(int x,int y,int z){return(y+z*27644437)*27644437+x;}
 public static int[] getSlotAccess(Container inventory,Direction side){return inventory instanceof WorldlyContainer sided?sided.getSlotsForFace(side).clone():java.util.stream.IntStream.range(0,inventory.getContainerSize()).toArray();}
 private static boolean accepts(PneumaticEndpoint tube,ItemStack stack){return tube.matchesFilter(stack)==tube.whitelist();}
 private static int square(BlockPos a,BlockPos b){int x=a.getX()-b.getX(),y=a.getY()-b.getY(),z=a.getZ()-b.getZ();return x*x+y*y+z*z;}
 private static int compare(Container a,Container b,BlockPos origin){var p=a instanceof BlockEntity tile?tile.getBlockPos():null;var q=b instanceof BlockEntity tile?tile.getBlockPos():null;if(p==null)return q==null?0:1;if(q==null)return-1;int d=square(p,origin)-square(q,origin);return d!=0?d:getIdentifier(p.getX(),p.getY(),p.getZ())-getIdentifier(q.getX(),q.getY(),q.getZ());}
 public boolean send(Container source,PneumaticEndpoint tube,Direction accessDir,int sendOrder,int receiveOrder,int maxRange,int nextReceiver){update();if(receivers.isEmpty())return false;int[] sourceAccess=getSlotAccess(source,accessDir);if(sendOrder==SEND_LAST)for(int i=0;i<sourceAccess.length/2;i++){int temp=sourceAccess[i];sourceAccess[i]=sourceAccess[sourceAccess.length-1-i];sourceAccess[sourceAccess.length-1-i]=temp;}if(sendOrder==SEND_RANDOM){var shuffle=new Random();for(int i=sourceAccess.length-1;i>0;i--){int j=shuffle.nextInt(i+1),temp=sourceAccess[j];sourceAccess[j]=sourceAccess[i];sourceAccess[i]=temp;}}
  var sourceSided=source instanceof WorldlyContainer sided?sided:null;boolean hasItem=false;for(int slot:sourceAccess){var stack=source.getItem(slot);if(stack.isEmpty()||sourceSided!=null&&!sourceSided.canTakeItemThroughFace(slot,stack,accessDir)||!accepts(tube,stack))continue;hasItem=true;break;}if(!hasItem)return false;
  var list=new ArrayList<>(receivers.entrySet());if(receiveOrder==RECEIVE_ROBIN)list.sort((a,b)->compare(a.getKey(),b.getKey(),tube.pneumaticPosition()));if(receiveOrder==RECEIVE_RANDOM)Collections.shuffle(list);
  var from=source instanceof BlockEntity tile?tile:null;int attempts=0,maxAttempts=Math.min(list.size(),5);while(attempts<maxAttempts){int index=receiveOrder==RECEIVE_ROBIN?(nextReceiver+attempts)%list.size():attempts;var candidate=list.get(index);var dest=candidate.getKey();if(dest==source||bad(dest)){if(bad(dest))receivers.remove(dest);attempts++;continue;}var to=dest instanceof BlockEntity tile?tile:null;if(from!=null&&to!=null&&square(from.getBlockPos(),to.getBlockPos())>maxRange*maxRange){attempts++;continue;}var destSided=dest instanceof WorldlyContainer sided?sided:null;var destSide=candidate.getValue().side.getOpposite();int[] destAccess=getSlotAccess(dest,destSide);int mass=ITEMS_PER_TRANSFER,itemHardCap=dest instanceof SingleItemReceiver?1:ITEMS_PER_TRANSFER;boolean changed=false;
   for(int sourceSlot:sourceAccess){var sourceStack=source.getItem(sourceSlot);if(sourceStack.isEmpty()||sourceSided!=null&&!sourceSided.canTakeItemThroughFace(sourceSlot,sourceStack,accessDir)||!accepts(tube,sourceStack))continue;var endpoint=candidate.getValue().endpoint;if(endpoint!=null&&endpoint!=tube&&!accepts(endpoint,sourceStack))continue;int value=Math.clamp(64/sourceStack.getMaxStackSize(),1,64);
    for(int destSlot:destAccess){var destStack=dest.getItem(destSlot);if(destStack.isEmpty()||!ItemStack.isSameItemSameComponents(sourceStack,destStack))continue;int amount=Math.min(Math.min(sourceStack.getCount(),destStack.getMaxStackSize()-destStack.getCount()),Math.min(Math.min(dest.getMaxStackSize()-destStack.getCount(),mass/value),itemHardCap));if(amount<=0)continue;var check=destStack.copyWithCount(destStack.getCount()+amount);if(!dest.canPlaceItem(destSlot,check)||destSided!=null&&!destSided.canPlaceItemThroughFace(destSlot,check,destSide))continue;sourceStack.shrink(amount);if(sourceStack.isEmpty())source.setItem(sourceSlot,ItemStack.EMPTY);destStack.grow(amount);mass-=amount*value;changed=true;if(mass<=0)break;}
    if(mass>0&&!sourceStack.isEmpty())for(int destSlot:destAccess){if(!dest.getItem(destSlot).isEmpty())continue;int amount=Math.min(Math.min(sourceStack.getCount(),dest.getMaxStackSize()),Math.min(mass/value,itemHardCap));if(amount<=0)continue;var check=sourceStack.copyWithCount(amount);if(!dest.canPlaceItem(destSlot,check)||destSided!=null&&!destSided.canPlaceItemThroughFace(destSlot,check,destSide))continue;sourceStack.shrink(amount);if(sourceStack.isEmpty())source.setItem(sourceSlot,ItemStack.EMPTY);dest.setItem(destSlot,check);mass-=amount*value;changed=true;if(mass<=0)break;}if(mass<=0)break;
   }
   if(changed){source.setChanged();dest.setChanged();return true;}attempts++;
  }return false;
 }
}
