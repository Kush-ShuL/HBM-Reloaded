package com.hbm.machine.reactor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
/** Source EntityItemWaste, including its unconditional damage immunity. */
public final class WastePlateEntity extends ItemEntity {
    public WastePlateEntity(EntityType<? extends WastePlateEntity> type,Level level){super(type,level);lifespan=Integer.MAX_VALUE;}
    public WastePlateEntity(Level level,double x,double y,double z,ItemStack stack){this(ReactorContent.WASTE_ENTITY.get(),level);setPos(x,y,z);setItem(stack);setUnlimitedLifetime();}
    @Override public boolean isInvulnerableTo(DamageSource source){return true;}
    @Override public boolean hurt(DamageSource source,float amount){return false;}
}
