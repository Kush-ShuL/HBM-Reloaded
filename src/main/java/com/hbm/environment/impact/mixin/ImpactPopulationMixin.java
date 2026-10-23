package com.hbm.environment.impact.mixin;
import com.hbm.environment.impact.ImpactState;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=NaturalSpawner.class,remap=false)
public abstract class ImpactPopulationMixin {
    @Inject(method="spawnMobsForChunkGeneration",at=@At("HEAD"),cancellable=true)
    private static void hbm$denyImpactAnimals(ServerLevelAccessor region,Holder<Biome> biome,ChunkPos chunk,RandomSource random,CallbackInfo ci){if(ImpactState.generation(region.getLevel()).impact())ci.cancel();}
}
