package com.hbm.explosion;

import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.registry.HbmContent;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original thermal conversion order, radius tests and entity freezing, adapted to flattened blocks. */
public final class ExplosionThermo {
    private static final Set<String> WOODS = Set.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak");
    private ExplosionThermo() { }
    private interface Conversion { void run(Level world, int x, int y, int z); }

    private static void randomized(Level world,int x,int y,int z,int strength,Conversion conversion) {
        int r=strength*2, r22=r*r/2;
        for(int xx=-r;xx<r;xx++) for(int yy=-r;yy<r;yy++) for(int zz=-r;zz<r;zz++)
            // The source consumes one random draw for every cube cell, including cells outside the sphere.
            if(xx*xx+yy*yy+zz*zz<r22+world.random.nextInt(r22/2)) conversion.run(world,x+xx,y+yy,z+zz);
    }
    public static void freeze(Level world,int x,int y,int z,int strength) { randomized(world,x,y,z,strength,ExplosionThermo::freezeDest); }
    public static void scorch(Level world,int x,int y,int z,int strength) { randomized(world,x,y,z,strength,ExplosionThermo::scorchDest); }
    public static void scorchLight(Level world,int x,int y,int z,int strength) { randomized(world,x,y,z,strength,ExplosionThermo::scorchDestLight); }

    public static void snow(Level world,int x,int y,int z,int bound) {
        int r=bound,r22=r*r/2;
        for(int xx=-r;xx<r;xx++) for(int yy=-r;yy<r;yy++) for(int zz=-r;zz<r;zz++) if(xx*xx+yy*yy+zz*zz<r22) {
            BlockPos pos=new BlockPos(x+xx,y+yy+1,z+zz);BlockState state=world.getBlockState(pos);
            if(Blocks.SNOW.defaultBlockState().canSurvive(world,pos)&&(state.isAir()||state.is(Blocks.FIRE))) world.setBlockAndUpdate(pos,Blocks.SNOW.defaultBlockState());
        }
    }

    private static boolean oldWood(Block block,String suffix) {
        var id=BuiltInRegistries.BLOCK.getKey(block);
        if(!id.getNamespace().equals("minecraft")) return false;
        for(String wood:WOODS) if(id.getPath().equals(wood+suffix)) return true;
        return false;
    }
    private static boolean logs(Block block) { return oldWood(block,"_log")||oldWood(block,"_wood"); }
    private static boolean named(Block block,String symbol) { return BuiltInRegistries.BLOCK.getKey(block).equals(ResourceLocation.fromNamespaceAndPath("hbm",symbol)); }
    private static void replace(Level world,BlockPos pos,Block product) { world.setBlockAndUpdate(pos,product.defaultBlockState()); }
    private static void replace(Level world,BlockPos pos,String product) { replace(world,pos,HbmContent.block(product)); }
    private static boolean oldDirt(Block block) { return block==Blocks.DIRT||block==Blocks.COARSE_DIRT||block==Blocks.PODZOL; }
    private static boolean oldStone(Block block) { return block==Blocks.STONE; }
    private static boolean stoneBricks(Block block) { return block==Blocks.STONE_BRICKS||block==Blocks.MOSSY_STONE_BRICKS||block==Blocks.CRACKED_STONE_BRICKS||block==Blocks.CHISELED_STONE_BRICKS; }

    public static void freezeDest(Level world,int x,int y,int z) {
        BlockPos pos=new BlockPos(x,y,z);Block block=world.getBlockState(pos).getBlock();
        if(block==HbmWorldFluids.block("volcanic_lava_block")) replace(world,pos,Blocks.COBBLESTONE);
        if(block==Blocks.GRASS_BLOCK) replace(world,pos,"frozen_grass");
        if(oldDirt(block)) replace(world,pos,"frozen_dirt");
        if(logs(block)||named(block,"waste_log")) replace(world,pos,"frozen_log");
        if(oldWood(block,"_planks")||named(block,"waste_planks")) replace(world,pos,"frozen_planks");
        if(oldStone(block)||block==Blocks.COBBLESTONE||stoneBricks(block)) replace(world,pos,Blocks.PACKED_ICE);
        if(oldWood(block,"_leaves")) replace(world,pos,Blocks.SNOW_BLOCK);
        if(block==Blocks.LAVA) replace(world,pos,Blocks.OBSIDIAN);
        if(block==Blocks.WATER) replace(world,pos,Blocks.ICE);
    }

    public static void scorchDest(Level world,int x,int y,int z) {
        BlockPos pos=new BlockPos(x,y,z);Block block=world.getBlockState(pos).getBlock();
        if(block==Blocks.GRASS_BLOCK||named(block,"frozen_grass")) replace(world,pos,Blocks.DIRT);
        if(oldDirt(block)) replace(world,pos,Blocks.NETHERRACK);
        if(named(block,"frozen_dirt")) replace(world,pos,Blocks.DIRT);
        if(block==Blocks.NETHERRACK) replace(world,pos,Blocks.LAVA);
        if(logs(block)||named(block,"frozen_log")) replace(world,pos,"waste_log");
        if(oldWood(block,"_planks")||named(block,"frozen_planks")) replace(world,pos,"waste_planks");
        if(oldStone(block)||block==Blocks.COBBLESTONE||stoneBricks(block)||block==Blocks.OBSIDIAN) replace(world,pos,Blocks.LAVA);
        if(oldWood(block,"_leaves")||block==Blocks.WATER||block==Blocks.ICE) replace(world,pos,Blocks.AIR);
        if(block==Blocks.PACKED_ICE) replace(world,pos,Blocks.WATER);
    }

    public static void scorchDestLight(Level world,int x,int y,int z) {
        BlockPos pos=new BlockPos(x,y,z);Block block=world.getBlockState(pos).getBlock();
        if(block==Blocks.GRASS_BLOCK||named(block,"frozen_grass")) replace(world,pos,Blocks.DIRT);
        if(oldDirt(block)||named(block,"waste_earth")) replace(world,pos,Blocks.NETHERRACK);
        if(named(block,"frozen_dirt")) replace(world,pos,Blocks.DIRT);
        if(logs(block)||named(block,"frozen_log")) replace(world,pos,"waste_log");
        if(oldWood(block,"_planks")||named(block,"frozen_planks")) replace(world,pos,"waste_planks");
        if(block==Blocks.OBSIDIAN) replace(world,pos,"gravel_obsidian");
        if(oldWood(block,"_leaves")||block==Blocks.WATER||block==Blocks.ICE) replace(world,pos,Blocks.AIR);
        if(block==Blocks.PACKED_ICE) replace(world,pos,Blocks.WATER);
        if(block==Blocks.SAND||block==Blocks.RED_SAND) replace(world,pos,Blocks.GLASS);
        if(block==Blocks.CLAY) {
            replace(world,pos,Blocks.WHITE_TERRACOTTA);
            var color=DyeColor.byId(world.random.nextInt(16));
            replace(world,pos,BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace(color.getName()+"_terracotta")));
        }
    }

    public static void freezer(Level world,int x,int y,int z,int strength) {
        double radius=strength;int diameter=(int)(strength*2F);
        AABB box=new AABB(Mth.floor(x-radius-1),Mth.floor(y-radius-1),Mth.floor(z-radius-1),Mth.floor(x+radius+1),Mth.floor(y+radius+1),Mth.floor(z+radius+1));
        for(Entity entity:world.getEntities((Entity)null,box)) if(distance(entity,x,y,z)/diameter<=1) {
            double dx=entity.getX()-x,dy=entity.getY()+entity.getEyeHeight()-y,dz=entity.getZ()-z;
            double length=(float)Math.sqrt(dx*dx+dy*dy+dz*dz);
            // Modern cats are the tamed branch of the old EntityOcelot.
            if(length<radius&&!(entity instanceof Ocelot)&&!(entity instanceof Cat)&&entity instanceof LivingEntity living) {
                // The asymmetric 3x3x3 ice cage, and integer truncation, are authored source behavior.
                for(int a=(int)entity.getX()-2;a<(int)entity.getX()+1;a++) for(int b=(int)entity.getY();b<(int)entity.getY()+3;b++) for(int c=(int)entity.getZ()-1;c<(int)entity.getZ()+2;c++)
                    replace(world,new BlockPos(a,b,c),Blocks.ICE);
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,2*60*20,4));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,90*20,2));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,3*60*20,2));
            }
        }
    }
    private static float distance(Entity entity,double x,double y,double z) { return (float)Math.sqrt(entity.distanceToSqr(x,y,z)); }
    private static boolean asbestos(LivingEntity entity) {
        EquipmentSlot[] slots={EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
        String[] names={"asbestos_helmet","asbestos_plate","asbestos_legs","asbestos_boots"};
        for(int i=0;i<slots.length;i++) if(!BuiltInRegistries.ITEM.getKey(entity.getItemBySlot(slots[i]).getItem()).equals(ResourceLocation.fromNamespaceAndPath("hbm",names[i]))) return false;
        return true;
    }
    public static void setEntitiesOnFire(Level world,double x,double y,double z,int radius) {
        for(Entity entity:world.getEntities((Entity)null,new AABB(x-radius,y-radius,z-radius,x+radius,y+radius,z+radius))) if(distance(entity,x,y,z)<=radius) {
            if(entity instanceof Player player&&asbestos(player)) continue;
            if(entity instanceof LivingEntity living) living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,15*20,4));
            entity.igniteForSeconds(10);
        }
    }
}
