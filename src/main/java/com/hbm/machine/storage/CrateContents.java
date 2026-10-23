package com.hbm.machine.storage;

import java.util.Random;
import java.util.random.RandomGenerator;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** The two original ejection RNG sources and the original three hostile cave spiders. */
public final class CrateContents {
    public static void ejectWorld(Level level,double x,double y,double z,Container inventory){eject(level,x,y,z,inventory,new Rolls(){public float nextFloat(){return level.random.nextFloat();}public int nextInt(int max){return level.random.nextInt(max);}public double gaussian(){return level.random.nextGaussian();}},Vec3.ZERO);}
    public static void ejectHeld(Player player,Container inventory){var random=new Random();eject(player.level(),player.getX(),player.getY(),player.getZ(),inventory,new Rolls(){public float nextFloat(){return random.nextFloat();}public int nextInt(int max){return random.nextInt(max);}public double gaussian(){return random.nextGaussian();}},player.getDeltaMovement());}
    private interface Rolls{float nextFloat();int nextInt(int max);double gaussian();}
    private static void eject(Level level,double x,double y,double z,Container inventory,Rolls random,Vec3 velocity){
        if(level.isClientSide)return;
        for(int slot=0;slot<inventory.getContainerSize();slot++){
            var item=inventory.getItem(slot);if(item.isEmpty())continue;float dx=random.nextFloat()*.8F+.1F,dy=random.nextFloat()*.8F+.1F,dz=random.nextFloat()*.8F+.1F;
            while(!item.isEmpty()){
                ItemStack part=item.split(Math.min(item.getCount(),random.nextInt(21)+10));var entity=new ItemEntity(level,x+dx,y+dy,z+dz,part);
                entity.setDeltaMovement((float)random.gaussian()*.05F+velocity.x,(float)random.gaussian()*.05F+.2F+velocity.y,(float)random.gaussian()*.05F+velocity.z);level.addFreshEntity(entity);
            }
        }
    }
    public static void spiders(Player player,double x,double y,double z){if(player.level().isClientSide)return;var random=new Random();for(int i=0;i<3;i++){var spider=EntityType.CAVE_SPIDER.create(player.level());if(spider==null)throw new IllegalStateException("Cannot create original cave spider");spider.moveTo(x+random.nextGaussian()*2,y+1,z+random.nextGaussian()*2,random.nextFloat(),0);spider.setTarget(player);player.level().addFreshEntity(spider);}}
    private CrateContents(){}
}
