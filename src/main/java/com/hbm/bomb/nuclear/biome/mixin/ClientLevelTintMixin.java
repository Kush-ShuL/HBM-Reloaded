package com.hbm.bomb.nuclear.biome.mixin;
import com.hbm.client.bomb.nuclear.biome.ColumnBiomeTintAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.*;
@Mixin(value=ClientLevel.class,remap=false)
public abstract class ClientLevelTintMixin implements ColumnBiomeTintAccess {
    @Shadow @Final private Object2ObjectArrayMap<ColorResolver,BlockTintCache> tintCaches;
    @Override public void hbm$invalidateBiomeTint(int x,int z){tintCaches.values().forEach(cache->cache.invalidateForChunk(x,z));}
}
