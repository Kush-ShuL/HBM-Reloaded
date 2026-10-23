package com.hbm.environment.impact;

import com.hbm.config.GeneralModes;
import com.hbm.saveddata.TomSaveData;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Transient read-only views of the ONE dimension-local impactData; generation workers never access SavedData storage. */
public record ImpactState(float fire,float dust,boolean impact) {
    public static final ImpactState CLEAR=new ImpactState(0,0,false);
    private static final Map<ServerLevel,ImpactState> GENERATION=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Set<Level> SYNCED_CLIENTS=Collections.newSetFromMap(new WeakHashMap<>());
    public static ImpactState publish(ServerLevel world){var data=TomSaveData.forWorld(world);var state=new ImpactState(data.fire,data.dust,data.impact);GENERATION.put(world,state);return state;}
    public static ImpactState generation(ServerLevel world){return GENERATION.getOrDefault(world,CLEAR);}
    public static ImpactState get(Level world){
        if(world instanceof ServerLevel server){var data=TomSaveData.forWorld(server);return new ImpactState(data.fire,data.dust,data.impact);}
        synchronized(SYNCED_CLIENTS){if(!SYNCED_CLIENTS.contains(world))return CLEAR;}
        var data=TomSaveData.forWorld(world);return new ImpactState(data.fire,data.dust,data.impact);
    }
    public static void receive(Level world,ImpactSync packet){if(!world.dimension().location().equals(packet.dimension()))return;var data=TomSaveData.forWorld(world);data.fire=packet.fire();data.dust=packet.dust();data.impact=packet.impact();synchronized(SYNCED_CLIENTS){SYNCED_CLIENTS.add(world);}}
    public static boolean provider(Level world){return world.dimension()==Level.OVERWORLD&&(world.isClientSide?GeneralModes.localFlag("1.32_enableImpactWorldProvider"):GeneralModes.flag("1.32_enableImpactWorldProvider"));}
    public static boolean skyboxes(){return GeneralModes.localFlag("1.31_enableSkyboxes");}
    public boolean active(){return fire>0||dust>0;}
    public static void forget(Level world){if(world instanceof ServerLevel server)GENERATION.remove(server);synchronized(SYNCED_CLIENTS){SYNCED_CLIENTS.remove(world);}ImpactGeneration.forget(world);}
    public static void clear(){GENERATION.clear();synchronized(SYNCED_CLIENTS){SYNCED_CLIENTS.clear();}ImpactGeneration.clear();}
}
