package com.hbm.machine.tesla;

import com.hbm.armor.HazmatRegistry;
import com.hbm.damage.HbmDamageSources;
import com.hbm.machine.tesla.mixin.CreeperPowerAccess;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;

/** Original Tesla.zap and ArmorUtil Faraday rules, shared with future real tesla weapons. */
public final class TeslaDischarge {
    private static final String[] METALS={"chainmail","iron","silver","gold","platinum","tin","lead","liquidator","schrabidium","euphemium","steel","cmb","titanium","alloy","copper","bronze","electrum","t45","t51","bj","starmetal","hazmat","rubber","hev","ajr","rpa","spacesuit"};
    private TeslaDischarge(){}
    public static boolean faradayArmor(ItemStack armor,LivingEntity holder){
        if(armor.isEmpty())return false;
        String name=armor.getDescriptionId().toLowerCase(Locale.US);
        for(String metal:METALS)if(name.contains(metal))return true;
        return HazmatRegistry.getCladding(armor,holder.level().registryAccess())>0;
    }
    public static boolean hasFaradaySuit(Player player){for(EquipmentSlot slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD})if(!faradayArmor(player.getItemBySlot(slot),player))return false;return true;}
    public static List<Vec3> zap(Level level,Vec3 origin,double radius,Entity source){
        var all=level.getEntitiesOfClass(LivingEntity.class,new AABB(origin.x-radius,origin.y-radius,origin.z-radius,origin.x+radius,origin.y+radius,origin.z+radius));
        var result=new ArrayList<Vec3>();
        for(LivingEntity victim:all){
            if(victim instanceof Ocelot||victim==source)continue;
            Vec3 center=new Vec3(victim.getX(),victim.getY()+victim.getBbHeight()/2,victim.getZ());
            if(center.distanceTo(origin)>TeslaBlockEntity.RANGE)continue;
            // A block Tesla has no source entity. Its original world-only LOS
            // query uses no entity collision state, including for crab emitters.
            if(level.clip(new ClipContext(origin,center,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,CollisionContext.empty())).getType()!=HitResult.Type.MISS)continue;
            // Canonical IDs from X5846 EntityMappings; no substitute crab entities are registered.
            var type=BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());String crab=type.getNamespace().equals("hbm")?type.getPath():"";
            if(crab.equals("entity_taint_crab")){result.add(new Vec3(victim.getX(),victim.getY()+1.25,victim.getZ()));victim.heal(15);continue;}
            if(crab.equals("entity_tesla_crab")){result.add(new Vec3(victim.getX(),victim.getY()+1,victim.getZ()));victim.heal(10);continue;}
            if(crab.equals("entity_cyber_crab")){result.add(center);continue;}
            if(victim instanceof Creeper creeper){creeper.getEntityData().set(CreeperPowerAccess.hbm$powered(),true);result.add(center);continue;}
            if(!(victim instanceof Player player&&hasFaradaySuit(player)))if(victim.hurt(HbmDamageSources.source(level,HbmDamageSources.electricity),Mth.clamp(victim.getMaxHealth()*.5F,3,20)/all.size()))level.playSound(null,victim.getX(),victim.getY(),victim.getZ(),HbmSounds.get("weapon.tesla"),victim.getSoundSource(),1,1);
            result.add(source!=null&&victim instanceof Player&&level.isClientSide?center.add(0,-victim.getBbHeight(),0):center);
        }
        return List.copyOf(result);
    }
}
