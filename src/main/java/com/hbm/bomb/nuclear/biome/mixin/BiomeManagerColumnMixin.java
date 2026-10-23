package com.hbm.bomb.nuclear.biome.mixin;

import com.hbm.bomb.nuclear.biome.ColumnBiomes;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=BiomeManager.class,remap=false)
public abstract class BiomeManagerColumnMixin {
    @Shadow @Final private BiomeManager.NoiseBiomeSource noiseBiomeSource;
    @Unique private Level hbm$level(){return noiseBiomeSource instanceof Level level?level:noiseBiomeSource instanceof ServerLevelAccessor accessor?accessor.getLevel():null;}
    @Inject(method="getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",at=@At("HEAD"),cancellable=true)
    private void hbm$blockBiome(BlockPos pos,CallbackInfoReturnable<Holder<Biome>> result){var level=hbm$level();if(level!=null){var biome=ColumnBiomes.biome(level,pos);if(biome!=null)result.setReturnValue(biome);}}
    @Inject(method="getNoiseBiomeAtPosition(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",at=@At("HEAD"),cancellable=true)
    private void hbm$blockNoise(BlockPos pos,CallbackInfoReturnable<Holder<Biome>> result){hbm$noise(pos.getX(),pos.getY(),pos.getZ(),result);}
    @Inject(method="getNoiseBiomeAtPosition(DDD)Lnet/minecraft/core/Holder;",at=@At("HEAD"),cancellable=true)
    private void hbm$positionNoise(double x,double y,double z,CallbackInfoReturnable<Holder<Biome>> result){hbm$noise(x,y,z,result);}
    @Unique private void hbm$noise(double x,double y,double z,CallbackInfoReturnable<Holder<Biome>> result){var level=hbm$level();if(level!=null){var biome=ColumnBiomes.noiseBiome(level,x,y,z);if(biome!=null)result.setReturnValue(biome);}}
}
