package com.hbm.client.annihilator;

import com.hbm.machine.annihilator.AnnihilatorRecipeSync;
import com.hbm.machine.annihilator.AnnihilatorRecipes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Main-thread application of the server table and real optional viewer callbacks. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AnnihilatorRecipeSyncClient {
    private static final Map<String,Runnable> VIEWERS=new LinkedHashMap<>();
    public static void registerViewer(String id,Runnable refresh){VIEWERS.put(id,refresh);}
    public static void removeViewer(String id){VIEWERS.remove(id);}
    private static void refresh(){for(var callback:java.util.List.copyOf(VIEWERS.values()))callback.run();}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->AnnihilatorRecipeSync.CLIENT=packet->{
        var recipes=new HashMap<AnnihilatorRecipes.Key,java.util.List<AnnihilatorRecipes.Milestone>>();
        for(var row:packet.rows())recipes.put(row.key(),row.milestones());
        // Integrated server and client already share this canonical static map.
        // Do not install a remote-client mode guard into its live server state.
        if(net.minecraft.client.Minecraft.getInstance().getSingleplayerServer()==null)AnnihilatorRecipes.applyServerSnapshot(recipes);
        refresh();
        // The payload handler enqueued this callback onto the client main thread.
        // ACK only after the canonical map and loaded optional viewers are updated.
        PacketDistributor.sendToServer(new AnnihilatorRecipeSync.Ack(packet.request()));
    });}
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT) public static final class Events {
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){if(AnnihilatorRecipes.restoreLocalSnapshot())refresh();}
    }
    private AnnihilatorRecipeSyncClient(){}
}
