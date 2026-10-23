package com.hbm.world.structure.loot;
import com.hbm.machine.storage.LockableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Converter supplies the real rotated replacement state; source marker fields remain unchanged. */
public final class StructureLootMarkers {
    public static boolean ready(WorldGenLevel world,BlockPos pos,BlockState replacement,CompoundTag marker){if(replacement.is(StructureLootContent.PILE.get()))return StructureLootPresets.ready(world.getLevel(),marker.getString("pool"));if(!(replacement.getBlock() instanceof EntityBlock host))return false;var entity=host.newBlockEntity(pos,replacement);return entity instanceof Container&&LegacyStructureLoot.ready(world.getLevel(),marker.getString("pool"));}
    public static void replace(WorldGenLevel world,BlockPos pos,BlockState replacement,CompoundTag marker,RandomSource random){if(!ready(world,pos,replacement,marker))throw new IllegalStateException("Incomplete source loot marker replacement: "+marker);world.setBlock(pos,replacement,2);var entity=world.getBlockEntity(pos);if(entity instanceof LockableBlockEntity lock&&marker.getInt("lockCode")!=0){lock.setPins(marker.getInt("lockCode"));lock.setMod(marker.getFloat("lockMod"));lock.cheesable=marker.getFloat("lockMod")!=0;lock.lock();}if(entity instanceof Container container){int amount=LegacyStructureLoot.markerAmount(marker.getInt("min"),marker.getInt("max"),random);LegacyStructureLoot.fill(world.getLevel(),Vec3.atCenterOf(pos),container,marker.getString("pool"),amount,random);}else if(entity instanceof LootPileBlockEntity)StructureLootPresets.preset(world,pos,marker.getString("pool"),random);else throw new IllegalStateException("Real source marker host has no actual container/pile at "+pos);if(entity!=null)entity.setChanged();}
    private StructureLootMarkers(){}
}
