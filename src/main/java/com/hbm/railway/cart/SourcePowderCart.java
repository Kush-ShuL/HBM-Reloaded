package com.hbm.railway.cart;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** The actual source has a Powder load, not an ignition/activator/explosion callback. */
public final class SourcePowderCart extends SourceCartEntity {public SourcePowderCart(EntityType<?> type,Level level){super(type,level,SourceCartKind.POWDER);}}
