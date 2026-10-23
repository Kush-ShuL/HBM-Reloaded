package com.hbm.explosion;
import com.hbm.registry.HbmContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
/** Required HBM block dependencies resolve only when an explosion uses that branch. */
final class LegacyExplosionBlocks {
 private LegacyExplosionBlocks(){}
 static Block require(String symbol){try{return HbmContent.block(symbol);}catch(IllegalArgumentException absent){}var key=ResourceLocation.fromNamespaceAndPath("hbm",symbol);if(!BuiltInRegistries.BLOCK.containsKey(key))throw new IllegalStateException("Explosion requires original HBM block not yet ported: "+symbol);return BuiltInRegistries.BLOCK.get(key);}
}
