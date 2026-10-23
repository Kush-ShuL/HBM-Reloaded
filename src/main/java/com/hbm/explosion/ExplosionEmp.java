package com.hbm.explosion;

import com.hbm.energy.EnergyHandler;
import com.hbm.registry.HbmContent;
import java.math.BigInteger;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

/** Original ExplosionNukeGeneric EMP branch, including independent HE/RF scrap rolls. */
public final class ExplosionEmp {
    private static final Random random=new Random();
    private static final Direction[] SIDES={Direction.UP,Direction.DOWN,Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST};
    private ExplosionEmp() { }
    public static void empBlast(Level world,int x,int y,int z,int strength) {
        int r22=strength*strength/2;
        for(int xx=-strength;xx<strength;xx++) for(int yy=-strength;yy<strength;yy++) for(int zz=-strength;zz<strength;zz++)
            if(xx*xx+yy*yy+zz*zz<r22) emp(world,x+xx,y+yy,z+zz);
    }
    public static void emp(Level world,int x,int y,int z) {
        if(world.isClientSide) return;
        BlockPos pos=new BlockPos(x,y,z);
        // Compat.getTileStandard in the source never loaded missing chunks.
        if(!world.hasChunkAt(pos)) return;
        var machine=world.getBlockEntity(pos);
        if(machine==null) return;
        // Capture the foreign provider before the native scrap replacement removes the TE.
        var foreign=new java.util.ArrayList<net.neoforged.neoforge.energy.IEnergyStorage>();
        for(Direction side:SIDES) {
            var storage=world.getCapability(Capabilities.EnergyStorage.BLOCK,pos,side);
            foreign.add(storage!=null&&storage.canExtract()?storage:null);
        }
        if(machine instanceof EnergyHandler storage) {
            storage.setPowerExact(BigInteger.ZERO);
            if(random.nextInt(5)<1) world.setBlockAndUpdate(pos,HbmContent.block("block_electrical_scrap").defaultBlockState());
        }
        boolean provider=false;
        for(var storage:foreign) if(storage!=null) { provider=true;storage.extractEnergy(storage.getEnergyStored(),false); }
        if(provider&&random.nextInt(5)<=1) world.setBlockAndUpdate(pos,HbmContent.block("block_electrical_scrap").defaultBlockState());
    }
}
