package com.hbm.entity.mob.fbi;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.ufo.EntityUFOBase;
import com.hbm.bomb.conventional.grenade.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;
/** Source subclass of the unique UFO helper; its actual FRAG/HE/S7 grenade is reused unchanged. */
public final class EntityFBIDrone extends EntityUFOBase implements LegacyTrackingRange {
    private int attackCooldown;
    public EntityFBIDrone(EntityType<? extends EntityFBIDrone> type,Level level){super(type,level);}
    public static AttributeSupplier.Builder attributes(){return net.minecraft.world.entity.monster.Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,35).add(Attributes.MOVEMENT_SPEED,.7).add(Attributes.FOLLOW_RANGE,16);}
    @Override protected void customServerAiStep(){super.customServerAiStep();if(courseChangeCooldown>0)courseChangeCooldown--;if(scanCooldown>0)scanCooldown--;if(attackCooldown>0)attackCooldown--;if(target!=null&&attackCooldown<=0&&Math.abs(getX()-target.getX())<5&&Math.abs(getZ()-target.getZ())<5&&getY()-target.getY()>3){attackCooldown=60;var grenade=new UniversalGrenade(level(),UniversalGrenadeItem.make(GrenadeParts.Shell.FRAG,GrenadeParts.Filling.HE,GrenadeParts.Fuze.S7,null,1));grenade.setThrower(this);grenade.setPos(position());level().addFreshEntity(grenade);}if(courseChangeCooldown>0)approachPosition(target==null?.25:.5);}
    @Override protected int getScanRange(){return 100;}
    @Override protected int targetHeightOffset(){return 7+random.nextInt(4);}
    @Override protected int wanderHeightOffset(){return 7+random.nextInt(4);}
    @Override public int legacyTrackingRange(){return 80;}
}
