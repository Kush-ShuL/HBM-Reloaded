package com.hbm.bomb.conventional.grenade;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Source ItemGrenadeDynamite: timed fragmentation-like damage-only blast, with no block allocator. */
public class DynamiteGrenadeItem extends Item {
    private static final net.minecraft.util.RandomSource ITEM_RANDOM=net.minecraft.util.RandomSource.create();
    public DynamiteGrenadeItem(){super(new Properties().stacksTo(16));}
    public int fuseTicks(){return 60;}
    public double bounce(){return .5D;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.getAbilities().instabuild)stack.shrink(1);level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.ARROW_SHOOT,SoundSource.PLAYERS,.5F,.4F/(ITEM_RANDOM.nextFloat()*.4F+.8F));if(!level.isClientSide)level.addFreshEntity(new GenericGrenade(level,player).setType(this));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    public void explode(GenericGrenade grenade,LivingEntity owner){new ExplosionVNT(grenade.level(),grenade.getX(),grenade.getY(),grenade.getZ(),5,owner).setEntityProcessor(new EntityProcessorCrossSmooth(1,15)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1)).explode();}
}
