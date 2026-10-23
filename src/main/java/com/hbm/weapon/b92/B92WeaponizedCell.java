package com.hbm.weapon.b92;
import java.util.List;import net.minecraft.core.particles.*;import net.minecraft.network.chat.Component;import net.minecraft.world.entity.item.ItemEntity;import net.minecraft.world.item.*;
/** Exact source50s/onfire rigged-cell fuse; dropStar suppresses blast but not the consumed item. */
public final class B92WeaponizedCell extends Item {
 public B92WeaponizedCell(){super(new Properties().stacksTo(1));}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){var level=entity.level();if((entity.tickCount>1000||entity.isOnFire())&&!level.isClientSide){if(B92Settings.droppedStar())B92Blast.explode(level,entity.getX(),entity.getY(),entity.getZ(),100);entity.discard();}int randy=Math.max(1000-entity.tickCount,1);var random=level.random;level.addParticle(random.nextInt(1000)>=randy?DustParticleOptions.REDSTONE:ParticleTypes.SMOKE,entity.getX()+random.nextGaussian()*entity.getBbWidth()/2,entity.getY()+random.nextGaussian()*entity.getBbHeight(),entity.getZ()+random.nextGaussian()*entity.getBbWidth()/2,0,0,0);if(randy<100)level.addParticle(ParticleTypes.LAVA,entity.getX()+random.nextGaussian()*entity.getBbWidth()/2,entity.getY()+random.nextGaussian()*entity.getBbHeight(),entity.getZ()+random.nextGaussian()*entity.getBbWidth()/2,0,0,0);return false;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.weaponized_starblaster_cell.name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.b92.weaponized.1"));lines.add(Component.translatable("tooltip.hbm.b92.weaponized.2"));}
}
