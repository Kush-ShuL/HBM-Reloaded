package api.hbm.ntl;
import java.util.*;
import net.minecraft.world.item.*;
/** Source live slot monitoring: changes of type/availability and long quantity notify exactly the subscribed aggregates. */
public final class SlotMonitor {
 public final int index;public final ISlotMonitorProvider parent;public final LinkedHashSet<StackCache.CacheSlot> viewedBy=new LinkedHashSet<>();public Item item;public long stacksize;public int meta;private ItemStack template=ItemStack.EMPTY;protected boolean hasAvailabilityChanged=true,forceTypeUpdate=true;
 public SlotMonitor(int index,ISlotMonitorProvider parent){this.index=index;this.parent=parent;}
 /** Modern count-zero ItemStacks erase identity, so the original zero-count template is represented by a one-count identity copy. Aggregates start at zero independently. */
 public ItemStack toZeroStack(){return template.isEmpty()?ItemStack.EMPTY:template.copyWithCount(1);}
 public void availabilityHasChanged(){hasAvailabilityChanged=true;}
 public void checkUpdate(){var network=parent.getRelevantNetwork();if(hasAvailabilityChanged){if(network!=null)for(var cache:network.accessors)if(!cache.hasExpired&&parent.isAvailableToCache(cache))cache.addToCache(this);var it=viewedBy.iterator();while(it.hasNext()){var slot=it.next();var cache=slot.getStackCache();if(cache.hasExpired||!parent.isAvailableToCache(cache)){slot.removeMonitor(this);it.remove();}}hasAvailabilityChanged=false;}
  var stack=parent.getSlotAt(index);long amount=parent.getAmountAt(index);boolean changed=stack.isEmpty()!=template.isEmpty()||!stack.isEmpty()&&!ItemStack.isSameItemSameComponents(stack,template);if(changed||forceTypeUpdate){var it=viewedBy.iterator();while(it.hasNext()){it.next().removeMonitor(this);it.remove();}if(stack.isEmpty()){template=ItemStack.EMPTY;item=null;stacksize=0;meta=0;}else{template=stack.copyWithCount(1);item=stack.getItem();stacksize=amount;meta=stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}if(network!=null)for(var cache:network.accessors)if(!cache.hasExpired&&parent.isAvailableToCache(cache))cache.addToCache(this);forceTypeUpdate=false;return;}if(stacksize!=amount){long delta=amount-stacksize;for(var slot:viewedBy)slot.changeAmounts(delta);stacksize=amount;}
 }
}
