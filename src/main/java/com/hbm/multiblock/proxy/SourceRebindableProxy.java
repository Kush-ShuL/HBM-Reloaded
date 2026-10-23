package com.hbm.multiblock.proxy;
import net.minecraft.world.level.block.entity.BlockEntity;
/** Finite real source proxy marker; default fallback is the actual existing Dummyable core lookup. */
public interface SourceRebindableProxy {
 default BlockEntity sourceProxyFallbackTarget(){return SourceProxyBindings.originalCore((BlockEntity)this);}
}
