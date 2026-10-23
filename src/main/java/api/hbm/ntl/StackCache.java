package api.hbm.ntl;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
/** Original per-accessor uncapped-long aggregate, always references real provider storage. */
public final class StackCache {
 public final int x,y,z;public boolean hasExpired;public final LinkedHashMap<Long,CacheSlot> cacheSlots=new LinkedHashMap<>();
 public StackCache(int x,int y,int z){this.x=x;this.y=y;this.z=z;}
 public void addToCache(SlotMonitor monitor){long identity=getStackIdentity(monitor.toZeroStack());cacheSlots.computeIfAbsent(identity,id->new CacheSlot(monitor.toZeroStack())).addMonitor(monitor);}
 public CacheSlot getSlotFromStack(ItemStack stack){return cacheSlots.get(getStackIdentity(stack));}
 public long consumeItemsAndReturnQuantity(ItemStack stack,long amount){long identity=getStackIdentity(stack);var cache=cacheSlots.get(identity);if(cache==null)return 0;long original=amount;for(var monitor:cache.monitors){if(getStackIdentity(monitor.parent.getSlotAt(monitor.index))!=identity)continue;amount=monitor.parent.useUpItem(monitor.index,amount);if(amount<=0)break;}return original-amount;}
 public long addItemsAndReturnQuantity(ItemStack stack,long amount){long identity=getStackIdentity(stack);var cache=cacheSlots.get(identity);if(cache!=null)for(var monitor:cache.monitors){if(getStackIdentity(monitor.parent.getSlotAt(monitor.index))!=identity)continue;amount=monitor.parent.addItem(monitor.index,amount);if(amount<=0)break;}if(amount>0){var empty=cacheSlots.get(getNullIdentity());if(empty!=null)for(var monitor:empty.monitors){if(!monitor.parent.allowTypeSetting()||!monitor.parent.getSlotAt(monitor.index).isEmpty())continue;amount=monitor.parent.setupType(monitor.index,stack,amount);if(amount<=0)break;}}return amount;}
 public void dissolveCache(){for(var slot:cacheSlots.values())slot.destroy();cacheSlots.clear();hasExpired=true;}
 public final class CacheSlot {
  public final ItemStack displayStack;public final int itemId,meta;public long stacksize;public final LinkedHashSet<SlotMonitor> monitors=new LinkedHashSet<>();
  public CacheSlot(ItemStack stack){displayStack=stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1);itemId=stack.isEmpty()?0:BuiltInRegistries.ITEM.getId(stack.getItem());meta=stack.isEmpty()?0:stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());stacksize=0;}
  public void addMonitor(SlotMonitor monitor){if(monitors.add(monitor)){monitor.viewedBy.add(this);changeAmounts(monitor.stacksize);}}
  public void removeMonitor(SlotMonitor monitor){if(monitors.remove(monitor))changeAmounts(-monitor.stacksize);}
  public void destroy(){for(var monitor:monitors)monitor.viewedBy.remove(this);stacksize=0;}
  public void changeAmounts(long delta){stacksize+=delta;}public StackCache getStackCache(){return StackCache.this;}public void reCount(){stacksize=0;for(var monitor:monitors)stacksize+=monitor.stacksize;}
 }
 public static long getNullIdentity(){return 0;}
 public static long getStackIdentity(ItemStack stack){if(stack==null||stack.isEmpty())return 0;long identity=BuiltInRegistries.ITEM.getId(stack.getItem())*27644437;identity+=stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());identity*=27644437;identity+=stack.getComponents().hashCode();return identity;}
}
