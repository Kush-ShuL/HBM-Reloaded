package com.hbm.machine.teleport;

import com.hbm.satellite.SatelliteDimensions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** Original numeric dimension NBT remains authoritative; modern unnumbered keys are stored explicitly, never guessed. */
final class TeleportDimensions {
    static final String KEY="hbmDestinationDimension";
    static void record(CompoundTag tag,ResourceKey<Level> dimension){
        Integer source=SatelliteDimensions.sourceId(dimension);
        if(source!=null){tag.putInt("dim",source);tag.remove(KEY);}
        else{tag.remove("dim");tag.putString(KEY,dimension.location().toString());}
    }
    static ResourceKey<Level> resolve(int source,String modern){
        if(modern.isEmpty())return SatelliteDimensions.resolve(source);
        var id=ResourceLocation.tryParse(modern);
        return id==null?null:ResourceKey.create(Registries.DIMENSION,id);
    }
    private TeleportDimensions(){}
}
