package com.hbm.block.barbedwire;
import com.hbm.bomb.conventional.MistExposure;
import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.ExposureEffects;
import com.hbm.machine.pedestal.SourceBarbedWireBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
/** Original five BarbedWire identity branches, sharing the real ordinary wire's web/motion/collision/placement. */
public final class SpecialBarbedWireBlock extends SourceBarbedWireBlock {
    public enum Kind {FIRE,POISON,ACID,WITHER,ULTRADEATH;public String id(){return "barbed_wire_"+name().toLowerCase(java.util.Locale.ROOT);}}
    public final Kind kind;
    public SpecialBarbedWireBlock(Kind kind){this.kind=kind;}
    @Override protected void hurtTouch(Entity entity){
        if(kind==Kind.ULTRADEATH){
            entity.hurt(HbmDamageSources.source(entity.level(),HbmDamageSources.pc),5F);
            if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,100,9));
            return;
        }
        super.hurtTouch(entity);
        switch(kind){
            case FIRE->entity.igniteForSeconds(1);
            case POISON->{if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(MobEffects.POISON,100,2));}
            case ACID->{if(entity instanceof Player player)for(int slot=0;slot<4;slot++)MistExposure.damageSuit(player,slot,1);}
            case WITHER->{if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(MobEffects.WITHER,100,4));}
            default->throw new IllegalStateException("Handled source branch "+kind);
        }
    }
}
