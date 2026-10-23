package api.hbm.ntl;
import com.hbm.pneumatic.PneumaticNetwork;
import net.minecraft.world.item.ItemStack;
public interface ISlotMonitorProvider {
 SlotMonitor[] getMonitors();ItemStack getSlotAt(int index);long getAmountAt(int index);long useUpItem(int index,long amount);long addItem(int index,long amount);long setupType(int index,ItemStack zeroStack,long amount);boolean allowTypeSetting();boolean isAvailableToCache(StackCache cache);PneumaticNetwork getRelevantNetwork();
 default void onNewCacheHasJoined(StackCache cache,PneumaticNetwork network){for(SlotMonitor monitor:getMonitors())if(!cache.hasExpired&&isAvailableToCache(cache))cache.addToCache(monitor);}
 default void updateMonitors(){for(SlotMonitor monitor:getMonitors())monitor.checkUpdate();}
}
