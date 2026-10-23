package com.hbm.machine.radar;
import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;
/** Pending real SatelliteSavedData/Base/relay group binds its actual existing repository here, never a fabricated satellite. */
public final class RadarSatelliteBridge{public interface Backend{boolean isRelay(ItemStack stack);void onCoordinate(Level level,Player player,ItemStack relay,int x,int y,int z);}public static Backend backend;private RadarSatelliteBridge(){}}
