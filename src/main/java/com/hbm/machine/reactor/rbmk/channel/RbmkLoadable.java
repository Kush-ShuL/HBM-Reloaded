package com.hbm.machine.reactor.rbmk.channel;
import net.minecraft.world.item.ItemStack;
/** Original IRBMKLoadable contract for the genuine crane/autoloader, not ordinary hopper automation. */
public interface RbmkLoadable { boolean canLoad(ItemStack stack); void load(ItemStack stack); boolean canUnload(); ItemStack provideNext(); void unload(); boolean coldEnoughForAutoloader(); }
