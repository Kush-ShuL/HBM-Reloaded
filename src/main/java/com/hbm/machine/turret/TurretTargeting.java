package com.hbm.machine.turret;

import java.util.*;
import java.util.function.BiFunction;
import net.minecraft.world.entity.Entity;

/** Original CompatExternal turret categories and tri-state callbacks, for actual external entity classes. */
public final class TurretTargeting {
    public static final List<Class<? extends Entity>> blacklist=new ArrayList<>(),friendly=new ArrayList<>(),hostile=new ArrayList<>(),machines=new ArrayList<>(),players=new ArrayList<>();
    public static final Map<Class<? extends Entity>,BiFunction<Entity,Object,Integer>> conditions=new HashMap<>();
    static boolean contains(List<Class<? extends Entity>> classes,Entity entity){for(var type:classes)if(type.isInstance(entity))return true;return false;}
    private TurretTargeting(){}
}
