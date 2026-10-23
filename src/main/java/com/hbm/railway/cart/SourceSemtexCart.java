package com.hbm.railway.cart;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** The actual source only renders two Semtex parts and drops its corresponding cart. */
public final class SourceSemtexCart extends SourceCartEntity {public SourceSemtexCart(EntityType<?> type,Level level){super(type,level,SourceCartKind.SEMTEX);}}
