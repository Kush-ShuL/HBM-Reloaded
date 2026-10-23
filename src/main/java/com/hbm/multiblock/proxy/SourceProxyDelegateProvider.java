package com.hbm.multiblock.proxy;
import net.minecraft.core.BlockPos;
/** Exact source ProxyDyn.IProxyDelegateProvider: null keeps the actual core object. */
public interface SourceProxyDelegateProvider { Object sourceProxyDelegate(BlockPos position); }
