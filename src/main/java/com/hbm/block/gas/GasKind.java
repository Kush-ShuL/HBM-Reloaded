package com.hbm.block.gas;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

public enum GasKind {
    CHLORINE("chlorine_gas",.7F,.8F,.6F,10), RADON("gas_radon",.1F,.8F,.1F,50),
    RADON_DENSE("gas_radon_dense",.1F,.5F,.1F,30), RADON_TOMB("gas_radon_tomb",.1F,.3F,.1F,600),
    MELTDOWN("gas_meltdown",.1F,.4F,.1F,350), MONOXIDE("gas_monoxide",.1F,.1F,.1F,100),
    ASBESTOS("gas_asbestos",.6F,.6F,.5F,50), COAL("gas_coal",.2F,.2F,.2F,20),
    FLAMMABLE("gas_flammable",.8F,.8F,.2F,20), EXPLOSIVE("gas_explosive",.8F,.8F,.2F,20);
    private final String id;
    private final float red,green,blue;
    private final int decayChance;
    GasKind(String id,float red,float green,float blue,int decayChance){this.id=id;this.red=red;this.green=green;this.blue=blue;this.decayChance=decayChance;}
    public String id(){return id;} public float red(){return red;} public float green(){return green;} public float blue(){return blue;}
    public int decayChance(){return decayChance;}
    public boolean flammable(){return this==FLAMMABLE||this==EXPLOSIVE;}
    public int delay(RandomSource random){return flammable()?random.nextInt(5)+16:2;}
    public Direction firstDirection(RandomSource random){
        return switch(this){
            case MONOXIDE -> Direction.DOWN;
            case ASBESTOS,COAL -> random.nextInt(5)==0?Direction.DOWN:Direction.from3DDataValue(random.nextInt(6));
            case RADON_TOMB -> random.nextInt(3)==0?Direction.UP:Direction.DOWN;
            case MELTDOWN -> random.nextInt(2)==0?Direction.UP:Direction.DOWN;
            case FLAMMABLE,EXPLOSIVE -> random.nextInt(3)==0?Direction.from3DDataValue(random.nextInt(2)):horizontal(random);
            default -> random.nextInt(5)==0?Direction.UP:Direction.DOWN;
        };
    }
    public static Direction horizontal(RandomSource random){return Direction.from3DDataValue(random.nextInt(4)+2);}
}
