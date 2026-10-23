package com.hbm.entity.mob.duck;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Original nonfood ItemPeas consumes one even if the 50-block query finds no boss. */
public final class PeasItem extends Item {
    public PeasItem(){super(new Properties());}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(!player.getAbilities().instabuild)stack.shrink(1);
        for(var boss:level.getEntitiesOfClass(Quackos.class,player.getBoundingBox().inflate(50)))boss.sourceDespawn();
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
