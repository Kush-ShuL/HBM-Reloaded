package com.hbm.weapon.sedna.gun;

import java.util.*;
import net.minecraft.world.item.ItemStack;

/** Client-only transient source fields follow actual stack UUIDs, including two copies of the same gun item. */
public final class GunVisualState {
    private static final Map<UUID,State> STATES=new LinkedHashMap<>(32,.75F,true){@Override protected boolean removeEldestEntry(Map.Entry<UUID,State> e){return size()>256;}};
    private static final ThreadLocal<ItemStack> CURRENT=new ThreadLocal<>();
    private static final class State{final long[] shots;double random;final List<ItemGunBaseNT.SmokeNode>[] smoke;@SuppressWarnings("unchecked") State(int count){shots=new long[count];Arrays.fill(shots,-1);smoke=new List[count];for(int i=0;i<count;i++)smoke[i]=new ArrayList<>();}}
    public static ItemStack currentStack(){return CURRENT.get();}
    public static Scope bind(ItemStack stack){return new Scope(stack);}
    public static final class Scope implements AutoCloseable{
        private final ItemStack previous;private final ItemGunBaseNT gun;private final State state;private final long[] oldShots;private final double oldRandom;private final GunConfig[] configs;private final List<List<ItemGunBaseNT.SmokeNode>> oldSmoke=new ArrayList<>();
        private Scope(ItemStack stack){previous=CURRENT.get();CURRENT.set(stack);var id=stack==null?null:GunHands.identity(stack);if(stack!=null&&stack.getItem() instanceof ItemGunBaseNT value&&id!=null){gun=value;state=STATES.computeIfAbsent(id,key->new State(gun.getConfigCount()));oldShots=gun.lastShot;oldRandom=gun.shotRand;gun.lastShot=state.shots;gun.shotRand=state.random;configs=new GunConfig[gun.getConfigCount()];for(int i=0;i<configs.length;i++){configs[i]=gun.getConfig(stack,i);oldSmoke.add(configs[i].smokeNodes);configs[i].smokeNodes=state.smoke[i];}}else{gun=null;state=null;oldShots=null;oldRandom=0;configs=null;}}
        @Override public void close(){if(gun!=null){state.random=gun.shotRand;gun.lastShot=oldShots;gun.shotRand=oldRandom;for(int i=0;i<configs.length;i++)configs[i].smokeNodes=oldSmoke.get(i);}if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
    }
    private GunVisualState(){}
}
