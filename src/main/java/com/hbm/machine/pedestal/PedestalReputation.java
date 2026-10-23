package com.hbm.machine.pedestal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
/** The actual source HbmPlayerProps/reputation field, retained in persistent player data for future lantern callers. */
public final class PedestalReputation {
    public static int get(Player player){return player.getPersistentData().getCompound("HbmPlayerProps").getInt("reputation");}
    public static void set(Player player,int reputation){var props=player.getPersistentData().getCompound("HbmPlayerProps");props.putInt("reputation",reputation);player.getPersistentData().put("HbmPlayerProps",props);}
    public static void register(){NeoForge.EVENT_BUS.addListener((PlayerEvent.Clone event)->set(event.getEntity(),get(event.getOriginal())));}
    private PedestalReputation(){}
}
