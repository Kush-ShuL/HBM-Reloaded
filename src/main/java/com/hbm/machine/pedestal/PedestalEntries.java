package com.hbm.machine.pedestal;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
/** Original transient per-world aura entries, refreshed each20ticks and expiring after60. */
public final class PedestalEntries {
    public enum Kind{CHARM_OF_PROTECTION,METEORITE_CHARM}
    public record Entry(Kind type,BlockPos pos,long timestamp){}
    private static final Map<ResourceKey<Level>,List<Entry>> ENTRIES=new HashMap<>();
    public static void push(Level level,Kind kind,BlockPos pos){ENTRIES.computeIfAbsent(level.dimension(),k->new ArrayList<>()).add(new Entry(kind,pos.immutable(),level.getGameTime()));}
    public static List<Entry> entries(ResourceKey<Level> dimension){return List.copyOf(ENTRIES.getOrDefault(dimension,List.of()));}
    public static void expire(Level level){if(level.getGameTime()%20!=0)return;var entries=ENTRIES.get(level.dimension());if(entries!=null)entries.removeIf(entry->entry.timestamp()<level.getGameTime()-60);}
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event)->ENTRIES.clear());}
    private PedestalEntries(){}
}
