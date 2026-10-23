package com.hbm.heat;
import net.minecraft.world.item.ItemStack;
/** Original optional FireboxBase -> Ashpit hook. The actual ashpit owns its three persisted ash counters. */
public interface AshReceiver {void addFuelAsh(ItemStack fuel,int sourceBurnTime);}
