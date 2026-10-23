package com.hbm.entity.mob.crab;
import com.hbm.machine.tesla.TeslaDischarge;
import java.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Original EntityTeslaCrab remains a CyberCrab and evaluates the real shared Tesla zap on both sides. */
public final class TeslaCrab extends CyberCrab {
    public List<Vec3> targets=new ArrayList<>();
    public TeslaCrab(EntityType<? extends TeslaCrab> type,Level level){super(type,level);noCulling=true;}
    public static AttributeSupplier.Builder attributes(){return CyberCrab.attributes().add(Attributes.MAX_HEALTH,10).add(Attributes.MOVEMENT_SPEED,.5);}
    @Override public void aiStep(){targets=TeslaDischarge.zap(level(),new Vec3(getX(),getY()+1,getZ()),3,this);super.aiStep();}
}
